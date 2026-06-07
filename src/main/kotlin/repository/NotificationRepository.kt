package org.example.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.example.database.NotificationsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class NotificationRepository {
    fun addNotification(userId: Int, message: String, type: String) {
        transaction {
            NotificationsTable.insert {
                it[NotificationsTable.userId] = userId.toLong()
                it[NotificationsTable.message] = message
                it[NotificationsTable.type] = type
                it[NotificationsTable.status] = "unread"
                it[NotificationsTable.createdAt] = CurrentDateTime()
            }
        }
    }

    fun getNotificationsByUser(userId: Int): List<ResultRow> = transaction {
        NotificationsTable.select { NotificationsTable.userId eq userId.toLong() }.toList()
    }

    fun markAsRead(notificationId: Int) {
        transaction {
            NotificationsTable.update({ NotificationsTable.id eq notificationId.toLong() }) {
                it[NotificationsTable.status] = "read"
            }
        }
    }

    fun deleteNotification(notificationId: Int) {
        transaction {
            NotificationsTable.deleteWhere { NotificationsTable.id eq notificationId.toLong() }
        }
    }
}
