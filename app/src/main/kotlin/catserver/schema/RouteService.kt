package catserver.schema

import catserver.schema.models.Route
import com.expediagroup.graphql.server.operations.Query

class RoutesQuery : Query {
    fun routes(): List<Route> {
        return Route.allRoutes()
    }
}

class RouteQuery : Query {
    fun route(routeId: String): Route? {
        return Route.getRoute(routeId)
    }
}