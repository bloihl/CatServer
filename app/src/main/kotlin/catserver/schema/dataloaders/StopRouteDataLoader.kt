package catserver.schema.dataloaders

import catserver.schema.models.StopRoute
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import graphql.GraphQLContext
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import java.util.concurrent.CompletableFuture

val StopRouteDataLoader = object : KotlinDataLoader<String, List<StopRoute>> {
    override val dataLoaderName = "STOP_ROUTE_LOADER"
    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String,  List<StopRoute>> {
        return DataLoaderFactory.newDataLoader { stopIds ->
            CompletableFuture.supplyAsync {
                runBlocking { StopRoute.routesFor(stopIds) }
            }
        }
    }
}