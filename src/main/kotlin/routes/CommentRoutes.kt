package org.example.routes

import org.example.service.CommentService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Route.commentRoutes(commentService: CommentService) {
    get("/comments/{postId}") {
        val postId = call.parameters["postId"]?.toIntOrNull()
        if (postId == null) {
            call.respond(HttpStatusCode.BadRequest, "PostId inválido")
            return@get
        }
        val comments = commentService.getCommentsByPost(postId)
        call.respond(HttpStatusCode.OK, comments)
    }

    delete("/comments/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "ID inválido")
            return@delete
        }
        val success = commentService.deleteComment(id)
        if (success) {
            call.respond(HttpStatusCode.OK, "Comentário deletado")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar comentário")
        }
    }
}
