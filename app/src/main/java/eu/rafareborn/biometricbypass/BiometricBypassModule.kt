package eu.rafareborn.biometricbypass

import android.annotation.SuppressLint
import android.util.Log
import eu.rafareborn.biometricbypass.hook.BiometricBypassHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

internal const val TAG = "BiometricBypass"
private const val TARGET_PACKAGE = "com.android.systemui"

@PublishedApi internal lateinit var module: BiometricBypassModule

class BiometricBypassModule : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        module = this
        log(Log.INFO, TAG, "loaded version=${BuildConfig.VERSION_NAME}")
    }

    @SuppressLint("PrivateApi")
    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != TARGET_PACKAGE || !param.isFirstPackage) return

        try {
            BiometricBypassHook.hook(this, param.classLoader)
        } catch (e: ReflectiveOperationException) {
            log(Log.ERROR, TAG, "hook failed pkg=$TARGET_PACKAGE err=${e::class.simpleName} msg=${e.message}")
        } catch (e: Exception) {
            log(Log.ERROR, TAG, "hook failed pkg=$TARGET_PACKAGE err=${e::class.simpleName} msg=${e.message}")
        }
    }
}
