package catserver.schema.dataloaders

import catserver.schema.models.Trip
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import graphql.GraphQLContext
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import java.util.concurrent.CompletableFuture

val TripDataLoader = object : KotlinDataLoader<String, List<Trip>> {
    override val dataLoaderName = "TRIP_LOADER"
    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String,  List<Trip>> {
        return DataLoaderFactory.newDataLoader { routeIds ->
            CompletableFuture.supplyAsync {
                runBlocking { Trip.allTrips(routeIds) }
            }
        }
    }
}
