package org.example.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.example.database.CommentsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction



class CommentRepository {
    fun addComment(postId: Int, userId: Int, text: String) {
        transaction {
            CommentsTable.insert {
                it[CommentsTable.postId] = postId.toLong()
                it[CommentsTable.userId] = userId.toLong()
                it[CommentsTable.text] = text
                it[CommentsTable.createdAt] = CurrentDateTime()
            }
        }
    }

    fun getCommentsByPost(postId: Int): List<ResultRow> = transaction {
        CommentsTable.select { CommentsTable.postId eq postId.toLong() }.toList()
    }

    fun deleteComment(commentId: Int) {
        transaction {
            CommentsTable.deleteWhere { CommentsTable.id eq commentId.toLong() }
        }
    }
}
