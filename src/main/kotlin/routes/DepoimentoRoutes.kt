package org.example.routes

import org.example.service.DepoimentoService
import org.example.service.UserService
import org.example.models.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.depoimentoRoutes(depoimentoService: DepoimentoService, userService: UserService) {

    // ========== ROTAS PÚBLICAS ==========

    // Listar todos os depoimentos
    get("/depoimentos") {
        try {
            val depoimentos = depoimentoService.getAllDepoimentos()
            call.respond(HttpStatusCode.OK, depoimentos)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao listar depoimentos: ${e.message}")
            )
        }
    }

    // Buscar depoimento por ID
    get("/depoimentos/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "ID inválido")
                )
                return@get
            }
            val depoimento = depoimentoService.getDepoimentoById(id)
            if (depoimento != null) {
                call.respond(HttpStatusCode.OK, depoimento)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse(success = false, message = "Depoimento não encontrado")
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao buscar depoimento: ${e.message}")
            )
        }
    }

    // ========== ROTAS PROTEGIDAS COM JWT ==========

    authenticate("auth-jwt") {
        
        // Criar novo depoimento
        post("/depoimentos") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@post
                }

                val email = principal.payload.getClaim("email").asString()
                val user = userService.getUserByEmail(email)
                
                if (user == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Usuário não encontrado")
                    )
                    return@post
                }

                val request = call.receive<org.example.models.CreateDepoimentoRequest>()

                // Validações
                if (request.titulo.isBlank() || request.mensagem.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "Título e mensagem são obrigatórios")
                    )
                    return@post
                }

                val depoimento = depoimentoService.createDepoimento(
                    userId = user.id,
                    titulo = request.titulo,
                    mensagem = request.mensagem
                )

                if (depoimento != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            success = true,
                            message = "Depoimento criado com sucesso",
                            data = mapOf("id" to depoimento.id.toString())
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "Erro ao criar depoimento")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }

        // Listar depoimentos do usuário autenticado
        get("/depoimentos/user") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@get
                }

                val email = principal.payload.getClaim("email").asString()
                val user = userService.getUserByEmail(email)
                
                if (user == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Usuário não encontrado")
                    )
                    return@get
                }

                val depoimentos = depoimentoService.getDepoimentosByUserId(user.id)
                call.respond(HttpStatusCode.OK, depoimentos)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(success = false, message = "Erro ao listar depoimentos: ${e.message}")
                )
            }
        }

        // Deletar depoimento
        delete("/depoimentos/{id}") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@delete
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "ID inválido")
                    )
                    return@delete
                }

                val success = depoimentoService.deleteDepoimento(id)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Depoimento deletado com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao deletar depoimento")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }
    }
}
