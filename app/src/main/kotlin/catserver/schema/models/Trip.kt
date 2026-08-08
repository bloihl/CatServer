package catserver.schema.models

import catserver.db.tables.TripsTable
import catserver.schema.dataloaders.TripStopDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

data class Trip(
    val routeId: String,
    val tripId: String,
    val tripName: String?,
    val tripDir: String?,
    val tripServiceId: String?,
    val tripHeadSign: String?
){
    fun tripStops(dfe: DataFetchingEnvironment): CompletableFuture<List<TripStop>>  =
        dfe.getValueFromDataLoader(TripStopDataLoader.dataLoaderName, tripId)

    companion object {
        fun allTrips(routeIds: List<String>): List<List<Trip>> {
            val tripsByRouteId = transaction {
                TripsTable.selectAll()
                    .where { TripsTable.routeId inList routeIds }
                    .map { row ->
                        Trip(
                            row[TripsTable.routeId],
                            row[TripsTable.tripId],
                            row[TripsTable.tripName],
                            row[TripsTable.tripDir],
                            row[TripsTable.tripServiceId],
                            row[TripsTable.tripHeadSign]
                        )
                    }
                    .groupBy { it.routeId }
            }
            return routeIds.map { routeId ->
                tripsByRouteId[routeId] ?: emptyList()
            }
        }
    }
}