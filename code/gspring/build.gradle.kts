// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    `java-library`
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
    api("org.springframework.boot:spring-boot-starter-web:2.6.6")
    api("org.apache.tika:tika-core:2.3.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = 4
}

// Setup build info.
group = "com.glitchybyte.gspring"
version = "1.0.0"
