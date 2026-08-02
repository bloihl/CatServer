package catserver

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.routing

class App {
    val greeting: String
        get() = "Hello World!"
}

fun Application.module() {
    install(GraphQL) {
        schema {
            packages = listOf("catserver")
            queries = listOf(CatQuery())
            mutations = listOf(CatMutation())
        }
    }

    install(StatusPages) {
        defaultGraphQLStatusPages()
    }

    routing {
        graphQLPostRoute()
        graphQLGetRoute()
        graphiQLRoute()
    }
}

fun main() {
    // 1. Initialize Database connection
    DatabaseFactory.init()

    // 2. Start Ktor Web Server
    val port = System.getenv("PORT")?.toInt() ?: 8080
    println("Starting Ktor server on port $port...")

    val server = embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }

    // Add shutdown hook to close the connection pool gracefully
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Stopping server...")
        server.stop(1000, 2000)
        DatabaseFactory.close()
        println("Server stopped gracefully.")
    })

    server.start(wait = true)
}
