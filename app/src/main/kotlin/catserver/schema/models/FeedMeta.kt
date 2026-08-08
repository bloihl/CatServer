package catserver.schema.models

import catserver.db.tables.FeedMetaTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date


data class FeedMeta(
    val feed_key: String,
    val feed_url: String,
    val last_updated: String?,
    val last_successful_refresh: String?
){
    companion object {
        fun allFeedMeta(): List<FeedMeta> {
            return transaction {
                FeedMetaTable.selectAll().map { row ->
                    FeedMeta(
                        feed_key = row[FeedMetaTable.feedKey],
                        feed_url = row[FeedMetaTable.feedUrl],
                        last_updated = Date(row[FeedMetaTable.lastUpdated]).toString(),
                        last_successful_refresh = Date(row[FeedMetaTable.lastSuccessfulRefresh]).toString()
                    )
                }
            }
        }
    }
}