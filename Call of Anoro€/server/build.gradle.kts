plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.3.70"
}


val ktorVersion: String by project



dependencies {
    add("implementation", project(":shared"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
	implementation ("io.ktor:ktor-server-netty:1.3.2")
    implementation("org.mapeditor:libtiled:1.2.3")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
}
application.mainClassName = "server.MainKt"
