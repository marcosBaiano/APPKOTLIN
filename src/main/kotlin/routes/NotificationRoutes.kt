package org.example.routes

import org.example.service.NotificationService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Route.notificationRoutes(notificationService: NotificationService) {
    get("/notifications/{userId}") {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "UserId inválido")
            return@get
        }
        val notifications = notificationService.getNotificationsByUser(userId)
        call.respond(HttpStatusCode.OK, notifications)
    }

    put("/notifications/{id}/read") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "ID inválido")
            return@put
        }
        val success = notificationService.markAsRead(id)
        if (success) {
            call.respond(HttpStatusCode.OK, "Notificação marcada como lida")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar notificação")
        }
    }

    delete("/notifications/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "ID inválido")
            return@delete
        }
        val success = notificationService.deleteNotification(id)
        if (success) {
            call.respond(HttpStatusCode.OK, "Notificação deletada")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar notificação")
        }
    }
}
