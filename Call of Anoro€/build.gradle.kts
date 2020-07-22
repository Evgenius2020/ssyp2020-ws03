import com.soywiz.korge.gradle.*

buildscript {
	val korgeVersion: String by project

	repositories {
		mavenLocal()
		jcenter()
		maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
		maven { url = uri("https://plugins.gradle.org/m2/") }
		mavenCentral()
		google()
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:1.15.0.0")
	}
}

apply<KorgeGradlePlugin>()

korge {
	id = "com.example.example"
}

kotlin.sourceSets.getByName("jvmMain").dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
	implementation ("io.ktor:ktor-server-netty:1.3.2")
	implementation("org.mapeditor:libtiled:1.2.3")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
	implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
}