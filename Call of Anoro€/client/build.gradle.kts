import com.soywiz.korge.gradle.*

apply<KorgeGradlePlugin>()

korge {
    id = "ru.leadpogrommer.mpg"
}
korge.jvmMainClassName = "client.MainKt"


plugins {
    kotlin("plugin.serialization") version "1.3.70"
}
val ktorVersion: String by project

kotlin.sourceSets.getByName("jvmMain").dependencies {
    implementation(project(":shared"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
	implementation ("io.ktor:ktor-server-netty:1.3.2")
}