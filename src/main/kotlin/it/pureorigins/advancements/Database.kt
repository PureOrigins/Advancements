package it.pureorigins.advancements

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll


object AdvancementTable : Table("advancements") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 100)
    val json = text("json")
    
    override val primaryKey = PrimaryKey(id)
    
    fun getAll(): Map<Int, Advancement> = selectAll().associate { it.toAdvancement() }
    fun getByName(name: String): Map<Int, Advancement> = select { AdvancementTable.name eq name }.associate { it.toAdvancement() }
}

object ServerTable : Table("servers") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 50).uniqueIndex()
    val title = varchar("title", length = 50)
    
    override val primaryKey = PrimaryKey(id)
    
    fun getTitleByName(name: String): String? = select { ServerTable.name eq name }.singleOrNull()?.get(title)
}

object AdvancementCompatibilityTable : Table("advancements_compatibility") {
    val advancementId = integer("advancement_id") references AdvancementTable.id
    val serverId = integer("server_id") references ServerTable.id
    
    fun getCompatibleAdvancements(serverName: String): Map<Int, Advancement> = innerJoin(ServerTable).innerJoin(AdvancementTable).select { ServerTable.name eq serverName }.associate { it.toAdvancement() }
}

fun ResultRow.toAdvancement(): Pair<Int, Advancement> = this[AdvancementTable.id] to Advancement(
    this[AdvancementTable.name],
    JsonParser().parse(this[AdvancementTable.json]).asJsonObject
)
