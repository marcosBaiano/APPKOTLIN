package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.staticfiles.*
import java.io.File

import org.example.config.DatabaseConfig
import org.example.config.JwtConfig
import org.example.repository.*
import org.example.service.*
import org.example.routes.*
import org.example.models.ApiResponse

/**
 * MAIN - Único ponto de entrada da aplicação
 */
fun main() {
    // Inicializar banco de dados
    try {
        DatabaseConfig.init()
        println("✅ Banco de dados inicializado com sucesso!")
    } catch (e: Exception) {
        println("❌ Falha ao inicializar banco de dados!")
        return
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureServer()
    }.start(wait = true)
}

/**
 * Configuração do servidor Ktor
 */
fun Application.configureServer() {
    // Configuração de CORS para aceitar requisições do frontend
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    // Configuração de JSON
    install(ContentNegotiation) {
        json()
    }

    // Configuração de Autenticação JWT
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "app-kotlin-api"
            verifier(JwtConfig.getVerifier())
            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                if (email != null && email.isNotBlank()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiResponse(
                        success = false,
                        message = "Token inválido ou expirado"
                    )
                )
            }
        }
    }

    // Inicializar repositories e services
    val userRepository = UserRepository
    val postRepository = PostRepository
    val commentRepository = CommentRepository
    val notificationRepository = NotificationRepository
    val colletaRepository = ColletaRepository
    val depoimentoRepository = DepoimentoRepository

    val userService = UserService(userRepository)
    val postService = PostService(postRepository)
    val commentService = CommentService(commentRepository)
    val notificationService = NotificationService(notificationRepository)
    val colletaService = ColletaService(colletaRepository)
    val depoimentoService = DepoimentoService(depoimentoRepository)

    // Configurar rotas
    routing {
        // Servir arquivos estáticos (HTML, CSS, JS)
        staticResources("/", "static") {
            default("index.html")
        }

        // Health check da API
        get("/api/health") {
            call.respond(
                ApiResponse(
                    success = true,
                    message = "API está funcionando"
                )
            )
        }

        // Incluir todas as rotas da API
        userRoutes(userService)
        postRoutes(postService)
        commentRoutes(commentService)
        notificationRoutes(notificationService)
        colletaRoutes(colletaService, userService)
        depoimentoRoutes(depoimentoService, userService)
    }

    println("✅ Servidor Ktor iniciado em http://0.0.0.0:8080")
    println("📡 Frontend disponível em http://0.0.0.0:8080/")
    println("🔌 API disponível em http://0.0.0.0:8080/api/")
}

