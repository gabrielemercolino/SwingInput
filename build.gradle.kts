@file:Suppress("SpellCheckingInspection")

plugins {
    `java-library`
    `maven-publish`

    id("pl.allegro.tech.build.axion-release") version "1.21.1"
}

group = "com.github.gabrielemercolino"
version = scmVersion.version

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing.publications.create<MavenPublication>("maven") {
    from(components["java"])
}
