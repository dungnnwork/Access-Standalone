pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}

rootProject.name = "test_pro"
include(":app")


//pluginManagement {
//    repositories {
//        mavenCentral()
//        gradlePluginPortal()
//        maven("https://maven.aliyun.com/repository/google")
//        maven("https://maven.aliyun.com/repository/gradle-plugin")
//        google()
//    }
//}
//
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        mavenCentral()
//        maven("https://maven.aliyun.com/repository/google")
//        google()
//    }
//}
//
//rootProject.name = "test_pro"
//include(":app")
