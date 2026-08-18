package catserver.schema.models

import catserver.db.tables.RoutesTable
import catserver.db.tables.StopTimesTable
import catserver.db.tables.TripsTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class StopRoute(
    val tripId: String,
    val arrivalTime: String,
    val departureTime: String,
    val stopId: String,
    val stopSequence: Int,
    val stopHeadsign: String?,
    val routeId: String,
    val routeShortName: String?,
    val routeLongName: String,
    val routeDesc: String?
){
    companion object {
        fun routesFor(stopIds: List<String>): List<List<StopRoute>>{
            val stopTimesByTrip = transaction {
                StopTimesTable.join(TripsTable, JoinType.INNER, additionalConstraint = { StopTimesTable.tripId eq TripsTable.tripId })
                    .join(RoutesTable, JoinType.INNER, additionalConstraint = { TripsTable.routeId eq RoutesTable.routeId })
                    .selectAll()
                    .where{ StopTimesTable.stopId inList stopIds }
                    .orderBy(StopTimesTable.arrivalTime)
                    .map { row ->
                        StopRoute(
                            tripId = row[StopTimesTable.tripId],
                            arrivalTime = row[StopTimesTable.arrivalTime],
                            departureTime = row[StopTimesTable.departureTime],
                            stopId = row[StopTimesTable.stopId],
                            stopSequence = row[StopTimesTable.stopSequence],
                            stopHeadsign = row[StopTimesTable.stopHeadsign],
                            routeId = row[TripsTable.routeId],
                            routeShortName = row[RoutesTable.routeShortName],
                            routeLongName = row[RoutesTable.routeLongName],
                            routeDesc = row[RoutesTable.routeDesc],
                        )
                    }
                    .groupBy{it.stopId}
            }
            return stopIds.map { stopId ->
                stopTimesByTrip[stopId] ?: emptyList()
            }
        }
    }
}