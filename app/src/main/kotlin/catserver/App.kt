package catserver

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import com.expediagroup.graphql.server.operations.Query
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date

import catserver.RoutesQuery

class HelloQuery : Query {
    fun hello(): String = "Hello World!"
}

class FeedMetaQuery : Query {
    fun feedMeta(): List<FeedMeta> {
        return transaction {
            FeedMetaTable.selectAll().map { row ->
                FeedMeta(
                    feed_key = row[FeedMetaTable.feedKey],
                    feed_url = row[FeedMetaTable.feedUrl],
                    last_updated = Date(row[FeedMetaTable.lastUpdated]).toString(),
                    last_successful_refresh = Date(row[FeedMetaTable.lastSuccessfulRefresh]).toString()
                )
            }
        }
    }
}

object FeedMetaTable : Table("feed_meta") {
    val feedKey = varchar("feed_key", 255)
    val feedUrl = varchar("feed_url", 1024)
    val lastUpdated = long("last_updated")
    val lastSuccessfulRefresh = long("last_successful_refresh")

    override val primaryKey = PrimaryKey(feedKey)
}

data class FeedMeta(
    val feed_key: String,
    val feed_url: String,
    val last_updated: String?,
    val last_successful_refresh: String?
)

fun initDatabase() {
    try {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/catracker"
            driverClassName = "org.postgresql.Driver"
            username = System.getenv("DB_USER") ?: "bobl"
            password = System.getenv("DB_PASSWORD") ?: ""
            maximumPoolSize = 10
            initializationFailTimeout = 1000 // prevent hanging if PostgreSQL is unavailable
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        println("Connected to database successfully.")
    } catch (e: Exception) {
        println("Warning: Failed to connect to local database 'catracker': ${e.message}")
    }
}

fun main() {
    initDatabase()
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(GraphQL) {
        schema {
            packages = listOf("catserver")
            queries = listOf(
                HelloQuery(),
                FeedMetaQuery(),
                RoutesQuery(),
                RouteQuery(),
                StopsQuery(),
                StopQuery(),
            )
        }
    }

    routing {
        graphQLPostRoute()
        graphQLGetRoute()
        graphQLSDLRoute()
        graphiQLRoute()
    }

    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}
