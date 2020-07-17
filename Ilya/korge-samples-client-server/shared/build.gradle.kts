import com.soywiz.korge.gradle.*

apply<KorgeGradlePlugin>()

plugins {
//    kotlin("plugin.serialization") version "1.3.70"
}
val ktorVersion: String by project

kotlin.sourceSets.getByName("jvmMain").dependencies {
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") //
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:0.20.0") //
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
//    implementation("org.json:json:20200518")
    implementation ("com.google.code.gson:gson:2.8.6")
}