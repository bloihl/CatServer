package catserver.schema.models

import catserver.db.tables.StopsTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class Stop(
    val stopId: String,
    val stopName: String?,
    val stopDesc: String?
){
    companion object{
        fun allStops(): List<Stop> {
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

        fun getStop(stopId: String): Stop? {
            return transaction {
                StopsTable.selectAll()
                .where { StopsTable.stopId eq stopId }
                .singleOrNull()
                ?.let { row ->
                    Stop(
                        stopId = row[StopsTable.stopId],
                        stopName = row[StopsTable.stopName],
                        stopDesc = row[StopsTable.stopDesc]
                    )
                }
            }
        }
    }
}