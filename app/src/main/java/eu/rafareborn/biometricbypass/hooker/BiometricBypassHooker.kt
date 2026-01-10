package eu.rafareborn.biometricbypass.hooker

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import eu.rafareborn.biometricbypass.BiometricBypassModule
import eu.rafareborn.biometricbypass.BiometricBypassModule.Companion.TAG
import eu.rafareborn.biometricbypass.module
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.XposedHooker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@XposedHooker
class BiometricBypassHooker : XposedInterface.Hooker {
    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 100L

        @JvmStatic
        @AfterInvocation
        fun afterInvocation(callback: XposedInterface.AfterHookCallback) {
            val authContainerView = callback.thisObject as? View ?: return
            val context = authContainerView.context ?: return

            @SuppressLint("DiscouragedApi")
            val confirmButtonId =
                context.resources.getIdentifier(
                    BiometricBypassModule.BUTTON_CONFIRM_ID,
                    "id",
                    context.packageName,
                )

            CoroutineScope(Dispatchers.Main).launch {
                retryClickButton(authContainerView, confirmButtonId)
            }
        }

        private suspend fun retryClickButton(
            parentView: View,
            buttonId: Int,
        ) {
            var delayTime = INITIAL_DELAY_MS

            repeat(MAX_RETRIES) { attempt ->
                parentView.findViewById<Button?>(buttonId)?.takeIf { it.isShown }?.let {
                    it.performClick()
                    module.log("$TAG Confirm button clicked successfully.")
                    return
                }

                module.log("$TAG Retry #${attempt + 1}: Button not visible. Waiting ${delayTime}ms...")
                delay(delayTime)
                delayTime *= 2
            }

            module.log("$TAG Confirm button not found after $MAX_RETRIES retries.")
        }
    }
}
