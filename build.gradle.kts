import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.4.2"

plugins {
    java
    kotlin("jvm") version "1.4.10"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    mainClassName = "io.ktor.server.netty.EngineMain"
}

group = "io.github"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("junit", "junit", "4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}