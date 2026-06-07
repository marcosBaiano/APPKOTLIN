package org.example.service

import org.example.repository.CommentRepository
import org.jetbrains.exposed.sql.ResultRow

class CommentService(private val commentRepository: CommentRepository) {
    fun addComment(postId: Int, userId: Int, text: String): Boolean {
        return try {
            commentRepository.addComment(postId, userId, text)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCommentsByPost(postId: Int): List<ResultRow> = commentRepository.getCommentsByPost(postId)

    fun deleteComment(commentId: Int): Boolean {
        return try {
            commentRepository.deleteComment(commentId)
            true
        } catch (e: Exception) {
            false
        }
    }
}
