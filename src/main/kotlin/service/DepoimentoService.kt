package org.example.service

import org.example.repository.DepoimentoRepository
import org.example.models.DepoimentoEntity

class DepoimentoService(private val depoimentoRepository: DepoimentoRepository = DepoimentoRepository) {

    fun createDepoimento(
        userId: Int,
        titulo: String,
        mensagem: String
    ): DepoimentoEntity? {
        return try {
            depoimentoRepository.createDepoimento(userId, titulo, mensagem)
        } catch (e: Exception) {
            null
        }
    }

    fun getDepoimentoById(id: Int): DepoimentoEntity? = depoimentoRepository.getDepoimentoById(id)

    fun getAllDepoimentos(): List<DepoimentoEntity> = depoimentoRepository.getAllDepoimentos()

    fun getDepoimentosByUserId(userId: Int): List<DepoimentoEntity> = depoimentoRepository.getDepoimentosByUserId(userId)

    fun deleteDepoimento(id: Int): Boolean {
        return try {
            depoimentoRepository.deleteDepoimento(id)
        } catch (e: Exception) {
            false
        }
    }
}
