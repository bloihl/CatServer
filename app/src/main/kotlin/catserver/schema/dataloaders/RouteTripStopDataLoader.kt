package catserver.schema.dataloaders

import catserver.schema.models.RouteTripStop
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import graphql.GraphQLContext
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import java.util.concurrent.CompletableFuture


val RouteTripStopDataLoader = object : KotlinDataLoader<String, List<RouteTripStop>> {
    override val dataLoaderName = "ROUTE_TRIP_STOP_LOADER"
    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String,  List<RouteTripStop>> {
        return DataLoaderFactory.newDataLoader { tripIds ->
            CompletableFuture.supplyAsync {
                runBlocking { RouteTripStop.stopsFor(tripIds) }
            }
        }
    }
}
