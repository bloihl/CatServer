package catserver.schema.models

import catserver.db.tables.RoutesTable
import catserver.schema.dataloaders.TripDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

class Route(
    val routeId: String,
    val routeShortName: String?,
    val routeLongName: String,
    val routeDesc: String?
) {
    fun trips(dfe: DataFetchingEnvironment): CompletableFuture<List<Trip>> {
        return dfe.getValueFromDataLoader(TripDataLoader.dataLoaderName, routeId)
//        CompletableFuture.completedFuture(emptyList<Trip>())
    }

    companion object {
        fun allRoutes(): List<Route>
        {
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

        fun getRoute(routeId: String): Route? {
            return transaction {
                RoutesTable.selectAll()
                    .where { RoutesTable.routeId eq routeId }
                    .singleOrNull()
                    ?.let { row ->
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
}