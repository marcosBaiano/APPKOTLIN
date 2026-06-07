package org.example.database


import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/users",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "edneiaSQL"
        )

        transaction {
            SchemaUtils.create(UsersTable, PostsTable, CommentsTable, NotificationsTable)
        }
    }
}
