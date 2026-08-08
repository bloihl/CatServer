package catserver.schema.models

import catserver.db.tables.StopTimesTable
import catserver.db.tables.StopsTable
import catserver.db.tables.TripsTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class RouteTrip(
    val routeId: String,
    val tripId: String,
    val tripName: String?,
    val tripDir: String?,
    val tripServiceId: String?,
    val tripHeadSign: String?,
    val arrivalTime: String,
    val departureTime: String,
    val stopId: String,
    val stopSequence: Int,
    val stopHeadsign: String?,
    val stopName: String?,
    val stopDesc: String?
){
    companion object {
        fun allTrips(routeIds: List<String>): List<List<RouteTrip>> {
            val tripsByRouteId = transaction {
                TripsTable
                    .join(StopTimesTable, JoinType.INNER, additionalConstraint = { TripsTable.tripId eq StopTimesTable.tripId })
                    .join(StopsTable, JoinType.INNER, additionalConstraint = { StopTimesTable.stopId eq StopsTable.stopId })
                    .selectAll()
                    .where { TripsTable.routeId inList routeIds }
                    .orderBy(StopTimesTable.arrivalTime)
                    .map { row ->
                        RouteTrip(
                            row[TripsTable.routeId],
                            row[TripsTable.tripId],
                            row[TripsTable.tripName],
                            row[TripsTable.tripDir],
                            row[TripsTable.tripServiceId],
                            row[TripsTable.tripHeadSign],
                            row[StopTimesTable.arrivalTime],
                            row[StopTimesTable.departureTime],
                            row[StopTimesTable.stopId],
                            row[StopTimesTable.stopSequence],
                            row[StopTimesTable.stopHeadsign],
                            row[StopsTable.stopName],
                            row[StopsTable.stopDesc],
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