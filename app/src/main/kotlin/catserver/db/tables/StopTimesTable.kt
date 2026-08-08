package catserver.db.tables

import org.jetbrains.exposed.sql.Table

object StopTimesTable : Table("stop_times") {
    val tripId = varchar("trip_id", 255)
    val arrivalTime = long("arrival_time")
    val departureTime = long("departure_time")
    val stopId = varchar("stop_id", 255)
    val stopSequence = integer("stop_sequence")
    val stopHeadsign = varchar("stop_headsign", 1024).nullable()

    override val primaryKey = PrimaryKey(tripId)
}