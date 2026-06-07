package org.example

import org.example.repository.*
import org.example.service.*
import org.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureServer()
    }.start(wait = true)
}

fun Application.configureServer() {
    install(ContentNegotiation) { json() }

    val userService = UserService(UserRepository())
    val postService = PostService(PostRepository())
    val commentService = CommentService(CommentRepository())
    val notificationService = NotificationService(NotificationRepository())

    routing {
        userRoutes(userService)
        postRoutes(postService)
        commentRoutes(commentService)
        notificationRoutes(notificationService)
    }
}
