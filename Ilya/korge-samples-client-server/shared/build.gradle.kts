import com.soywiz.korge.gradle.*

apply<KorgeGradlePlugin>()

val ktorVersion: String by project

kotlin.sourceSets.getByName("jvmMain").dependencies {
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation ("com.google.code.gson:gson:2.8.6")
}