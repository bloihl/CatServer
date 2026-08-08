package catserver.db.tables

import org.jetbrains.exposed.sql.Table

object RoutesTable : Table("routes") {
    val routeId = varchar("route_id", 255)
    val routeShortName = varchar("route_short_name", 1024).nullable()
    val routeLongName = varchar("route_long_name", 1024)
    val routeDesc = varchar("route_desc", 1024).nullable()

    override val primaryKey = PrimaryKey(routeId)
}