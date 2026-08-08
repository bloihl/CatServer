package catserver.schema.models

import catserver.db.tables.StopTimesTable
import catserver.db.tables.StopsTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class RouteTripStop(
    val tripId: String,
    val arrivalTime: String,
    val departureTime: String,
    val stopId: String,
    val stopSequence: Int,
    val stopHeadsign: String?,
    val stopName: String?,
    val stopDesc: String?
){
    companion object {
        fun stopsFor(tripIds: List<String>): List<List<RouteTripStop>>{
            val stopTimesByTrip = transaction {
                StopTimesTable.join(StopsTable, JoinType.INNER, additionalConstraint = { StopTimesTable.stopId eq StopsTable.stopId })
                    .selectAll()
                    .where{ StopTimesTable.tripId inList tripIds }
                    .orderBy(StopTimesTable.stopSequence)
                    .map { row ->
                        RouteTripStop(
                            tripId = row[StopTimesTable.tripId],
                            arrivalTime = row[StopTimesTable.arrivalTime],
                            departureTime = row[StopTimesTable.departureTime],
                            stopId = row[StopTimesTable.stopId],
                            stopSequence = row[StopTimesTable.stopSequence],
                            stopHeadsign = row[StopTimesTable.stopHeadsign],
                            stopName = row[StopsTable.stopName],
                            stopDesc = row[StopsTable.stopDesc]
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