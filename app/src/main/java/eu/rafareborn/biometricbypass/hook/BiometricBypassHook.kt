package eu.rafareborn.biometricbypass.hook

import android.annotation.SuppressLint
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import eu.rafareborn.biometricbypass.TAG
import eu.rafareborn.biometricbypass.module
import java.lang.reflect.Field
import java.lang.reflect.Method

object BiometricBypassHook {
    private val TARGET_CLASSES =
        listOf(
            "com.android.systemui.biometrics.prompt.ui.AuthContainerView",
            "com.android.systemui.biometrics.AuthContainerView",
        )
    private const val TARGET_METHOD = "onDialogAnimatedIn"
    private const val CONFIG_FIELD = "mConfig"
    private const val OP_PACKAGE_FIELD = "mOpPackageName"
    private const val BUTTON_CONFIRM_ID = "button_confirm"

    private const val MAX_WAIT_MS = 1_500L

    private var configField: Field? = null
    private var opPackageNameField: Field? = null

    @Volatile
    private var confirmButtonId: Int = 0

    fun hook(classLoader: ClassLoader) {
        val targetClass =
            TARGET_CLASSES.firstNotNullOfOrNull { name ->
                runCatching { classLoader.loadClass(name) }.getOrNull()
            } ?: throw ClassNotFoundException(
                "AuthContainerView not on any known path $TARGET_CLASSES",
            )

        val targetMethod =
            targetClass.zeroArgMethodByBaseName(TARGET_METHOD)
                ?: throw NoSuchMethodException("no $TARGET_METHOD variant on ${targetClass.name}")

        configField = targetClass.fieldByBaseName(CONFIG_FIELD)
        opPackageNameField = configField?.type?.fieldByBaseName(OP_PACKAGE_FIELD)
        if (opPackageNameField == null) {
            module.log(Log.WARN, TAG, "op package field unresolved pkg=unknown in logs")
        }

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

        module.log(Log.INFO, TAG, "hooked confirm on ${targetClass.name}#${targetMethod.name}")
    }

    private fun Class<*>.zeroArgMethodByBaseName(base: String): Method? {
        val zeroArg = declaredMethods.filter { it.parameterCount == 0 }
        return zeroArg.firstOrNull { it.name == base }
            ?: zeroArg.firstOrNull { it.name.startsWith("$base\$") }
    }

    private fun Class<*>.fieldByBaseName(base: String): Field? {
        val field =
            declaredFields.firstOrNull { it.name == base }
                ?: declaredFields.firstOrNull { it.name.startsWith("$base\$") }
        return field?.apply { isAccessible = true }
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
