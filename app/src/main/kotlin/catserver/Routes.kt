package catserver

import com.expediagroup.graphql.server.operations.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RoutesQuery : Query {
    fun routes(): List<Route> {
        return transaction {
            RoutesTable.selectAll().map { row ->
                Route(
                    routeId = row[RoutesTable.routeId],
                    routeShortName = row[RoutesTable.routeShortName],
                    routeLongName = row[RoutesTable.routeLongName],
                    routeDesc = row[RoutesTable.routeDesc]
                )
            }
        }
    }
}

class RouteQuery : Query {
    fun route(routeId: String): Route? {
        return transaction {
            RoutesTable.selectAll()
                .where {RoutesTable.routeId eq routeId}
                .singleOrNull()
                ?.let{ row ->
                    Route(
                        routeId = row[RoutesTable.routeId],
                        routeShortName = row[RoutesTable.routeShortName],
                        routeLongName = row[RoutesTable.routeLongName],
                        routeDesc = row[RoutesTable.routeDesc]
                    )
                }
            }
        }
    }

object RoutesTable : Table("routes") {
    val routeId = varchar("route_id", 255)
    val routeShortName = varchar("route_short_name", 1024).nullable()
    val routeLongName = varchar("route_long_name", 1024)
    val routeDesc = varchar("route_desc", 1024).nullable()

    override val primaryKey = PrimaryKey(routeId)
}

data class Route(
    val routeId: String,
    val routeShortName: String?,
    val routeLongName: String,
    val routeDesc: String?
)

