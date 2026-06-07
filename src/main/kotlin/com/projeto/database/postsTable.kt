package org.example.database


import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object PostsTable : Table("posts") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references UsersTable.id
    val imageUrl = varchar("image_url", 255)
    val caption = varchar("caption", 500).nullable()
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
