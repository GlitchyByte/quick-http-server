// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    application
    id("org.springframework.boot") version "2.6.6"
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
    implementation(project(":gspring"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = 4
}

// Setup build info.
group = "com.glitchybyte.quickserver"
version = "1.1.1"

application {
    mainClass.set("com.glitchybyte.quickserver.App")
}
