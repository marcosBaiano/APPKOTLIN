package org.example.repository

import org.example.tables.DepoimentosTable
import org.example.models.DepoimentoEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * Repository para operações CRUD de Depoimentos
 */
object DepoimentoRepository {

    /**
     * CREATE - Inserir novo depoimento
     */
    fun createDepoimento(
        userId: Int,
        titulo: String,
        mensagem: String
    ): DepoimentoEntity {
        return transaction {
            val now = System.currentTimeMillis()
            DepoimentosTable.insert {
                it[DepoimentosTable.userId] = userId
                it[DepoimentosTable.titulo] = titulo
                it[DepoimentosTable.mensagem] = mensagem
                it[DepoimentosTable.createdAt] = now
            }

            DepoimentosTable.selectAll()
                .orderBy(DepoimentosTable.createdAt to SortOrder.DESC)
                .limit(1)
                .map { DepoimentoEntity.fromRow(it) }
                .first()
        }
    }

    /**
     * READ - Buscar depoimento por ID
     */
    fun getDepoimentoById(id: Int): DepoimentoEntity? {
        return transaction {
            DepoimentosTable.select { DepoimentosTable.id eq id }
                .map { DepoimentoEntity.fromRow(it) }
                .firstOrNull()
        }
    }

    /**
     * READ - Buscar todos os depoimentos
     */
    fun getAllDepoimentos(): List<DepoimentoEntity> {
        return transaction {
            DepoimentosTable.selectAll()
                .orderBy(DepoimentosTable.createdAt to SortOrder.DESC)
                .map { DepoimentoEntity.fromRow(it) }
        }
    }

    /**
     * READ - Buscar depoimentos de um usuário
     */
    fun getDepoimentosByUserId(userId: Int): List<DepoimentoEntity> {
        return transaction {
            DepoimentosTable.select { DepoimentosTable.userId eq userId }
                .orderBy(DepoimentosTable.createdAt to SortOrder.DESC)
                .map { DepoimentoEntity.fromRow(it) }
        }
    }

    /**
     * DELETE - Deletar depoimento por ID
     */
    fun deleteDepoimento(id: Int): Boolean {
        return transaction {
            val deleted = DepoimentosTable.deleteWhere { (DepoimentosTable.id eq id) }
            deleted > 0
        }
    }
}
