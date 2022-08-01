// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    application
    id("org.springframework.boot") version "2.7.2"
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

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.8.2")
        }
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

dependencies {
    implementation(project(":gspring"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Setup build info.
group = "com.glitchybyte.quickserver"
version = "1.1.2"

application {
    mainClass.set("com.glitchybyte.quickserver.App")
}
