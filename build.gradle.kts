import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    `maven-publish`
}

group = "at.xirado"
version = "1.0.0"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.7"

dependencies {
    api("org.slf4j:slf4j-api:2.0.9")
    api("io.github.oshai:kotlin-logging-jvm:5.1.0")

    api("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    api("io.ktor:ktor-client-json:$ktorVersion")
    api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    api("io.ktor:ktor-client-encoding:$ktorVersion")

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")

    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.apply {
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.test {
    useJUnitPlatform()
}

val javadoc: Javadoc by tasks

val sourcesJar = task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

tasks {
    build {
        dependsOn(sourcesJar)
        dependsOn(jar)
    }
}

publishing {
    repositories {
        maven {
            name = "release"
            url = uri("https://maven.xirado.dev/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        register<MavenPublication>("release") {
            from(components["java"])
            groupId = "at.xirado"
            artifactId = "ktbin"
            version = project.version as String

            artifact(sourcesJar)
        }
    }
}