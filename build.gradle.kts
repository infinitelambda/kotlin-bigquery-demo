import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.0"
    application
    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.infinitelambda"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
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
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
                implementation("io.ktor:ktor-server-html-builder-jvm:2.3.2")
                implementation("io.ktor:ktor-server-call-logging:2.3.2")

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")

                implementation(platform("com.google.cloud:libraries-bom:26.26.0"))
                implementation("com.google.cloud:google-cloud-bigquery")

                implementation("ch.qos.logback:logback-classic:1.4.11")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
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