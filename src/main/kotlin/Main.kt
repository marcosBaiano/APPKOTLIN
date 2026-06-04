package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.Date

// ============================================================================
// DATA CLASSES - Requisições e Respostas
// ============================================================================

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class DeleteRequest(
    val userId: String,
    val password: String
)

@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Map<String, String>? = null
)

@Serializable
data class UserProfile(
    val email: String,
    val name: String,
    val createdAt: Long
)

// ============================================================================
// CONFIGURAÇÃO JWT
// ============================================================================

object JwtConfig {
    private const val SECRET = "your-secret-key-change-this-in-production-12345678"
    private const val ISSUER = "app-kotlin-api"
    private const val AUDIENCE = "app-users"
    private const val VALIDITY_IN_MS = 36_000_000 // 10 horas

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(email: String): String = JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
        .sign(algorithm)

    fun validateToken(token: String): String? = try {
        JWT.require(algorithm)
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .build()
            .verify(token)
            .getClaim("email")
            .asString()
    } catch (e: Exception) {
        null
    }

    fun getVerifier() = JWT.require(algorithm)
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()
}

// ============================================================================
// MODELO DE USUÁRIO
// ============================================================================

data class User(
    val email: String,
    val hashedPassword: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

// ============================================================================
// DATABASE (Em Memória)
// ============================================================================

val users = mutableMapOf<String, User>()

// ============================================================================
// FUNÇÕES DE SEGURANÇA
// ============================================================================

/**
 * Hash de senha com BCrypt
 */
fun hashPassword(password: String): String {
    val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
    return hashedPassword
}

/**
 * Verificar senha sem hash com hash armazenado
 */
fun verifyPassword(rawPassword: String, hashedPassword: String): Boolean {
    val result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword.toCharArray())
    return result.verified
}

// ============================================================================
// MAIN - Servidor Ktor
// ============================================================================

