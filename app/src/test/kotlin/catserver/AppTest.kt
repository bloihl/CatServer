package catserver

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppTest {
    @Test
    fun testAppGreeting() {
        val app = App()
        assertEquals("Hello World!", app.greeting)
    }

    @Test
    fun testGraphQLPlayground() = testApplication {
        application {
            module()
        }
        // Expedia's graphiQLRoute exposes the GraphiQL playground on /graphiql
        val response = client.get("/graphiql")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("GraphiQL"))
    }

    @Test
    fun testGraphQLHelloQuery() = testApplication {
        application {
            module()
        }
        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"query": "{ hello }"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("Hello from CatServer!"))
    }

    @Test
    fun testGraphQLCatsQueryWithFallback() = testApplication {
        application {
            module()
        }
        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"query": "{ cats { id name breed age } }"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = response.bodyAsText()
        // Should contain the default static cats since DB connection in test fails or falls back gracefully
        assertTrue(responseBody.contains("Milo"))
        assertTrue(responseBody.contains("Luna"))
    }
}
