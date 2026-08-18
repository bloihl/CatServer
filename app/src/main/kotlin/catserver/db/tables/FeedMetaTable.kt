package catserver.db.tables

import org.jetbrains.exposed.sql.Table

object FeedMetaTable : Table("feed_meta") {
    val feedKey = varchar("feed_key", 255)
    val feedUrl = varchar("feed_url", 1024)
    val lastUpdated = long("last_updated")
    val lastSuccessfulRefresh = long("last_successful_refresh")

    override val primaryKey = PrimaryKey(feedKey)
}