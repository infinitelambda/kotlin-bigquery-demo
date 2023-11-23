import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.0"
    application
    kotlin("plugin.serialization") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.1"
}

group = "com.infinitelambda"
version = "1.0-SNAPSHOT"

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
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
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

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")

                implementation(platform("com.google.cloud:libraries-bom:26.26.0"))
                implementation("com.google.cloud:google-cloud-bigquery")
                implementation("com.google.cloud:google-cloud-language")

                implementation("ch.qos.logback:logback-classic:1.4.11")

                implementation(compose.runtime)
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)

                implementation("io.github.koalaplot:koalaplot-core:0.4.0")

                implementation("io.ktor:ktor-client-core-js:2.3.2")
                implementation("io.ktor:ktor-client-js:2.3.2")
                implementation("io.ktor:ktor-client-websockets:2.3.2")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("com.infinitelambda.application.ServerKt")
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

compose.experimental {
    web.application {}
}