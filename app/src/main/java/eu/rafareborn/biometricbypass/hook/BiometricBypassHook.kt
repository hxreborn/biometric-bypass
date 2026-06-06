package eu.rafareborn.biometricbypass.hook

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Button
import eu.rafareborn.biometricbypass.TAG
import eu.rafareborn.biometricbypass.module
import io.github.libxposed.api.XposedModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Field

object BiometricBypassHook {
    private const val TARGET_CLASS = "com.android.systemui.biometrics.AuthContainerView"
    private const val TARGET_METHOD = "onDialogAnimatedIn"
    private const val BUTTON_CONFIRM_ID = "button_confirm"
    private const val MAX_RETRIES = 3
    private const val INITIAL_DELAY_MS = 100L

    // Reflected once at setup so interceptors never walk the class hierarchy.
    private var configField: Field? = null
    private var opPackageNameField: Field? = null

    // Resolved on first interceptor call (needs SystemUI context), memoized for the process.
    @Volatile private var confirmButtonId: Int = 0

    @SuppressLint("PrivateApi")
    fun hook(
        xposed: XposedModule,
        classLoader: ClassLoader,
    ) {
        val targetClass = classLoader.loadClass(TARGET_CLASS)
        val targetMethod = targetClass.getDeclaredMethod(TARGET_METHOD)

        configField =
            runCatching {
                targetClass.getDeclaredField("mConfig").apply { isAccessible = true }
            }.onFailure {
                xposed.log(Log.WARN, TAG, "reflect miss field=mConfig err=${it.javaClass.simpleName}")
            }.getOrNull()

        opPackageNameField =
            runCatching {
                configField
                    ?.type
                    ?.getDeclaredField("mOpPackageName")
                    ?.apply { isAccessible = true }
            }.onFailure {
                xposed.log(
                    Log.WARN,
                    TAG,
                    "reflect miss field=mOpPackageName err=${it.javaClass.simpleName}",
                )
            }.getOrNull()

        xposed.hook(targetMethod).intercept { chain ->
            chain.proceed()

            val authContainerView = chain.thisObject as? View ?: return@intercept null
            val context = authContainerView.context

            if (confirmButtonId == 0) {
                @SuppressLint("DiscouragedApi")
                val id =
                    context.resources.getIdentifier(BUTTON_CONFIRM_ID, "id", context.packageName)
                confirmButtonId = id
            }
            val buttonId = confirmButtonId
            val opPackageName = readOpPackageName(authContainerView)

            CoroutineScope(Dispatchers.Main).launch {
                retryClickButton(authContainerView, buttonId, opPackageName)
            }
        }

        xposed.log(Log.INFO, TAG, "hooked confirm method=$TARGET_METHOD class=$TARGET_CLASS")
    }

    private fun readOpPackageName(authContainerView: View): String {
        val config = configField?.get(authContainerView) ?: return "unknown"
        return runCatching { opPackageNameField?.get(config) as? String }
            .getOrNull() ?: "unknown"
    }

    private suspend fun retryClickButton(
        parentView: View,
        buttonId: Int,
        opPackageName: String,
    ) {
        if (buttonId == 0) {
            module.log(Log.WARN, TAG, "button missing id pkg=$opPackageName")
            return
        }

        var delayTime = INITIAL_DELAY_MS

        repeat(MAX_RETRIES) { attempt ->
            parentView.findViewById<Button?>(buttonId)?.takeIf { it.isShown }?.let {
                it.performClick()
                module.log(Log.INFO, TAG, "confirm clicked pkg=$opPackageName")
                return
            }

            module.log(
                Log.INFO,
                TAG,
                "confirm retry pkg=$opPackageName attempt=${attempt + 1} delayMs=$delayTime",
            )
            delay(delayTime)
            delayTime *= 2
        }

        module.log(Log.WARN, TAG, "button not found pkg=$opPackageName retries=$MAX_RETRIES")
    }
}
