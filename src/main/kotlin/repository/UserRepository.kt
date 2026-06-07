package org.example.repository

import org.example.database.UsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.CurrentDateTime


class UserRepository {
    fun addUser(name: String, email: String, password: String, avatarUrl: String? = null) {
        transaction {
            UsersTable.insert {
                it[UsersTable.name] = name
                it[UsersTable.email] = email
                it[UsersTable.password] = password
                it[UsersTable.avatarUrl] = avatarUrl
                it[UsersTable.termsAccepted] = false
                it[UsersTable.createdAt] = CurrentDateTime()
            }
        }
    }

    fun findUserById(userId: Int): ResultRow? = transaction {
        UsersTable.select { UsersTable.id eq userId.toLong() }.singleOrNull()
    }

    fun deleteUser(userId: Int) {
        transaction {
            UsersTable.deleteWhere { UsersTable.id eq userId.toLong() }
        }
    }
}
