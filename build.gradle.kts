plugins {
    kotlin("jvm") version "2.0.0"
    application
}

application {
    mainClass.set("org.example.ApplicationKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // 🔹 Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-server-auth:2.3.12")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.12")
    implementation("io.ktor:ktor-server-cors:2.3.12")
    implementation("io.ktor:ktor-server-default-headers:2.3.12")
    implementation("io.ktor:ktor-server-compression:2.3.12")
    implementation("io.ktor:ktor-server-caching-headers:2.3.12")
    implementation("io.ktor:ktor-server-sessions:2.3.12")
    implementation("io.ktor:ktor-server-swagger:2.3.12")

    // 🔹 JSON serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

    // 🔹 Security - Bcrypt for password hashing
    implementation("at.favre.lib:bcrypt:0.9.0")

    // 🔹 JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // 🔹 Database - Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.50.0")

    // 🔹 MySQL Driver
    implementation("mysql:mysql-connector-java:8.0.33")

    // 🔹 Connection Pool
    implementation("com.zaxxer:HikariCP:5.0.1")

    // 🔹 Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // 🔹 Testes
    testImplementation(kotlin("test"))
} //