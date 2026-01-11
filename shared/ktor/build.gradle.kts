@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("oneclick.android.library")
    id("oneclick.jvm.library")
    id("oneclick.wasm.library")
    id("oneclick.ios.library")
    alias(libs.plugins.kmp.serialization)
}

androidLibrary {
    namespace = "oneclick.shared.ktor"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(ktorLibs.client.core)
                implementation(projects.shared.contracts.core)
            }
        }
    }
}