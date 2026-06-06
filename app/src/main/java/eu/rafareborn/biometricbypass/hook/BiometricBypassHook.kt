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

object BiometricBypassHook {
    private const val TARGET_CLASS = "com.android.systemui.biometrics.AuthContainerView"
    private const val TARGET_METHOD = "onDialogAnimatedIn"
    private const val BUTTON_CONFIRM_ID = "button_confirm"
    private const val MAX_RETRIES = 3
    private const val INITIAL_DELAY_MS = 100L

    @SuppressLint("PrivateApi")
    fun hook(
        xposed: XposedModule,
        classLoader: ClassLoader,
    ) {
        val targetClass = classLoader.loadClass(TARGET_CLASS)
        val targetMethod = targetClass.getDeclaredMethod(TARGET_METHOD)

        xposed.hook(targetMethod).intercept { chain ->
            chain.proceed()

            val authContainerView = chain.thisObject as? View ?: return@intercept null
            val context = authContainerView.context

            @SuppressLint("DiscouragedApi")
            val confirmButtonId =
                context.resources.getIdentifier(BUTTON_CONFIRM_ID, "id", context.packageName)
            val opPackageName = getOpPackageName(authContainerView)

            CoroutineScope(Dispatchers.Main).launch {
                retryClickButton(authContainerView, confirmButtonId, opPackageName)
            }
        }

        xposed.log(Log.INFO, TAG, "Hooked $TARGET_METHOD in $TARGET_CLASS")
    }

    private fun getOpPackageName(authContainerView: View): String {
        val result =
            runCatching {
                val config =
                    authContainerView.javaClass
                        .getDeclaredField("mConfig")
                        .apply { isAccessible = true }
                        .get(authContainerView) ?: return@runCatching null

                config.javaClass
                    .getDeclaredField("mOpPackageName")
                    .apply { isAccessible = true }
                    .get(config) as? String
            }

        result.exceptionOrNull()?.let {
            module.log(Log.WARN, TAG, "Reflection: ${it.javaClass.simpleName}")
        }

        return result.getOrNull() ?: "unknown"
    }

    private suspend fun retryClickButton(
        parentView: View,
        buttonId: Int,
        opPackageName: String,
    ) {
        var delayTime = INITIAL_DELAY_MS

        repeat(MAX_RETRIES) { attempt ->
            parentView.findViewById<Button?>(buttonId)?.takeIf { it.isShown }?.let {
                it.performClick()
                module.log(Log.INFO, TAG, "Confirm clicked [$opPackageName]")
                return
            }

            module.log(Log.INFO, TAG, "Retry ${attempt + 1} [$opPackageName] ${delayTime}ms")
            delay(delayTime)
            delayTime *= 2
        }

        module.log(Log.WARN, TAG, "Button not found [$opPackageName] after $MAX_RETRIES retries")
    }
}
