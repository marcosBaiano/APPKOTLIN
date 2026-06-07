package org.example.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.example.database.PostsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class PostRepository {
    fun addPost(userId: Int, imageUrl: String, caption: String?) {
        transaction {
            PostsTable.insert {
                it[PostsTable.userId] = userId.toLong()
                it[PostsTable.imageUrl] = imageUrl
                it[PostsTable.caption] = caption
                it[PostsTable.createdAt] = CurrentDateTime()
            }
        }
    }

    fun getPostsByUser(userId: Int): List<ResultRow> = transaction {
        PostsTable.select { PostsTable.userId eq userId.toLong() }.toList()
    }

    fun getAllPosts(): List<ResultRow> = transaction {
        PostsTable.selectAll().toList()
    }

    fun deletePost(postId: Int) {
        transaction {
            PostsTable.deleteWhere { PostsTable.id eq postId.toLong() }
        }
    }
}
