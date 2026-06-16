package org.example.repository

import org.example.tables.ColletasTable
import org.example.models.ColletaEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * Repository para operações CRUD de Coletas
 */
object ColletaRepository {

    /**
     * CREATE - Inserir nova coleta
     */
    fun createColleta(
        userId: Int,
        material: String,
        peso: Int,
        local: String,
        origem: String,
        contato: String,
        fotoBase64: String?
    ): ColletaEntity {
        return transaction {
            val now = System.currentTimeMillis()
            ColletasTable.insert {
                it[ColletasTable.userId] = userId
                it[ColletasTable.material] = material
                it[ColletasTable.peso] = peso
                it[ColletasTable.local] = local
                it[ColletasTable.origem] = origem
                it[ColletasTable.contato] = contato
                it[ColletasTable.fotoBase64] = fotoBase64
                it[ColletasTable.status] = "Pendente"
                it[ColletasTable.createdAt] = now
                it[ColletasTable.updatedAt] = now
            }

            ColletasTable.selectAll()
                .orderBy(ColletasTable.createdAt to SortOrder.DESC)
                .limit(1)
                .map { ColletaEntity.fromRow(it) }
                .first()
        }
    }

    /**
     * READ - Buscar coleta por ID
     */
    fun getColletaById(id: Int): ColletaEntity? {
        return transaction {
            ColletasTable.select { ColletasTable.id eq id }
                .map { ColletaEntity.fromRow(it) }
                .firstOrNull()
        }
    }

    /**
     * READ - Buscar todas as coletas
     */
    fun getAllColetas(): List<ColletaEntity> {
        return transaction {
            ColletasTable.selectAll()
                .orderBy(ColletasTable.createdAt to SortOrder.DESC)
                .map { ColletaEntity.fromRow(it) }
        }
    }

    /**
     * READ - Buscar coletas de um usuário específico
     */
    fun getColetasByUserId(userId: Int): List<ColletaEntity> {
        return transaction {
            ColletasTable.select { ColletasTable.userId eq userId }
                .orderBy(ColletasTable.createdAt to SortOrder.DESC)
                .map { ColletaEntity.fromRow(it) }
        }
    }

    /**
     * READ - Buscar coletas por status
     */
    fun getColetasByStatus(status: String): List<ColletaEntity> {
        return transaction {
            ColletasTable.select { ColletasTable.status eq status }
                .orderBy(ColletasTable.createdAt to SortOrder.DESC)
                .map { ColletaEntity.fromRow(it) }
        }
    }

    /**
     * UPDATE - Atualizar status da coleta
     */
    fun updateStatus(id: Int, newStatus: String): Boolean {
        return transaction {
            val updated = ColletasTable.update({ ColletasTable.id eq id }) {
                it[ColletasTable.status] = newStatus
                it[ColletasTable.updatedAt] = System.currentTimeMillis()
            }
            updated > 0
        }
    }

    /**
     * DELETE - Deletar coleta por ID
     */
    fun deleteColleta(id: Int): Boolean {
        return transaction {
            val deleted = ColletasTable.deleteWhere { (ColletasTable.id eq id) }
            deleted > 0
        }
    }
}
