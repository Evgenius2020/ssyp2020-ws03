import com.soywiz.korge.gradle.*
import org.jetbrains.kotlin.gradle.internal.Kapt3KotlinGradleSubplugin.Companion.isIncrementalKapt
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

apply<KorgeGradlePlugin>()


korge {
    id = "ru.leadpogrommer.mpg"
}
korge.jvmMainClassName = "ru.leadpogrommer.mpg.MainKt"


plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.3.70"
    kotlin("kapt")
}
val ktorVersion: String by project



kotlin.sourceSets.getByName("jvmMain").dependencies {
    implementation(project(":shared"))
    compileOnly(project(":annotations"))
    compileOnly(project(":processor"))
//    configurations["kapt"].dependencies.add(project(":processor"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") //
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:0.20.0") //
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}