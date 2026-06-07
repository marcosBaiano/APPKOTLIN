package org.example.service

import org.example.repository.NotificationRepository
import org.jetbrains.exposed.sql.ResultRow

class NotificationService(private val notificationRepository: NotificationRepository) {
    fun addNotification(userId: Int, message: String, type: String): Boolean {
        return try {
            notificationRepository.addNotification(userId, message, type)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getNotificationsByUser(userId: Int): List<ResultRow> = notificationRepository.getNotificationsByUser(userId)

    fun markAsRead(notificationId: Int): Boolean {
        return try {
            notificationRepository.markAsRead(notificationId)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteNotification(notificationId: Int): Boolean {
        return try {
            notificationRepository.deleteNotification(notificationId)
            true
        } catch (e: Exception) {
            false
        }
    }
}
