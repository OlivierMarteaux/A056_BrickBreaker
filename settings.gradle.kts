pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Add the plugin for JVM toolchain downloading
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Add Jitpack repository for personal shared library
//        maven{ url = uri("https://jitpack.io") }
    }
}

rootProject.name = "A056_BricksBreaker"
include(":app")
includeBuild("../shared-android-library") // to load shared library locally
