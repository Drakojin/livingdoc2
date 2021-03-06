plugins {
	`kotlin-project-config`
}

dependencies {
	api(project(":livingdoc-api"))
	api(project(":livingdoc-converters"))
	api(project(":livingdoc-repositories"))

	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

tasks.compileTestJava {
	options.compilerArgs.add("-parameters")
}
