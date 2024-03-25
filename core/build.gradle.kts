// TODO: Android support

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    //alias(libs.plugins.androidLibrary)
    id("module.publication")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    js {
        browser()
        nodejs()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.datetime)
                api(libs.kotlin.logging)
                api(libs.ktor.client.json)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.client.encoding)
                api(libs.ktor.serialization.json)
                api(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly(libs.java.std)
                api(libs.slf4j)
            }
        }
    }
}

//android {
//    namespace = "dev.xirado.ktor"
//    compileSdk = libs.versions.android.compileSdk.get().toInt()
//    defaultConfig {
//        minSdk = libs.versions.android.minSdk.get().toInt()
//    }
//}