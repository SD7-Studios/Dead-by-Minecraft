package org.wordandahalf.spigot.deadbyminecraft

import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.SlimeWorld
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.plugin.java.JavaPlugin
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import java.lang.Exception
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

class DeadByMinecraftPlugin() : JavaPlugin()
{
    object Config
    {
        private const val MAX_PLAYERS_PATH = "max-players"

        private const val GAME_WORLD_NAME_PATH = "game.world-name"
        private const val LOBBY_WORLD_NAME_PATH = "lobby.world-name"

        private const val LOBBY_SPAWN_LOCATION_PATH = "lobby.spawn-location"
        private const val LOBBY_SPAWN_ROTATION_PATH = "lobby.spawn-rotation"

        private const val LOBBY_NPCS_ENABLED_PATH = "lobby.npcs.enabled"
        private const val LOBBY_NPCS_LOCATIONS_PATH = "lobby.npcs.locations"

        private const val DEFAULT_WORLD_NAME_PATH = "default-world.name"

        fun get(key: String) : Any?
        {
            return Instance.config.get(key)
        }

        fun maxPlayers() : Int
        {
            val maxPlayers : Any? = get(MAX_PLAYERS_PATH)

            if(maxPlayers !is Integer)
                throw InvalidConfigurationException("$MAX_PLAYERS_PATH must be an integer (number)! Found a(n) ${ get(MAX_PLAYERS_PATH)?.javaClass?.simpleName } instead.")

            return maxPlayers.toInt()
        }

        fun gameWorldName() : String
        {
            if(get(GAME_WORLD_NAME_PATH) !is String)
                throw InvalidConfigurationException("$GAME_WORLD_NAME_PATH must be a string (word)!")

            return get(GAME_WORLD_NAME_PATH) as String
        }

        fun lobbyWorldName() : String
        {
            if(get(LOBBY_WORLD_NAME_PATH) !is String)
                throw InvalidConfigurationException("$LOBBY_WORLD_NAME_PATH must be a string (word)!")

            return get(LOBBY_WORLD_NAME_PATH) as String
        }

        fun lobbySpawnLocation(): Array<Double>
        {
            val possibleSpawnLocation : Any? = get(LOBBY_SPAWN_LOCATION_PATH)
            val possibleSpawnRotation : Any? = get(LOBBY_SPAWN_ROTATION_PATH)

            if(possibleSpawnLocation !is List<*>)
                throw InvalidConfigurationException("$LOBBY_SPAWN_LOCATION_PATH should be an array of three floating-point (decimal) numbers! ")

            if(possibleSpawnRotation !is List<*>)
                throw InvalidConfigurationException("$LOBBY_SPAWN_ROTATION_PATH should be an array of two floating-point (decimal) numbers!")

            val spawnLocation : List<Double> = (get(LOBBY_SPAWN_LOCATION_PATH) as List<*>).filterIsInstance<Double>()
            val spawnRotation : List<Double>  = (get(LOBBY_SPAWN_ROTATION_PATH) as List<*>).filterIsInstance<Double>()

            if (spawnLocation.size != 3)
                throw InvalidConfigurationException("$LOBBY_SPAWN_LOCATION_PATH should be an array of three floating-point (decimal) numbers!")

            if (spawnRotation.size != 2)
                throw InvalidConfigurationException("$LOBBY_SPAWN_ROTATION_PATH should be an array of two floating-point (decimal) numbers!")

            return arrayOf(spawnLocation[0], spawnLocation[1], spawnLocation[2], spawnRotation[0], spawnRotation[1])
        }

        fun lobbyNPCSpawnLocations() : List<ArrayList<Double>>
        {
            val npcSpawnCoordList : List<ArrayList<Double>>? = get(LOBBY_NPCS_LOCATIONS_PATH) as? List<ArrayList<Double>>

            if(npcSpawnCoordList !is List<ArrayList<Double>> || npcSpawnCoordList.isEmpty() || npcSpawnCoordList[0].size != 3)
                throw InvalidConfigurationException("$LOBBY_NPCS_LOCATIONS_PATH should be a list of arrays of three floating-point (decimal) numbers!")

            if(npcSpawnCoordList.isEmpty())
                return listOf()

            return npcSpawnCoordList
        }

