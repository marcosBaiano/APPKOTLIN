package org.example

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

/**
 * Configuração do banco de dados MySQL
 */
object DatabaseConfig {

    // Configurações do banco de dados
    private const val DB_URL = "jdbc:mysql://localhost:3306/appkotlin"
    private const val DB_USER = "root"
    private const val DB_PASSWORD = "root"  // ⚠️ MUDAR EM PRODUÇÃO!
    private const val DB_DRIVER = "com.mysql.cj.jdbc.Driver"

    /**
     * Inicializar conexão com banco de dados
     */
    fun init() {
        try {
            // Configurar HikariCP para pooling de conexões
            val config = HikariConfig().apply {
                jdbcUrl = DB_URL
                username = DB_USER
                password = DB_PASSWORD
                driverClassName = DB_DRIVER
                maximumPoolSize = 10
                minimumIdle = 2
                connectionTimeout = 30000
                idleTimeout = 600000
                maxLifetime = 1800000
            }

            val hikariDataSource = HikariDataSource(config)

            // Conectar ao banco
            Database.connect(hikariDataSource)

            println("✅ Conectado ao banco de dados MySQL com sucesso!")

            // Criar tabelas se não existirem
            createTables()

        } catch (e: Exception) {
            println("❌ Erro ao conectar ao banco de dados: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Criar tabelas do banco de dados
     */
    private fun createTables() {
        try {
            transaction {
                SchemaUtils.create(UsersTable)
                println("✅ Tabelas criadas/verificadas com sucesso!")
            }
        } catch (e: Exception) {
            println("⚠️  Erro ao criar tabelas: ${e.message}")
        }
    }

    /**
     * Testar conexão com banco de dados
     */
    fun testConnection(): Boolean {
        return try {
            transaction {
                UsersTable.selectAll().limit(1).toList()
                println("✅ Conexão com banco de dados testada com sucesso!")
                true
            }
        } catch (e: Exception) {
            println("❌ Erro ao testar conexão: ${e.message}")
            false
        }
    }
}

/**
 * Definição da tabela USERS
 */
object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val name = varchar("name", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}

/**
 * Classe de dados representando um usuário
 */
data class UserEntity(
    val id: Int,
    val email: String,
    val hashedPassword: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromRow(row: ResultRow): UserEntity {
            return UserEntity(
                id = row[UsersTable.id],
                email = row[UsersTable.email],
                hashedPassword = row[UsersTable.hashedPassword],
                name = row[UsersTable.name],
                createdAt = row[UsersTable.createdAt],
                updatedAt = row[UsersTable.updatedAt]
            )
        }
    }
}

/**
 * Repository para operações CRUD
 */
object UserRepository {

    /**
     * CREATE - Inserir novo usuário
     */
    fun createUser(email: String, hashedPassword: String, name: String): UserEntity {
        return transaction {
            val now = System.currentTimeMillis()
            UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.hashedPassword] = hashedPassword
                it[UsersTable.name] = name
                it[UsersTable.createdAt] = now
                it[UsersTable.updatedAt] = now
            }

            UsersTable.select { UsersTable.email eq email }
                .map { UserEntity.fromRow(it) }
                .first()
        }
    }

    /**
     * READ - Buscar usuário por email
     */
    fun getUserByEmail(email: String): UserEntity? {
        return transaction {
            UsersTable.select { UsersTable.email eq email }
                .map { UserEntity.fromRow(it) }
                .firstOrNull()
        }
    }

    /**
     * READ - Buscar usuário por ID
     */
    fun getUserById(id: Int): UserEntity? {
        return transaction {
            UsersTable.select { UsersTable.id eq id }
                .map { UserEntity.fromRow(it) }
                .firstOrNull()
        }
    }

    /**
     * READ - Buscar todos os usuários
     */
    fun getAllUsers(): List<UserEntity> {
        return transaction {
            UsersTable.selectAll()
                .map { UserEntity.fromRow(it) }
        }
    }

    /**
     * UPDATE - Atualizar nome do usuário
     */
    fun updateUserName(email: String, newName: String): Boolean {
        return transaction {
            val updated = UsersTable.update({ UsersTable.email eq email }) {
                it[UsersTable.name] = newName
                it[UsersTable.updatedAt] = System.currentTimeMillis()
            }
            updated > 0
        }
    }

    /**
     * UPDATE - Atualizar senha do usuário
     */
    fun updatePassword(email: String, newHashedPassword: String): Boolean {
        return transaction {
            val updated = UsersTable.update({ UsersTable.email eq email }) {
                it[UsersTable.hashedPassword] = newHashedPassword
                it[UsersTable.updatedAt] = System.currentTimeMillis()
            }
            updated > 0
        }
    }

    /**
     * DELETE - Deletar usuário por email
     */
    fun deleteUser(email: String): Boolean {
        return transaction {
            val deleted = UsersTable.deleteWhere { (UsersTable.email eq email) }
            deleted > 0
        }
    }

    /**
     * DELETE - Deletar usuário por ID
     */
    fun deleteUserById(id: Int): Boolean {
        return transaction {
            val deleted = UsersTable.deleteWhere { (UsersTable.id eq id) }
            deleted > 0
        }
    }

    /**
     * Verificar se email já existe
     */
    fun emailExists(email: String): Boolean {
        return transaction {
            UsersTable.select { UsersTable.email eq email }.count() > 0
        }
    }

    /**
     * Contar total de usuários
     */
    fun countUsers(): Long {
        return transaction {
            UsersTable.selectAll().count()
        }
    }
}






