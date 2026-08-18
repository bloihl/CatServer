package catserver.db.tables

import org.jetbrains.exposed.sql.Table

object StopsTable : Table("stops") {
    val stopId = varchar("stop_id", 255)
    val stopName = varchar("stop_name", 1024).nullable()
    val stopDesc = varchar("stop_desc", 1024).nullable()

    override val primaryKey = PrimaryKey(stopId)
}
