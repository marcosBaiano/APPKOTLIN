plugins {
    kotlin("jvm") version "2.3.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-server-auth:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.7")

    // JSON serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

    // Security - Bcrypt for password hashing
    implementation("at.favre.lib:bcrypt:0.9.0")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // Database - Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    // MySQL Driver
    implementation("mysql:mysql-connector-java:8.0.33")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

// Task para executar a aplicação
tasks.register<JavaExec>("run") {
    group = "application"
    description = "Executa a aplicação de API"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("MainKt")
}
