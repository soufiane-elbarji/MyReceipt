/*
 * Eco-Responsible Receipt Scanner
 *
 * PRIVACY BY DESIGN: This project is configured to work 100% offline.
 * No cloud services or network dependencies are included.
 */

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ReceiptReader"

include(":app")
