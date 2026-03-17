# Xposed
-adaptresourcefilecontents META-INF/xposed/java_init.list
-keepattributes RuntimeVisibleAnnotations
-keep,allowobfuscation,allowoptimization public class * extends io.github.libxposed.api.XposedModule {
    public <init>();
    public void onPackageLoaded(...);
    public void onPackageReady(...);
    public void onSystemServerStarting(...);
}

# Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}
-assumenosideeffects class java.util.Objects {
    public static ** requireNonNull(...);
}

# Strip debug log
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}

# Strip coroutine debug probes
-assumenosideeffects class kotlinx.coroutines.DebugKt {
    public static ** getAGENT_INSTALLED();
    public static ** getDEBUG();
    public static ** getCHECK_RECOVERY_MODE();
    public static ** getCHECK_RECOVERY_MODE_EXCEPTION();
}
-checkdiscard class kotlinx.coroutines.debug.**

# Obfuscation
-repackageclasses
-allowaccessmodification
