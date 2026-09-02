plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "eu.rafareborn.biometricbypass"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "eu.rafareborn.biometricbypass"
        minSdk = 29
        targetSdk = 36

        versionCode = 202
        versionName = "2.0.2"
    }

    androidResources {
        @Suppress("UnstableApiUsage")
        localeFilters.add("en")
    }

    signingConfigs {
        create("release") {
            fun secret(name: String): String? =
                providers
                    .gradleProperty(name)
                    .orElse(providers.environmentVariable(name))
                    .orNull

            val storeFilePath = secret("RELEASE_STORE_FILE")
            val storePassword = secret("RELEASE_STORE_PASSWORD")
            val keyAlias = secret("RELEASE_KEY_ALIAS")
            val keyPassword = secret("RELEASE_KEY_PASSWORD")
            val storeType = secret("RELEASE_STORE_TYPE") ?: "PKCS12"

            if (!storeFilePath.isNullOrBlank()) {
                storeFile = file(storeFilePath)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
                this.storeType = storeType

                enableV1Signing = false
                enableV2Signing = true
            } else {
                logger.warn("RELEASE_STORE_FILE not found. Release signing is disabled.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release").takeIf { it.storeFile != null }
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    buildFeatures {
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            merges += "META-INF/xposed/*"
            excludes +=
                setOf(
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/AL2.0",
                    "META-INF/LGPL2.1",
                    "META-INF/*.kotlin_module",
                    "META-INF/INDEX.LIST",
                    "kotlin-tooling-metadata.json",
                    "kotlin/**",
                    "META-INF/services/*",
                    "META-INF/com/android/build/gradle/*",
                    "META-INF/version-control-info.textproto",
                )
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
        disable.addAll(listOf("OldTargetApi", "PrivateApi", "DiscouragedPrivateApi"))
        ignoreTestSources = true
    }
}

kotlin { jvmToolchain(21) }

ktlint {
    version.set("1.8.0")
    android.set(true)
    ignoreFailures.set(false)
}

// AGP 9 built-in Kotlin doesn't register source sets that the ktlint plugin can discover.
// Run ktlint 1.8.0 directly on source files as a workaround.
val ktlintSrc by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Runs ktlint 1.8.0 on Kotlin source files"
    mainClass.set("com.pinterest.ktlint.Main")
    classpath =
        configurations.detachedConfiguration(
            dependencies.create("com.pinterest.ktlint:ktlint-cli:1.8.0"),
        )
    args("src/**/*.kt")
}

tasks.named("check").configure {
    dependsOn(ktlintSrc)
}

dependencies {
    compileOnly(libs.libxposed.api)
}
