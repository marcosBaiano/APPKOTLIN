package org.example.database


import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object NotificationsTable : Table("notifications") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references UsersTable.id
    val message = varchar("message", 255)
    val type = varchar("type", 50)
    val status = varchar("status", 20).default("unread")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
