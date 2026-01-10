package eu.rafareborn.biometricbypass

import android.annotation.SuppressLint
import eu.rafareborn.biometricbypass.hooker.BiometricBypassHooker
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

internal lateinit var module: BiometricBypassModule

class BiometricBypassModule(
    base: XposedInterface,
    param: ModuleLoadedParam,
) : XposedModule(base, param) {
    init {
        module = this
    }

    @SuppressLint("PrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        if (param.packageName != TARGET_PACKAGE || !param.isFirstPackage) return

        module.log("$TAG Loaded package: ${param.packageName}")

        try {
            hookTargetMethod(param.classLoader)
        } catch (e: ReflectiveOperationException) {
            module.log("$TAG Error: ${e::class.simpleName} - ${e.message}")
        } catch (e: Exception) {
            module.log("$TAG Unexpected error: ${e.message}")
        }
    }

    @SuppressLint("PrivateApi")
    private fun hookTargetMethod(classLoader: ClassLoader) {
        val targetClass = classLoader.loadClass(TARGET_CLASS)
        val targetMethod = targetClass.getDeclaredMethod(TARGET_METHOD)
        module.hook(targetMethod, BiometricBypassHooker::class.java)
        module.log("$TAG Successfully hooked method: $TARGET_METHOD in $TARGET_CLASS")
    }

    companion object {
        const val TAG = "BiometricBypassModule"
        private const val TARGET_PACKAGE = "com.android.systemui"
        private const val TARGET_CLASS = "com.android.systemui.biometrics.AuthContainerView"
        private const val TARGET_METHOD = "onDialogAnimatedIn"
        const val BUTTON_CONFIRM_ID = "button_confirm"
    }
}
