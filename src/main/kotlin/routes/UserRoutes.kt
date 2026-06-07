package org.example.routes

import org.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Route.userRoutes(userService: UserService) {
    get("/users/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "ID inválido")
            return@get
        }
        val user = userService.findUserById(id)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound, "Usuário não encontrado")
        }
    }

    delete("/users/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "ID inválido")
            return@delete
        }
        val success = userService.deleteUser(id)
        if (success) {
            call.respond(HttpStatusCode.OK, "Usuário deletado")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar usuário")
        }
    }
}
