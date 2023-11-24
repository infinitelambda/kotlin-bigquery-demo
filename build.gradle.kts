import io.ktor.plugin.features.*
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val targetEnvironmentArg = (project.findProperty("targetEnv") as? String)?.uppercase()
val targetEnvironment = when (targetEnvironmentArg) {
    "DEV", "PROD" -> targetEnvironmentArg
    null -> "DEV"
    else -> throw Exception("Invalid target environment: '$targetEnvironmentArg'. Expected 'DEV' or 'PROD'")
}

logger.lifecycle("Building with targetEnv = $targetEnvironment")

plugins {
    kotlin("multiplatform") version "1.9.0"
    application
    kotlin("plugin.serialization") version "1.9.0"
    id("io.ktor.plugin") version "2.3.2"
}

group = "com.infinitelambda"
version = "0.0.4"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    js {
        binaries.executable()
        browser {
            commonWebpackConfig(Action<KotlinWebpackConfig> {
                cssSupport {
                    enabled.set(true)
                }

                export = false
            })

            val targetEnvironmentWebpackArgs = listOf("--env", "targetEnvironment=${targetEnvironment}")
            webpackTask(Action<KotlinWebpack> {
                args.plusAssign(targetEnvironmentWebpackArgs)
            })
            runTask(Action<KotlinWebpack> {
                args.plusAssign(targetEnvironmentWebpackArgs)
            })
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                implementation("io.arrow-kt:arrow-core:1.2.0")
                implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")

                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:2.3.2")
                implementation("io.ktor:ktor-server-content-negotiation:2.3.2")
                implementation("io.ktor:ktor-server-html-builder-jvm:2.3.2")
                implementation("io.ktor:ktor-server-call-logging:2.3.2")
                implementation("io.ktor:ktor-server-websockets:2.3.2")
                implementation("io.ktor:ktor-server-cors:2.3.2")

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")

                implementation(platform("com.google.cloud:libraries-bom:26.26.0"))
                implementation("com.google.cloud:google-cloud-bigquery")
                implementation("com.google.cloud:google-cloud-language")

                implementation("ch.qos.logback:logback-classic:1.4.11")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core-js:2.3.2")
                implementation("io.ktor:ktor-client-js:2.3.2")
                implementation("io.ktor:ktor-client-websockets:2.3.2")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.2")

                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.9.1")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("com.infinitelambda.application.ServerKt")
}

ktor {
    fatJar {
        archiveFileName.set("${project.name}-$version-all.jar")
    }

    docker {
        jreVersion.set(JreVersion.JRE_17)
        localImageName.set("kotlin-bigquery-demo")
        imageTag.set(version.toString())

        externalRegistry.set(
            DockerImageRegistry.harbor(
                appName = provider { "kotlin-bigquery-demo" },
                username = providers.environmentVariable("IL_HARBOR_USERNAME"),
                password = providers.environmentVariable("IL_HARBOR_PASSWORD")
            )
        )
    }
}

tasks {

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
        }

    }

    named<Copy>("jvmProcessResources") {
        val jsBrowserDistribution = named("jsBrowserDistribution")
        from(jsBrowserDistribution)
    }

    named<JavaExec>("run") {
        dependsOn(named<Jar>("jvmJar"))
        classpath(named<Jar>("jvmJar"))
    }
}

class HarborImageRegistry(
    appName: Provider<String>,
    override val username: Provider<String>,
    override val password: Provider<String>
) : DockerImageRegistry {

    override val toImage: Provider<String> =
        appName.map { name -> "harbor.iflambda.com/lib/$name" }

}

fun DockerImageRegistry.Companion.harbor(
    appName: Provider<String>,
    username: Provider<String>,
    password: Provider<String>
) = HarborImageRegistry(appName, username, password)