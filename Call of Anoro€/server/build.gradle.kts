buildscript {
    dependencies{
        classpath("com.github.jengelman.gradle.plugins:shadow:6.0.0")
    }
}


plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}



val ktorVersion: String by project

sourceSets.getByName("main").resources.srcDirs("../shared/src/jvmMain/resources")

repositories{
    maven{
        url = uri("https://github.com/leadpogrommer/libtiled-snapshot-binaries/raw/master")
    }
}

dependencies {
    add("implementation", project(":shared"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
	implementation ("io.ktor:ktor-server-netty:1.3.2")
    implementation("org.mapeditor:libtiled:1.2.3-SNAPSHOT")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
}
application.mainClassName = "server.MainKt"

tasks.jar{
    manifest.attributes("Main-Class" to application.mainClassName)
}