plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.3.70"
}


val ktorVersion: String by project



dependencies {
    add("implementation", project(":shared"))
    add("implementation", "io.ktor:ktor-network:$ktorVersion")
    add("implementation", "ch.qos.logback:logback-classic:1.2.3")

    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") //
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:0.20.0") //
    implementation("org.mapeditor:libtiled:1.2.3")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
}

application {
    mainClassName = "ru.leadpogrommer.mpg.MainKt"
}
