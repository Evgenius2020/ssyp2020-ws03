import com.soywiz.korge.gradle.*

apply<KorgeGradlePlugin>()

korge {
    id = "com.sample.clientserver"
}

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


//plugins{
//    application
//    kotlin("jvm")
//    kotlin("plugin.serialization") version "1.3.70"
//}
//
//repositories {
//    mavenLocal()
//    maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
//    maven { url = uri("https://plugins.gradle.org/m2/") }
//    jcenter()
//    mavenCentral()
//}
//val ktorVersion: String by project
//dependencies {
//    add("implementation", project(":shared"))
//    add("implementation", "io.ktor:ktor-network:$ktorVersion")
//    add("implementation", "ch.qos.logback:logback-classic:1.2.3")
//
//    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") //
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:0.20.0") //
//
//    implementation("com.soywiz.korlibs.korge:korge:1.15.0")
//
//
//}
//
//application{
//    mainClassName = "MainKt"
//}