        fun areLobbyNPCsEnabled() : Boolean
        {
            val enabled : Any? = get(LOBBY_NPCS_ENABLED_PATH)

            if(enabled !is Boolean)
                throw InvalidConfigurationException("$LOBBY_NPCS_ENABLED_PATH should be a boolean (true/false)")

            return enabled
        }

        fun defaultWorldName() : String
        {
            val defaultWorldName : Any? = get(DEFAULT_WORLD_NAME_PATH)

            if(defaultWorldName !is String)
                throw InvalidConfigurationException("$DEFAULT_WORLD_NAME_PATH needs to be a string (word)!")

            return defaultWorldName
        }
    }

    object Worlds
    {
        private const val LOBBY_WORLD_PREFIX = "dbm-lobby-world-"
        private const val GAME_WORLD_PREFIX = "dbm-game-world-"

        private lateinit var lobbyWorld : SlimeWorld
        private lateinit var gameWorld : SlimeWorld

        /**
         * Loads the world templates if they have not already been loaded
         * @return true if already loaded or loaded successfully, false otherwise
         */
        fun loadTemplateWorlds() : Boolean
        {
            try {
                val slimePlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin
                val loader = slimePlugin.getLoader("file")

                val properties = SlimePropertyMap()
                properties.setString(SlimeProperties.DIFFICULTY, "peaceful")
                properties.setBoolean(SlimeProperties.ALLOW_ANIMALS, false)
                properties.setBoolean(SlimeProperties.ALLOW_MONSTERS, false)
                properties.setBoolean(SlimeProperties.PVP, false)
                properties.setString(SlimeProperties.WORLD_TYPE, "customized")

                lobbyWorld = slimePlugin.loadWorld(loader, Config.lobbyWorldName(), !DEBUG, properties)
                slimePlugin.generateWorld(lobbyWorld)

                properties.setString(SlimeProperties.DIFFICULTY, "hard")
                properties.setBoolean(SlimeProperties.PVP, false)

                gameWorld = slimePlugin.loadWorld(loader, Config.gameWorldName(), !DEBUG, properties)
                slimePlugin.generateWorld(gameWorld)
            }
            catch (e: Exception)
            {
                Logger.warning(e.stackTraceToString())
                return false
            }

            return true
        }

        fun cloneLobbyWorld() : SlimeWorld
        {
            val world = lobbyWorld.clone(LOBBY_WORLD_PREFIX + UUID.randomUUID().toString())
            (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(world)

            Logger.info("Cloning lobby world with name '${world.name}'")
            Logger.info("Loaded worlds: " + Bukkit.getWorlds().joinToString { it.name })

            return world
        }

        fun cloneGameWorld() : SlimeWorld
        {
            val world = gameWorld.clone(GAME_WORLD_PREFIX + UUID.randomUUID().toString())
            (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(world)

            Logger.info("Cloning game world with name '${world.name}'")
            Logger.info("Loaded worlds: " + Bukkit.getWorlds().joinToString { it.name })

            return world
        }
    }

    companion object
    {
        lateinit var Instance : DeadByMinecraftPlugin
        lateinit var Logger : Logger

        const val DEBUG : Boolean = true
    }

    override fun onEnable()
    {
        Instance = this
        Logger = this.logger

        Logger.info("Dead by Minecraft has loaded!")

        saveDefaultConfig()

        server.pluginManager.registerEvents(DeadByMinecraftEventListener(), this)
        getCommand("dbm")!!.setExecutor(DeadByMinecraftCommandListener());

        Worlds.loadTemplateWorlds()

        if(DEBUG)
            DeadByMinecraftGameManager.createGame();
    }

    override fun onDisable()
    {
        DeadByMinecraftGameManager.getGames().forEach { it.stop() }
    }
}