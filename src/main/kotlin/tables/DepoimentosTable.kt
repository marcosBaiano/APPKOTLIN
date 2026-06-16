package org.example.tables

import org.jetbrains.exposed.sql.Table

/**
 * Definição da tabela DEPOIMENTOS (Mural de Impacto Social)
 */
object DepoimentosTable : Table("depoimentos") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references UsersTable.id
    val titulo = varchar("titulo", 255)
    val mensagem = text("mensagem")
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}
