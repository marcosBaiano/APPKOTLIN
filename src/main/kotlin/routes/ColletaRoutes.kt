package org.example.routes

import org.example.service.ColletaService
import org.example.service.UserService
import org.example.models.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.colletaRoutes(colletaService: ColletaService, userService: UserService) {

    // ========== ROTAS PÚBLICAS ==========

    // Listar todas as coletas (públicas)
    get("/coletas") {
        try {
            val coletas = colletaService.getAllColetas()
            call.respond(HttpStatusCode.OK, coletas)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao listar coletas: ${e.message}")
            )
        }
    }

    // Buscar coleta por ID
    get("/coletas/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "ID inválido")
                )
                return@get
            }
            val coleta = colletaService.getColletaById(id)
            if (coleta != null) {
                call.respond(HttpStatusCode.OK, coleta)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse(success = false, message = "Coleta não encontrada")
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao buscar coleta: ${e.message}")
            )
        }
    }

    // Buscar coletas por status
    get("/coletas/status/{status}") {
        try {
            val status = call.parameters["status"]
            if (status == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Status inválido")
                )
                return@get
            }
            val coletas = colletaService.getColetasByStatus(status)
            call.respond(HttpStatusCode.OK, coletas)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao listar coletas por status: ${e.message}")
            )
        }
    }

    // Obter estatísticas gerais
    get("/coletas/stats") {
        try {
            val stats = colletaService.getStatistics()
            call.respond(HttpStatusCode.OK, stats)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao obter estatísticas: ${e.message}")
            )
        }
    }

    // ========== ROTAS PROTEGIDAS COM JWT ==========

    authenticate("auth-jwt") {
        
        // Criar nova coleta
        post("/coletas") {
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

                val request = call.receive<org.example.models.CreateColletaRequest>()

                // Validações
                if (request.material.isBlank() || request.peso <= 0 || request.local.isBlank() || request.origem.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "Campos obrigatórios não preenchidos")
                    )
                    return@post
                }

                val coleta = colletaService.createColleta(
                    userId = user.id,
                    material = request.material,
                    peso = request.peso,
                    local = request.local,
                    origem = request.origem,
                    contato = request.contato,
                    fotoBase64 = request.fotoBase64
                )

                if (coleta != null) {
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            success = true,
                            message = "Coleta criada com sucesso",
                            data = mapOf("id" to coleta.id.toString())
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "Erro ao criar coleta")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }

        // Listar coletas do usuário autenticado
        get("/coletas/user") {
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

                val coletas = colletaService.getColetasByUserId(user.id)
                call.respond(HttpStatusCode.OK, coletas)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(success = false, message = "Erro ao listar coletas: ${e.message}")
                )
            }
        }

        // Atualizar status da coleta (Admin)
        put("/coletas/{id}/status") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@put
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "ID inválido")
                    )
                    return@put
                }

                val request = call.receive<org.example.models.UpdateColletaStatusRequest>()

                val success = colletaService.updateStatus(id, request.status)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Status atualizado com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao atualizar status")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }

        // Deletar coleta
        delete("/coletas/{id}") {
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

                val success = colletaService.deleteColleta(id)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Coleta deletada com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao deletar coleta")
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
