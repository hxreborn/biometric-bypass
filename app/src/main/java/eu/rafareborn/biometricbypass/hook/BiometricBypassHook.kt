package eu.rafareborn.biometricbypass.hook

import android.annotation.SuppressLint
import android.os.SystemClock
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

    private const val MAX_WAIT_MS = 1_500L

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

            val startUptimeMs = SystemClock.uptimeMillis()
            if (clickConfirm(authContainerView, buttonId, opPackageName, startUptimeMs)) {
                return@intercept null
            }

            retryUntilShown(authContainerView, buttonId, opPackageName, startUptimeMs)
            null
        }

        module.log(Log.INFO, TAG, "hooked confirm method=$TARGET_METHOD class=$TARGET_CLASS")
    }

    private fun readOpPackageName(authContainerView: View): String =
        runCatching {
            val config = configField?.get(authContainerView) ?: return@runCatching null
            opPackageNameField?.get(config) as? String
        }.getOrNull() ?: "unknown"

    private fun clickConfirm(
        parentView: View,
        buttonId: Int,
        opPackageName: String,
        startUptimeMs: Long,
    ): Boolean {
        val button = parentView.findViewById<Button?>(buttonId)
        if (button == null || !button.isShown) return false
        button.performClick()
        val latencyMs = SystemClock.uptimeMillis() - startUptimeMs
        module.log(Log.INFO, TAG, "confirm clicked pkg=$opPackageName latencyMs=$latencyMs")
        return true
    }

    private fun retryUntilShown(
        parentView: View,
        buttonId: Int,
        opPackageName: String,
        startUptimeMs: Long,
    ) {
        parentView.postOnAnimation {
            if (clickConfirm(parentView, buttonId, opPackageName, startUptimeMs)) {
                return@postOnAnimation
            }
            val elapsedMs = SystemClock.uptimeMillis() - startUptimeMs
            if (elapsedMs >= MAX_WAIT_MS) {
                module.log(Log.WARN, TAG, "button not found pkg=$opPackageName waitMs=$elapsedMs")
                return@postOnAnimation
            }
            retryUntilShown(parentView, buttonId, opPackageName, startUptimeMs)
        }
    }
}
