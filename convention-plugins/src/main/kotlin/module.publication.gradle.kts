import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    `maven-publish`
    signing
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })

        // Provide artifacts information required by Maven Central
        pom {
            name.set("ktbin")
            description.set("Kotlin API Wrapper for Gobin, a hastebin/pastebin compatible paste server")
            url.set("https://github.com/Xirado/ktbin")

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("Xirado")
                    name.set("Marcel Korzonek")
                    email.set("marcel@xirado.dev")
                }
            }
            scm {
                url.set("https://github.com/Xirado/ktbin")
                connection.set("scm:git:git://github.com/Xirado/ktbin")
                developerConnection.set("scm:git:ssh:git@github.com:Xirado/ktbin")
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}