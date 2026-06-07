package org.example.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CommentsTable : Table("comments") {
    val id = integer("id").autoIncrement()
    val postId = integer("post_id") references PostsTable.id
    val userId = integer("user_id") references UsersTable.id
    val text = varchar("text", 500)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
