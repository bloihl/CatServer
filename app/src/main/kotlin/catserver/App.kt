package catserver

import catserver.schema.RouteQuery
import catserver.schema.RoutesQuery
import catserver.schema.StopQuery
import catserver.schema.StopsQuery
import catserver.schema.FeedMetaQuery
import catserver.schema.dataloaders.StopRouteDataLoader
import catserver.schema.dataloaders.RouteTripDataLoader
import catserver.schema.dataloaders.RouteTripStopDataLoader
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.server.ktor.*
import com.expediagroup.graphql.server.operations.Query
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

class HelloQuery : Query {
    fun hello(): String = "Hello World!"
}

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

        engine {
            dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory(
                listOf(RouteTripDataLoader, RouteTripStopDataLoader, StopRouteDataLoader)
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
