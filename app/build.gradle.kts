
plugins {
    alias(libs.plugins.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.ktor.server.test.host)

    implementation(libs.guava)

    // Ktor Server and GraphQL
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.graphql.kotlin.ktor.server)

    // Database
    implementation(libs.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "catserver.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
