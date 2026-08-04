package catserver

import com.expediagroup.graphql.server.operations.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class StopsQuery : Query {
    fun stops(): List<Stop> {
        return transaction {
            StopsTable.selectAll().map { row ->
                Stop(
                    stopId = row[StopsTable.stopId],
                    stopName = row[StopsTable.stopName],
                    stopDesc = row[StopsTable.stopDesc]
                )
            }
        }
    }
}

class StopQuery : Query {
    fun stop(stopId: String): Stop? {
        return transaction {
            StopsTable.selectAll()
                .where {StopsTable.stopId eq stopId}
                .singleOrNull()
                ?.let{ row ->
                    Stop(
                        stopId = row[StopsTable.stopId],
                        stopName = row[StopsTable.stopName],
                        stopDesc = row[StopsTable.stopDesc]
                    )
                }
            }
        }
    }

object StopsTable : Table("stops") {
    val stopId = varchar("stop_id", 255)
    val stopName = varchar("stop_name", 1024).nullable()
    val stopDesc = varchar("stop_desc", 1024).nullable()

    override val primaryKey = PrimaryKey(stopId)
}

data class Stop(
    val stopId: String,
    val stopName: String?,
    val stopDesc: String?
)

