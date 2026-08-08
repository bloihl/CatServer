package catserver.schema.dataloaders

import catserver.schema.models.Stop
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import graphql.GraphQLContext
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import java.util.concurrent.CompletableFuture

val StopDataLoader = object : KotlinDataLoader<String, List<Stop>> {
    override val dataLoaderName = "STOP_LOADER"
    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String,  List<Stop>> {
        return DataLoaderFactory.newDataLoader { stopIds ->
            CompletableFuture.supplyAsync {
                runBlocking { Stop.getStops(stopIds) }
            }
        }
    }
}