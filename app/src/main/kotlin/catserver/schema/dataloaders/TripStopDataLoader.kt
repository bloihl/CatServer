package catserver.schema.dataloaders

import catserver.schema.models.TripStop
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import graphql.GraphQLContext
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import java.util.concurrent.CompletableFuture


val TripStopDataLoader = object : KotlinDataLoader<String, List<TripStop>> {
    override val dataLoaderName = "TRIPSTOP_LOADER"
    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String,  List<TripStop>> {
        return DataLoaderFactory.newDataLoader { tripIds ->
            CompletableFuture.supplyAsync {
                runBlocking { TripStop.stopsFor(tripIds) }
            }
        }
    }
}
