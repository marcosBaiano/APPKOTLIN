package org.example.database


import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 150).uniqueIndex()
    val password = varchar("password", 255)
    val avatarUrl = varchar("avatar_url", 255).nullable()
    val termsAccepted = bool("terms_accepted").default(false)
    val termsAcceptedAt = datetime("terms_accepted_at").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}
