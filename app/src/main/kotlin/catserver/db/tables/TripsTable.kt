package catserver.db.tables

import org.jetbrains.exposed.sql.Table

object TripsTable : Table("trips") {
    val routeId = varchar("route_id", 255)
    val tripId = varchar("trip_id", 255)
    val tripServiceId = varchar("service_id", 255)
    val tripName = varchar("trip_short_name", 1024).nullable()
    val tripDir = varchar("direction_id", 1024).nullable()
    val tripHeadSign = varchar("trip_headsign", 1024).nullable()

    override val primaryKey = PrimaryKey(tripId)
}