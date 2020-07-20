import com.soywiz.korge.gradle.*

apply<KorgeGradlePlugin>()

korge {
    id = "ru.leadpogrommer.mpg"
}
korge.jvmMainClassName = "ru.leadpogrommer.mpg.MainKt"


plugins {
    kotlin("plugin.serialization") version "1.3.70"
}
val ktorVersion: String by project

kotlin.sourceSets.getByName("jvmMain").dependencies {
    implementation(project(":shared"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") //
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:0.20.0") //
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}