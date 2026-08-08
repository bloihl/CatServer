package catserver.schema.dataloaders

import catserver.schema.models.RouteTrip
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import graphql.GraphQLContext
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import java.util.concurrent.CompletableFuture

val RouteTripDataLoader = object : KotlinDataLoader<String, List<RouteTrip>> {
    override val dataLoaderName = "ROUTE_TRIP_LOADER"
    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, List<RouteTrip>> {
        return DataLoaderFactory.newDataLoader { routeIds ->
            CompletableFuture.supplyAsync {
                runBlocking { RouteTrip.Companion.allTrips(routeIds) }
            }
        }
    }
}