package catserver.schema

import catserver.db.tables.FeedMetaTable
import catserver.schema.models.FeedMeta
import com.expediagroup.graphql.server.operations.Query
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date

class FeedMetaQuery: Query {
        fun feedMeta(): List<FeedMeta> {
            return FeedMeta.allFeedMeta()
        }
    }