package eu.rafareborn.biometricbypass.hook

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Button
import eu.rafareborn.biometricbypass.TAG
import eu.rafareborn.biometricbypass.module
import java.lang.reflect.Field

object BiometricBypassHook {
    private const val TARGET_CLASS = "com.android.systemui.biometrics.AuthContainerView"
    private const val TARGET_METHOD = "onDialogAnimatedIn"
    private const val BUTTON_CONFIRM_ID = "button_confirm"
    private const val MAX_ATTEMPTS = 3
    private const val INITIAL_DELAY_MS = 100L

    private var configField: Field? = null
    private var opPackageNameField: Field? = null

    @Volatile
    private var confirmButtonId: Int = 0

    fun hook(classLoader: ClassLoader) {
        val targetClass = classLoader.loadClass(TARGET_CLASS)
        val targetMethod = targetClass.getDeclaredMethod(TARGET_METHOD)

        configField =
            runCatching {
                targetClass.getDeclaredField("mConfig").apply { isAccessible = true }
            }.onFailure {
                module.log(Log.WARN, TAG, "reflect miss field=mConfig", it)
            }.getOrNull()

        opPackageNameField =
            runCatching {
                configField?.type?.getDeclaredField("mOpPackageName")?.apply { isAccessible = true }
            }.onFailure {
                module.log(Log.WARN, TAG, "reflect miss field=mOpPackageName", it)
            }.getOrNull()

        module.hook(targetMethod).intercept { chain ->
            chain.proceed()

            val authContainerView = chain.thisObject as? View ?: return@intercept null

            if (confirmButtonId == 0) {
                val context = authContainerView.context

                @SuppressLint("DiscouragedApi")
                val id =
                    context.resources.getIdentifier(BUTTON_CONFIRM_ID, "id", context.packageName)
                confirmButtonId = id
            }
            val buttonId = confirmButtonId
            val opPackageName = readOpPackageName(authContainerView)

            if (buttonId == 0) {
                module.log(Log.WARN, TAG, "button missing id pkg=$opPackageName")
                return@intercept null
            }

            authContainerView.post {
                clickWithRetry(authContainerView, buttonId, opPackageName, 1, INITIAL_DELAY_MS)
            }
            null
        }

        module.log(Log.INFO, TAG, "hooked confirm method=$TARGET_METHOD class=$TARGET_CLASS")
    }

    private fun readOpPackageName(authContainerView: View): String =
        runCatching {
            val config = configField?.get(authContainerView) ?: return@runCatching null
            opPackageNameField?.get(config) as? String
        }.getOrNull() ?: "unknown"

    private fun clickWithRetry(
        parentView: View,
        buttonId: Int,
        opPackageName: String,
        attempt: Int,
        nextDelayMs: Long,
    ) {
        parentView.findViewById<Button?>(buttonId)?.takeIf { it.isShown }?.let {
            it.performClick()
            module.log(Log.INFO, TAG, "confirm clicked pkg=$opPackageName")
            return
        }

        if (attempt >= MAX_ATTEMPTS) {
            module.log(Log.WARN, TAG, "button not found pkg=$opPackageName attempts=$MAX_ATTEMPTS")
            return
        }

        module.log(
            Log.INFO,
            TAG,
            "confirm retry pkg=$opPackageName attempt=$attempt delayMs=$nextDelayMs",
        )
        parentView.postDelayed(
            { clickWithRetry(parentView, buttonId, opPackageName, attempt + 1, nextDelayMs * 2) },
            nextDelayMs,
        )
    }
}
