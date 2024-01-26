plugins {
    `maven-publish`
    signing
}

ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKey"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null
val localPropsFile = project.rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.reader()
        .use { java.util.Properties().apply { load(it) } }
        .onEach { (name, value) -> ext[name.toString()] = value }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKey"] = System.getenv("SIGNING_SECRET_KEY")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["artifactoryUsername"] = System.getenv("ARTIFACTORY_BEATGRID_CREDS_USR")
    ext["artifactoryPassword"] = System.getenv("ARTIFACTORY_BEATGRID_CREDS_PSW")
}

fun getExtraString(name: String) = ext[name]?.toString()

val signPublications = getExtraString("signing.keyId") != null

publishing {
    repositories {
        maven {
            name = "artifactory"
            setUrl("https://artifactory.beatgrid.net/artifactory/libs-release-local")
            credentials {
                username = getExtraString("artifactoryUsername")
                password = getExtraString("artifactoryPassword")
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })
        if (signPublications) signing.sign(this)

        pom {
            name.set("NSExceptionKt")
            description.set("Kotlin library to improve crash reports on Apple platforms")
            url.set("https://github.com/rickclephas/NSExceptionKt")
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("rickclephas")
                    name.set("Rick Clephas")
                    email.set("rclephas@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/rickclephas/NSExceptionKt")
            }
        }
    }
}

if (signPublications) {
    signing {
        getExtraString("signing.secretKey")?.let { secretKey ->
            useInMemoryPgpKeys(getExtraString("signing.keyId"), secretKey, getExtraString("signing.password"))
        }
    }
}
