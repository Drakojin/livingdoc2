import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java-project-config")
	kotlin("jvm")
}

tasks.withType<KotlinCompile>().configureEach{
	kotlinOptions {
		jvmTarget = Versions.jvmTarget.toString()
		apiVersion = Versions.kotlinApi
		languageVersion = Versions.kotlinApi
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	testImplementation ("org.junit.jupiter:junit-jupiter-api:${Versions.junitJupiter}")
	testImplementation ("org.junit.jupiter:junit-jupiter-params:${Versions.junitJupiter}")
	testImplementation("io.mockk:mockk:${Versions.mockk}")
	testImplementation("io.strikt:strikt-core:0.22.2")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junitJupiter}")
}
