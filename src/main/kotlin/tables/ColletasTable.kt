package org.example.tables

import org.jetbrains.exposed.sql.Table

/**
 * Definição da tabela COLETAS (Logística Reversa de EPS/MDF)
 */
object ColletasTable : Table("coletas") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references UsersTable.id
    val material = varchar("material", 100)  // EPS Isopor, MDF / Resíduos, Misto
    val peso = integer("peso")  // em kg
    val local = varchar("local", 100)  // Salvador, Lauro de Freitas, Camaçari, etc
    val origem = varchar("origem", 255)  // Nome do ponto de coleta
    val contato = varchar("contato", 255)  // Telefone/pessoa de contato
    val status = varchar("status", 50).default("Pendente")  // Pendente, Em Trânsito, Finalizada
    val fotoBase64 = text("foto_base64").nullable()  // Foto em base64
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}