fun main() {
    // Inicializar banco de dados
    try {
        DatabaseConfig.init()
    } catch (e: Exception) {
        println("❌ Falha ao inicializar banco de dados!")
        return
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Configurar Ktor para lidar com JSON
        install(ContentNegotiation) {
            json()
        }

        // Configurar Autenticação JWT
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

        routing {
            // ========================================================================
            // ROTAS PÚBLICAS (Sem autenticação)
            // ========================================================================

            // Rota de Cadastro
            post("/users/register") {
                try {
                    val request = call.receive<RegisterRequest>()

                    // Validar dados
                    if (request.email.isBlank() || request.password.isBlank() || request.name.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(
                                success = false,
                                message = "Email, senha e nome são obrigatórios"
                            )
                        )
                        return@post
                    }

                    if (request.password.length < 6) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(
                                success = false,
                                message = "A senha deve ter pelo menos 6 caracteres"
                            )
                        )
                        return@post
                    }

                    // Verificar se usuário já existe (usando banco de dados)
                    if (UserRepository.emailExists(request.email)) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            ApiResponse(
                                success = false,
                                message = "Este email já está cadastrado"
                            )
                        )
                        return@post
                    }

                    // Hash da senha com BCrypt
                    val hashedPassword = hashPassword(request.password)

                    // Salvar usuário no banco de dados
                    val user = UserRepository.createUser(
                        email = request.email,
                        hashedPassword = hashedPassword,
                        name = request.name
                    )

                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            success = true,
                            message = "Usuário cadastrado com sucesso",
                            data = mapOf(
                                "email" to user.email,
                                "name" to user.name,
                                "id" to user.id.toString()
                            )
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(
                            success = false,
                            message = "Erro ao processar requisição: ${e.message}"
                        )
                    )
                }
            }

            // Rota de Login
            post("/users/login") {
                try {
                    val request = call.receive<LoginRequest>()

                    // Validar dados
                    if (request.email.isBlank() || request.password.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(
                                success = false,
                                message = "Email e senha são obrigatórios"
                            )
                        )
                        return@post
                    }

                    // Buscar usuário no banco de dados
                    val user = UserRepository.getUserByEmail(request.email)
                    if (user == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse(
                                success = false,
                                message = "Email ou senha incorretos"
                            )
                        )
                        return@post
                    }

                    // Verificar senha com BCrypt
                    if (!verifyPassword(request.password, user.hashedPassword)) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse(
                                success = false,
                                message = "Email ou senha incorretos"
                            )
                        )
                        return@post
                    }

                    // Gerar JWT Token
                    val token = JwtConfig.generateToken(user.email)

                    // Login bem-sucedido
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Login realizado com sucesso",
                            data = mapOf(
                                "email" to user.email,
                                "name" to user.name,
                                "id" to user.id.toString(),
                                "token" to token,
                                "expiresIn" to "10 horas"
                            )
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(
                            success = false,
                            message = "Erro ao processar requisição: ${e.message}"
                        )
                    )
                }
            }

            // Rota de Deletar Usuário
            post("/users/delete") {
                try {
                    val request = call.receive<DeleteRequest>()

                    // Validar dados
                    if (request.userId.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(
                                success = false,
                                message = "ID do usuário é obrigatório"
                            )
                        )
                        return@post
                    }

                    // Buscar usuário no banco de dados
                    val user = UserRepository.getUserByEmail(request.userId)
                    if (user == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse(
                                success = false,
                                message = "Usuário não encontrado"
                            )
                        )
                        return@post
                    }

                    // Verificar senha com BCrypt
                    if (!verifyPassword(request.password, user.hashedPassword)) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse(
                                success = false,
                                message = "Senha incorreta. Não é possível deletar o usuário"
                            )
                        )
                        return@post
                    }

                    // Deletar usuário do banco de dados
                    UserRepository.deleteUser(request.userId)

                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Usuário deletado com sucesso",
                            data = mapOf("deletedUser" to request.userId)
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(
                            success = false,
                            message = "Erro ao processar requisição: ${e.message}"
                        )
                    )
                }
            }

            // Rota de Health Check
            get("/health") {
                call.respond(
                    ApiResponse(
                        success = true,
                        message = "API está funcionando"
                    )
                )
            }

            // ========================================================================
            // ROTAS PRIVADAS (Requerem autenticação JWT)
            // ========================================================================

            authenticate("auth-jwt") {
                // Obter Perfil do Usuário
                get("/users/profile") {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal?.payload?.getClaim("email")?.asString()

                        if (email == null) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                ApiResponse(
                                    success = false,
                                    message = "Email não encontrado no token"
                                )
                            )
                            return@get
                        }

                        // Buscar usuário no banco de dados
                        val user = UserRepository.getUserByEmail(email)
                        if (user == null) {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ApiResponse(
                                    success = false,
                                    message = "Usuário não encontrado"
                                )
                            )
                            return@get
                        }

                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(
                                success = true,
                                message = "Perfil do usuário obtido com sucesso",
                                data = mapOf(
                                    "id" to user.id.toString(),
                                    "email" to user.email,
                                    "name" to user.name,
                                    "createdAt" to user.createdAt.toString()
                                )
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ApiResponse(
                                success = false,
                                message = "Erro ao obter perfil: ${e.message}"
                            )
                        )
                    }
                }

                // Atualizar Perfil
                put("/users/profile") {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal?.payload?.getClaim("email")?.asString()

                        if (email == null) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                ApiResponse(
                                    success = false,
                                    message = "Email não encontrado no token"
                                )
                            )
                            return@put
                        }

                        // Buscar usuário no banco de dados
                        val user = UserRepository.getUserByEmail(email)
                        if (user == null) {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ApiResponse(
                                    success = false,
                                    message = "Usuário não encontrado"
                                )
                            )
                            return@put
                        }

                        @Serializable
                        data class UpdateProfileRequest(val name: String)

                        val request = call.receive<UpdateProfileRequest>()

                        if (request.name.isBlank()) {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse(
                                    success = false,
                                    message = "Nome não pode estar vazio"
                                )
                            )
                            return@put
                        }

                        // Atualizar no banco de dados
                        UserRepository.updateUserName(email, request.name)
                        val updatedUser = UserRepository.getUserByEmail(email)!!

                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(
                                success = true,
                                message = "Perfil atualizado com sucesso",
                                data = mapOf(
                                    "email" to updatedUser.email,
                                    "name" to updatedUser.name
                                )
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(
                                success = false,
                                message = "Erro ao atualizar perfil: ${e.message}"
                            )
                        )
                    }
                }

                // Alterar Senha
                post("/users/change-password") {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                        val email = principal?.payload?.getClaim("email")?.asString()

                        if (email == null) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                ApiResponse(
                                    success = false,
                                    message = "Email não encontrado no token"
                                )
                            )
                            return@post
                        }

                        @Serializable
                        data class ChangePasswordRequest(
                            val currentPassword: String,
                            val newPassword: String
                        )

                        val request = call.receive<ChangePasswordRequest>()
                        val user = UserRepository.getUserByEmail(email)

                        if (user == null) {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ApiResponse(
                                    success = false,
                                    message = "Usuário não encontrado"
                                )
                            )
                            return@post
                        }

                        // Verificar senha atual
                        if (!verifyPassword(request.currentPassword, user.hashedPassword)) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                ApiResponse(
                                    success = false,
                                    message = "Senha atual incorreta"
                                )
                            )
                            return@post
                        }

                        // Validar nova senha
                        if (request.newPassword.length < 6) {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse(
                                    success = false,
                                    message = "A nova senha deve ter pelo menos 6 caracteres"
                                )
                            )
                            return@post
                        }

                        // Hash da nova senha e atualizar no banco
                        val hashedNewPassword = hashPassword(request.newPassword)
                        UserRepository.updatePassword(email, hashedNewPassword)

                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(
                                success = true,
                                message = "Senha alterada com sucesso"
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(
                                success = false,
                                message = "Erro ao alterar senha: ${e.message}"
                            )
                        )
                    }
                }

                // Listar todos os usuários (apenas para teste/admin)
                get("/users/list") {
                    try {
                        val allUsers = UserRepository.getAllUsers()
                        val userList = allUsers.map { user ->
                            mapOf(
                                "id" to user.id.toString(),
                                "email" to user.email,
                                "name" to user.name,
                                "createdAt" to user.createdAt.toString()
                            )
                        }

                        call.respond(
                            HttpStatusCode.OK,
                            mapOf(
                                "success" to true,
                                "message" to "Lista de usuários obtida com sucesso",
                                "count" to userList.size,
                                "data" to userList
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ApiResponse(
                                success = false,
                                message = "Erro ao listar usuários: ${e.message}"
                            )
                        )
                    }
                }
            }
        }
    }.start(wait = true)
}

