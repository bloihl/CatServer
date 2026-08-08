package catserver.schema.models

import catserver.db.tables.StopsTable
import catserver.schema.dataloaders.StopRouteDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

data class Stop(
    val stopId: String,
    val stopName: String?,
    val stopDesc: String?
){

    fun trips(dfe: DataFetchingEnvironment): CompletableFuture<List<StopRoute>>  =
        dfe.getValueFromDataLoader(StopRouteDataLoader.dataLoaderName, stopId)

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

        fun getStops(stopIds: List<String>): List<List<Stop>> {
            val stops = transaction {
                StopsTable.selectAll()
                    .where { StopsTable.stopId inList stopIds }
                    .map { row ->
                        Stop(
                            stopId = row[StopsTable.stopId],
                            stopName = row[StopsTable.stopName],
                            stopDesc = row[StopsTable.stopDesc]
                        )
                    }
                    .groupBy { it.stopId }
            }
            return stopIds.map { stopId ->
                stops[stopId] ?: emptyList()
            }
        }
    }
}