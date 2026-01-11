plugins {
    id("oneclick.jvm.library")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(ktorLibs.server.core)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.ktor)
            }
        }
    }
}
