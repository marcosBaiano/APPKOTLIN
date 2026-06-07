package org.example.service

import org.example.repository.PostRepository
import org.jetbrains.exposed.sql.ResultRow

class PostService(private val postRepository: PostRepository) {
    fun addPost(userId: Int, imageUrl: String, caption: String?): Boolean {
        return try {
            postRepository.addPost(userId, imageUrl, caption)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getPostsByUser(userId: Int): List<ResultRow> = postRepository.getPostsByUser(userId)

    fun getAllPosts(): List<ResultRow> = postRepository.getAllPosts()

    fun deletePost(postId: Int): Boolean {
        return try {
            postRepository.deletePost(postId)
            true
        } catch (e: Exception) {
            false
        }
    }
}
