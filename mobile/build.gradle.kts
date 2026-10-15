plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Optional local-only input. CI and ordinary source builds contain no accessory identity.
val localAuthenticationAssets = providers.environmentVariable("TUPIGPLAY_AUTH_ASSETS_DIR")
    .orNull?.let { file(it).canonicalFile }

// Optional local-only override for a head-unit whitelist experiment. Unset in CI, so ordinary
// builds keep the default identity. When set it is the final package name: the debug suffix is
// dropped so the APK carries exactly the requested applicationId.
// Usage: ./gradlew :mobile:assembleRelease -PtupigplayApplicationId=com.ximalaya.ting.android.car
val overrideApplicationId = providers.gradleProperty("tupigplayApplicationId").orNull

// TuPigPlay fork versioning: version.properties is the single source of truth, decoupled from the
// upstream version scheme. Bump it with ./version_increment.sh [major|minor|patch|build].
// versionCode keeps the +38 offset so 0.0.0.1 stays compatible with the previously released code 39.
val versionProperties = java.util.Properties().apply {
    rootProject.file("version.properties").inputStream().use { load(it) }
}
val forkVersionName = listOf("VERSION_MAJOR", "VERSION_MINOR", "VERSION_PATCH", "VERSION_BUILD")
    .joinToString(".") { versionProperties.getProperty(it) }
val forkVersionCode = versionProperties.getProperty("VERSION_MAJOR").toInt() * 1_000_000 +
    versionProperties.getProperty("VERSION_MINOR").toInt() * 10_000 +
    versionProperties.getProperty("VERSION_PATCH").toInt() * 100 +
    versionProperties.getProperty("VERSION_BUILD").toInt() + 38

android {
    namespace = "com.shilapi.xcertplay"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = overrideApplicationId ?: "com.tupig.tupigplay"
        minSdk = 25
        targetSdk = 37
        versionCode = forkVersionCode
        versionName = forkVersionName

    }


    localAuthenticationAssets?.let { sourceSets.getByName("main").assets.srcDir(it) }

    signingConfigs {
        create("release") {
            storeFile = file(
                providers.environmentVariable("ANDROID_KEYSTORE_PATH")
                    .getOrElse("missing-release-keystore.jks"),
            )
            storePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").getOrElse("")
            keyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").getOrElse("")
            keyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").getOrElse("")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = if (overrideApplicationId != null) "" else ".hudtest"
            versionNameSuffix = "-hud-test"
        }
        release {
            optimization {
                enable = false
            }
            signingConfig = signingConfigs.getByName("release")
            // 零跑白名单包名构建：指定 -PtupigplayApplicationId 时使用指定包名，去掉后缀
            applicationIdSuffix = if (overrideApplicationId != null) "" else ""
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(project(":common"))
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.app.projected)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// No implicit import. Only the two explicitly selected local runtime assets are allowed.
val credentialAssets = files(android.sourceSets.flatMap { source ->
    source.assets.directories.map { directory ->
        fileTree(directory) {
            include("**/offline-mfi/**", "**/*.pk8", "**/*.p7b", "**/*.key",
                "**/*.pem", "**/*.p12", "**/*.pfx", "**/*.jks", "**/*.keystore")
        }
    }
})
val rejectBundledCredentials by tasks.registering {
    group = "verification"
    description = "Reject unexpected credential files in APK assets."
    val filesToCheck = credentialAssets
    val allowed = localAuthenticationAssets?.let { dir ->
        listOf("identity.pk8", "certificate.p7b").map { dir.resolve("offline-mfi/$it").canonicalFile }.toSet()
    } ?: emptySet()
    inputs.files(filesToCheck)
    doLast {
        check(allowed.all { it.isFile }) { "Explicit local authentication assets are incomplete" }
        val unexpected = filesToCheck.files.filter { it.canonicalFile !in allowed }
        check(unexpected.isEmpty()) { "Unexpected credential files in APK assets" }
    }
}
tasks.named("preBuild") { dependsOn(rejectBundledCredentials) }

// Car-test packages must be standalone. Keep ordinary source/CI builds identity-free.
val verifyStandaloneAuthentication by tasks.registering {
    group = "verification"
    description = "Require the explicit runtime authentication input for a standalone car-test APK."
    val directory = localAuthenticationAssets
    doLast {
        check(directory != null) {
            "Standalone car builds require TUPIGPLAY_AUTH_ASSETS_DIR; assembleDebug alone is source-only."
        }
        check(listOf("identity.pk8", "certificate.p7b").all {
            directory.resolve("offline-mfi/$it").let { file -> file.isFile && file.length() > 0 }
        }) { "Standalone CarPlay authentication files are missing or empty" }
    }
}
tasks.named("preBuild") { mustRunAfter(verifyStandaloneAuthentication) }
tasks.register("assembleStandaloneDebug") {
    group = "build"
    description = "Build a standalone car-test APK with explicitly provisioned authentication."
    dependsOn(verifyStandaloneAuthentication, "assembleDebug")
}
