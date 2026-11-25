plugins {
	java
	id("org.springframework.boot") version "4.0.6"         // https://plugins.gradle.org/plugin/org.springframework.boot
	id("io.spring.dependency-management") version "1.1.7"  // https://plugins.gradle.org/plugin/io.spring.dependency-management
}

group = "com.jordanpaille"
version = "0.0.1-SNAPSHOT"
description = "project for housing-coop"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.commonmark:commonmark:0.33.0")
	implementation("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:20261110.1")

	// Logging
	implementation("ch.qos.logback:logback-classic:1.5.32")                            // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.46")
	annotationProcessor("org.projectlombok:lombok:1.18.46")

	// Email Service
	implementation("com.postmarkapp:postmark:1.13.0")

	// db
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.postgresql:postgresql")
	runtimeOnly("com.h2database:h2")
	implementation("org.springframework.boot:spring-boot-h2console")


	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("ch.qos.logback:logback-classic:1.5.32")
	testCompileOnly("org.projectlombok:lombok:1.18.46")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.46")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	args("--spring.profiles.active=dev")
	sourceResources(sourceSets["main"])
}