package org.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.example.tables.ColletasTable

/**
 * Classe de dados representando uma Coleta (Solicitação de Logística Reversa)
 */
@Serializable
data class ColletaEntity(
    val id: Int,
    val userId: Int,
    val material: String,
    val peso: Int,
    val local: String,
    val origem: String,
    val contato: String,
    val status: String,
    val fotoBase64: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromRow(row: ResultRow): ColletaEntity {
            return ColletaEntity(
                id = row[ColletasTable.id],
                userId = row[ColletasTable.userId],
                material = row[ColletasTable.material],
                peso = row[ColletasTable.peso],
                local = row[ColletasTable.local],
                origem = row[ColletasTable.origem],
                contato = row[ColletasTable.contato],
                status = row[ColletasTable.status],
                fotoBase64 = row[ColletasTable.fotoBase64],
                createdAt = row[ColletasTable.createdAt],
                updatedAt = row[ColletasTable.updatedAt]
            )
        }
    }
}

/**
 * DTOs para requisições de Coleta
 */
@Serializable
data class CreateColletaRequest(
    val material: String,
    val peso: Int,
    val local: String,
    val origem: String,
    val contato: String,
    val fotoBase64: String?
)

@Serializable
data class UpdateColletaStatusRequest(
    val status: String
)
