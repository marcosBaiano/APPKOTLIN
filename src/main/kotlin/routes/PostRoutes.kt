package org.example.routes

import org.example.service.PostService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Route.postRoutes(postService: PostService) {
    get("/posts") {
        val posts = postService.getAllPosts()
        call.respond(HttpStatusCode.OK, posts)
    }

    get("/posts/{userId}") {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "UserId inválido")
            return@get
        }
        val posts = postService.getPostsByUser(userId)
        call.respond(HttpStatusCode.OK, posts)
    }

    delete("/posts/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "ID inválido")
            return@delete
        }
        val success = postService.deletePost(id)
        if (success) {
            call.respond(HttpStatusCode.OK, "Post deletado")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar post")
        }
    }
}
