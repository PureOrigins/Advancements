package it.pureorigins.advancements

import com.google.gson.JsonElement
import it.pureorigins.framework.configuration.configFile
import it.pureorigins.framework.configuration.json
import it.pureorigins.framework.configuration.readFileAs
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction

object Advancements : ModInitializer {
    private lateinit var database: Database
    private var serverName: String? = null
    
    @JvmName("getCompatibleAdvancements")
    internal fun getCompatibleAdvancements(): Map<Identifier, JsonElement> {
        val serverName = serverName
        return transaction(database) {
            if (serverName == null) {
                AdvancementTable.getAll()
            } else {
                AdvancementCompatibilityTable.getCompatibleAdvancements(serverName)
            }.map { (_, advancement) -> Identifier(advancement.name) to advancement.json }.toMap()
        }
    }
    
    override fun onInitialize() {
        val (serverName, db) = json.readFileAs(configFile("advancements.json"), Config())
        require(db.url.isNotEmpty()) { "database url should not be empty" }
        database = Database.connect(db.url, user = db.username, password = db.password)
        transaction(database) {
            createMissingTablesAndColumns(AdvancementTable, ServerTable, AdvancementCompatibilityTable)
        }
        this.serverName = serverName
    }
    
    @Serializable
    data class Config(
        val serverName: String? = null,
        val database: Database = Database()
    ) {
        @Serializable
        data class Database(
            val url: String = "",
            val username: String = "",
            val password: String = ""
        )
    }
}
