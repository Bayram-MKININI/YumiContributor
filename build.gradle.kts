import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("com.android.library") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.13" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("androidx.navigation.safeargs") version "2.7.5" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.github.ben-manes.versions") version "0.49.0"
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        candidate.version.isNonStable()
    }
    checkForGradleUpdate = true
    revision = "release"
    outputFormatter = "html"
    outputDir = "./app/build/reports/"
    reportfileName = "yumi_contributor_dependency_update_report"
}