package catserver.schema.models

import catserver.db.tables.StopTimesTable
import catserver.schema.dataloaders.StopDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

data class TripStop(
    val tripId: String,
    val arrivalTime: String,
    val departureTime: String,
    val stopId: String,
    val stopSequence: Int,
    val stopHeadsign: String?
){
    fun stop(dfe: DataFetchingEnvironment): CompletableFuture<List<Stop>>  =
        dfe.getValueFromDataLoader(StopDataLoader.dataLoaderName, stopId)


    companion object {
        fun stopsFor(tripIds: List<String>): List<List<TripStop>>{
            val stopTimesByTrip = transaction {
                StopTimesTable.selectAll()
                    .where{ StopTimesTable.tripId inList tripIds }
                    .orderBy(StopTimesTable.stopSequence)
                    .map { row ->
                        TripStop(
                            tripId = row[StopTimesTable.tripId],
                            arrivalTime = row[StopTimesTable.arrivalTime],
                            departureTime = row[StopTimesTable.departureTime],
                            stopId = row[StopTimesTable.stopId],
                            stopSequence = row[StopTimesTable.stopSequence],
                            stopHeadsign = row[StopTimesTable.stopHeadsign]
                        )
                    }
                    .groupBy{it.tripId}
            }
            return tripIds.map { tripId ->
                stopTimesByTrip[tripId] ?: emptyList()
            }
        }
    }
}