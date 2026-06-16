package org.example.service

import org.example.repository.ColletaRepository
import org.example.models.ColletaEntity

class ColletaService(private val colletaRepository: ColletaRepository = ColletaRepository) {

    fun createColleta(
        userId: Int,
        material: String,
        peso: Int,
        local: String,
        origem: String,
        contato: String,
        fotoBase64: String?
    ): ColletaEntity? {
        return try {
            colletaRepository.createColleta(userId, material, peso, local, origem, contato, fotoBase64)
        } catch (e: Exception) {
            null
        }
    }

    fun getColletaById(id: Int): ColletaEntity? = colletaRepository.getColletaById(id)

    fun getAllColetas(): List<ColletaEntity> = colletaRepository.getAllColetas()

    fun getColetasByUserId(userId: Int): List<ColletaEntity> = colletaRepository.getColetasByUserId(userId)

    fun getColetasByStatus(status: String): List<ColletaEntity> = colletaRepository.getColetasByStatus(status)

    fun updateStatus(id: Int, newStatus: String): Boolean {
        return try {
            colletaRepository.updateStatus(id, newStatus)
        } catch (e: Exception) {
            false
        }
    }

    fun deleteColleta(id: Int): Boolean {
        return try {
            colletaRepository.deleteColleta(id)
        } catch (e: Exception) {
            false
        }
    }

    fun getStatistics(): Map<String, Any> {
        val allColetas = getAllColetas()
        val totalWeight = allColetas.sumOf { it.peso }
        val activeColetas = allColetas.filter { it.status != "Finalizada" }.size
        val co2Saved = (totalWeight * 2.7) / 1000  // Cálculo de CO2 economizado

        return mapOf(
            "totalWeight" to totalWeight,
            "activeColetas" to activeColetas,
            "co2Saved" to String.format("%.2f", co2Saved),
            "totalColetas" to allColetas.size
        )
    }
}
