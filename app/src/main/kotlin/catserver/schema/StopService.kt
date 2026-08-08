package catserver.schema

import catserver.db.tables.StopsTable
import catserver.schema.models.Stop
import com.expediagroup.graphql.server.operations.Query
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class StopsQuery : Query {
    fun stops(): List<Stop> {
        return Stop.allStops()
    }
}

class StopQuery : Query {
    fun stop(stopId: String): Stop? {
        return Stop.getStop(stopId)
    }
}