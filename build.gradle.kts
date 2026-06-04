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
    implementation("org.openfolder:kotlin-asyncapi-ktor:3.2.2")
    implementation("io.ktor:ktor-server-cors:2.3.7")
    implementation("io.ktor:ktor-server-caching-headers:2.3.7")
    implementation("io.ktor:ktor-server-compression:2.3.7")
    implementation("io.ktor:ktor-server-conditional-headers:2.3.7")
    implementation("io.ktor:ktor-server-default-headers:2.3.7")
    implementation("io.ktor:ktor-server-forwarded-header:2.3.7")
    implementation("io.ktor:ktor-server-hsts:2.3.7")
    implementation("io.ktor:ktor-server-http-redirect:2.3.7")
    implementation("io.ktor:ktor-server-openapi:2.3.7")
    implementation("io.ktor:ktor-server-routing-openapi:2.3.7")
    implementation("io.ktor:ktor-server-partial-content:2.3.7")
    implementation("com.ucasoft.ktor:ktor-simple-cache:0.72.1")
    implementation("com.ucasoft.ktor:ktor-simple-memory-cache:0.72.1")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache:0.72.1")
    implementation("io.ktor:ktor-server-swagger:2.3.7")
    implementation("io.ktor:ktor-server-auth-ldap:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-apache:2.3.7")
    implementation("io.ktor:ktor-server-csrf:2.3.7")
    implementation("io.ktor:ktor-server-sessions:2.3.7")
    implementation("io.github.cotrin8672:ktor-line-webhook-plugin:1.5.0")
    implementation("io.ktor:ktor-server-double-receive:2.3.7")
    implementation("com.kborowy:firebase-auth-provider:1.6.0")
    implementation("io.ktor:ktor-client-auth:2.3.7")
    implementation("io.ktor:ktor-client-auth:2.3.7")
    implementation("io.ktor:ktor-client-auth:2.3.7")
    implementation("io.ktor:ktor-client-encoding:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")
    implementation("io.ktor:ktor-client-resources:2.3.7")

    testImplementation(kotlin("test"))
}
plugins {
    kotlin("jvm") version "2.3.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
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
