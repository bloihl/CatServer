package catserver

import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CatQuery : Query {
    fun hello(): String = "Hello from CatServer!"

    fun cats(): List<Cat> {
        return try {
            transaction {
                Cats.selectAll().map {
                    Cat(
                        id = it[Cats.id],
                        name = it[Cats.name],
                        breed = it[Cats.breed],
                        age = it[Cats.age]
                    )
                }
            }
        } catch (e: Exception) {
            System.err.println("Failed to query cats from DB: ${e.message}. Returning default static cats.")
            listOf(
                Cat(1, "Milo", "Tabby", 3),
                Cat(2, "Luna", "Siamese", 2)
            )
        }
    }
}

class CatMutation : Mutation {
    fun createCat(name: String, breed: String, age: Int): Cat {
        return try {
            transaction {
                val insertedId = Cats.insert {
                    it[Cats.name] = name
                    it[Cats.breed] = breed
                    it[Cats.age] = age
                } get Cats.id

                Cat(id = insertedId, name = name, breed = breed, age = age)
            }
        } catch (e: Exception) {
            System.err.println("Failed to insert cat into DB: ${e.message}. Returning fallback Cat object.")
            Cat(999, name, breed, age)
        }
    }
}
