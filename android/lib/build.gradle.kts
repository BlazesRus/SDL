plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    buildToolsVersion = "31.0.0"
    ndkVersion = "23.0.7599858"
    compileSdk = 31
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        version = project.version
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                val buildOptions = mutableListOf<String>().apply {
                    System.getenv("ANDROID_CCACHE")?.let { add("ANDROID_CCACHE=$it") }
                }
                arguments.addAll(buildOptions.map { "-D$it" })
                targets.add("SDL2")
            }
        }
        splits {
            abi {
                System.getenv("ANDROID_ABI")?.let {
                    isEnable = true
                    reset()
                    include(*it.split(',').toTypedArray())
                }
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    externalNativeBuild {
        cmake {
            buildStagingDirectory = project.file(".cxx")
            path = project.file("../../CMakeLists.txt")
            version = "3.19.0+"
        }
    }
    sourceSets {
        named("main") {
            java.srcDir("../../android-project/app/src/main/java")
        }
    }
}

dependencies {
    implementation("com.getkeepsafe.relinker:relinker:1.4.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

tasks {
    register<Delete>("cleanAll") {
        dependsOn("clean")
        delete = setOf(android.externalNativeBuild.cmake.buildStagingDirectory)
    }
}
