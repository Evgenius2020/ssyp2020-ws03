import com.soywiz.korge.gradle.*

buildscript {
	val korgeVersion: String by project

	repositories {
		mavenLocal()
		maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
		maven { url = uri("https://plugins.gradle.org/m2/") }
		mavenCentral()
		jcenter()
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:1.13.8.3")
	}
}

apply<KorgeGradlePlugin>()

korge {
	id = "com.example.example"
}

kotlin.sourceSets.getByName("jvmMain").dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
}