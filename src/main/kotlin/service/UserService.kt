package org.example.service

import org.example.repository.UserRepository
import org.jetbrains.exposed.sql.ResultRow

class UserService(private val userRepository: UserRepository) {
    fun addUser(name: String, email: String, password: String, avatarUrl: String? = null): Boolean {
        return try {
            userRepository.addUser(name, email, password, avatarUrl)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun findUserById(userId: Int): ResultRow? = userRepository.findUserById(userId)

    fun deleteUser(userId: Int): Boolean {
        return try {
            userRepository.deleteUser(userId)
            true
        } catch (e: Exception) {
            false
        }
    }
}
