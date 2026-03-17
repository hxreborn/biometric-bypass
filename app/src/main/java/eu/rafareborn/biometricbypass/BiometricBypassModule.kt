package eu.rafareborn.biometricbypass

import android.annotation.SuppressLint
import android.util.Log
import eu.rafareborn.biometricbypass.hooker.BiometricBypassHooker
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

class BiometricBypassModule : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        log(Log.INFO, TAG, "BiometricBypass v${BuildConfig.VERSION_NAME} loaded")
    }

    @SuppressLint("PrivateApi")
    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != TARGET_PACKAGE || !param.isFirstPackage) return

        try {
            BiometricBypassHooker.hook(this, param.classLoader)
        } catch (e: ReflectiveOperationException) {
            log(Log.ERROR, TAG, "Error: ${e::class.simpleName} - ${e.message}")
        } catch (e: Exception) {
            log(Log.ERROR, TAG, "Unexpected error: ${e.message}")
        }
    }

    companion object {
        const val TAG = "BiometricBypass"
        private const val TARGET_PACKAGE = "com.android.systemui"
    }
}
