package catserver

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import catserver.initDatabase
import catserver.module

class AppTest {

    @Test
    fun testDatabaseInitialization() {
        // Verify initDatabase can be executed without throwing exceptions
        assertDoesNotThrow {
            initDatabase()
        }
    }

    @Test
    fun testGraphQLPostRoute() = testApplication {
        application {
            module()
        }
        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, "application/json")
            setBody("""{"query":"query { hello }"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Hello World!"), "Response should contain Hello World!")
    }

    @Test
    fun testGraphQLSDLRoute() = testApplication {
        application {
            module()
        }
        val response = client.get("/sdl")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("type Query"), "SDL should define type Query")
        assertTrue(body.contains("hello: String!"), "SDL should contain hello function")
    }

    @Test
    fun testGraphiQLRoute() = testApplication {
        application {
            module()
        }
        val response = client.get("/graphiql")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("GraphiQL"), "Should render GraphiQL HTML")
    }
}
