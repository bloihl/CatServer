package catserver

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

// Definition of the Cats table
object Cats : Table("cats") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val breed = varchar("breed", 50)
    val age = integer("age")

    override val primaryKey = PrimaryKey(id)
}

// Data class representation
data class Cat(val id: Int, val name: String, val breed: String, val age: Int)

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null

    fun init() {
        val dbUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/catracker"
        val dbUser = System.getenv("JDBC_DATABASE_USERNAME") ?: "postgres"
        val dbPassword = System.getenv("JDBC_DATABASE_PASSWORD") ?: "postgres"

        println("Connecting to database: $dbUrl as $dbUser...")

        val config = HikariConfig().apply {
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            // Set short timeouts so it doesn't hang indefinitely if localhost Postgres is down
            connectionTimeout = 3000
            validationTimeout = 1000
            initializationFailTimeout = 0 // Do not fail initialization if DB is down at startup
        }

        try {
            val ds = HikariDataSource(config)
            dataSource = ds
            Database.connect(ds)

            // Try to create the table if database is accessible
            transaction {
                SchemaUtils.create(Cats)
            }
            println("Database initialization completed successfully.")
        } catch (e: Exception) {
            System.err.println("Warning: Could not connect or initialize database. Continuing anyway. Error: ${e.message}")
        }
    }

    fun close() {
        dataSource?.close()
    }
}
