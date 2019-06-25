import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openjfx.gradle.JavaFXOptions

plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":dataforge-vis-spatial"))
    implementation(project(":dataforge-vis-fx"))
    implementation("org.fxyz3d:fxyz3d:0.4.0")
}

configure<JavaFXOptions> {
    modules("javafx.controls")
}

tasks.withType<KotlinCompile> {
    kotlinOptions{
        jvmTarget = "1.8"
    }
}