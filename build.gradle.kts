plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "io.vanja"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinCsvVersion = "1.9.3"

dependencies {
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:$kotlinCsvVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("io.vanja.SanloKt")
}