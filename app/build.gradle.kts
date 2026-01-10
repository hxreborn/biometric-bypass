plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "eu.rafareborn.biometricbypass"
    compileSdk = 36

    defaultConfig {
        applicationId = "eu.rafareborn.biometricbypass"
        minSdk = 29
        targetSdk = 36

        versionCode = 102
        versionName = "1.0.2"
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

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    kotlin {
        jvmToolchain(21)
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
                )
        }
    }

    lint {
        abortOnError = true
        disable.add("OldTargetApi")
    }
}

ktlint {
    version.set("1.8.0")
    android.set(true)
    ignoreFailures.set(false)
}

dependencies {
    implementation(libs.libxposed.service)
    compileOnly(libs.libxposed.api)
    implementation(libs.kotlinx.coroutines)
}
