package org.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.example.tables.DepoimentosTable

/**
 * Classe de dados representando um Depoimento (Mural de Impacto Social)
 */
@Serializable
data class DepoimentoEntity(
    val id: Int,
    val userId: Int,
    val titulo: String,
    val mensagem: String,
    val createdAt: Long
) {
    companion object {
        fun fromRow(row: ResultRow): DepoimentoEntity {
            return DepoimentoEntity(
                id = row[DepoimentosTable.id],
                userId = row[DepoimentosTable.userId],
                titulo = row[DepoimentosTable.titulo],
                mensagem = row[DepoimentosTable.mensagem],
                createdAt = row[DepoimentosTable.createdAt]
            )
        }
    }
}

/**
 * DTOs para requisições de Depoimento
 */
@Serializable
data class CreateDepoimentoRequest(
    val titulo: String,
    val mensagem: String
)
