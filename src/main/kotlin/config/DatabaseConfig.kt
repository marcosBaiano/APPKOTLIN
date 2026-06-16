package org.example.config

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.example.tables.*

/**
 * Configuração centralizada do banco de dados MySQL
 */
object DatabaseConfig {

    // Configurações do banco de dados
    private const val DB_URL = "jdbc:mysql://localhost:3306/appkotlin"
    private const val DB_USER = "root"
    private const val DB_PASSWORD = "edneiaSQL"  // ⚠️ MUDAR EM PRODUÇÃO!
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
                SchemaUtils.create(
                    UsersTable,
                    PostsTable,
                    CommentsTable,
                    NotificationsTable,
                    ColletasTable,
                    DepoimentosTable
                )
                println("✅ Tabelas criadas/verificadas com sucesso!")
            }
        } catch (e: Exception) {
            println("⚠️ Erro ao criar tabelas: ${e.message}")
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

