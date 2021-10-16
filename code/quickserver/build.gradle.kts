// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    application
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass.set("com.glitchybyte.quickserver.App")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = 4
}
