package org.wordandahalf.spigot.deadbyminecraft

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.plugin.java.JavaPlugin
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import java.util.logging.Logger

class DeadByMinecraftPlugin() : JavaPlugin()
{
    object Config
    {
        private const val MAX_PLAYERS_PATH = "max-players"

        private const val GAME_WORLD_NAME_PATH = "game-world.name"

        private const val GAME_WORLD_LOBBY_SPAWN_LOCATION_PATH = "game-world.lobby.spawn-location"
        private const val GAME_WORLD_LOBBY_SPAWN_ROTATION_PATH = "game-world.lobby.spawn-rotation"

        private const val GAME_WORLD_LOBBY_NPCS_ENABLED_PATH = "game-world.lobby.npcs.enabled"
        private const val GAME_WORLD_LOBBY_NPCS_LOCATIONS_PATH = "game-world.lobby.npcs.locations"

        private const val DEFAULT_WORLD_NAME_PATH = "default-world.name"

        fun get(key: String) : Any?
        {
            return Instance.config.get(key)
        }

        fun getMaxPlayers() : Int
        {
            val maxPlayers : Any? = get(MAX_PLAYERS_PATH)

            if(maxPlayers !is Integer)
                throw InvalidConfigurationException("$MAX_PLAYERS_PATH must be an integer (number)! Found a(n) ${ get(MAX_PLAYERS_PATH)?.javaClass?.simpleName } instead.")

            return maxPlayers.toInt()
        }

        fun getGameWorldName() : String
        {
            if(get(GAME_WORLD_NAME_PATH) !is String)
                throw InvalidConfigurationException("$GAME_WORLD_NAME_PATH must be a string (word)!")

            return get(GAME_WORLD_NAME_PATH) as String
        }

        fun getGameWorld() : World
        {
            val world = Instance.server.getWorld(getGameWorldName())

            if(world !is World)
                throw InvalidConfigurationException("$GAME_WORLD_NAME_PATH must be the name of a valid world folder in the server directory!")

            return world
        }

        fun getGameWorldLobbySpawnLocation(): Location
        {
            val possibleSpawnLocation : Any? = get(GAME_WORLD_LOBBY_SPAWN_LOCATION_PATH)
            val possibleSpawnRotation : Any? = get(GAME_WORLD_LOBBY_SPAWN_ROTATION_PATH)

            if(possibleSpawnLocation !is List<*>)
                throw InvalidConfigurationException("$GAME_WORLD_LOBBY_SPAWN_LOCATION_PATH should be an array of three floating-point (decimal) numbers! ")

            if(possibleSpawnRotation !is List<*>)
                throw InvalidConfigurationException("$GAME_WORLD_LOBBY_SPAWN_ROTATION_PATH should be an array of two floating-point (decimal) numbers!")

            val spawnLocation : List<Double> = (get(GAME_WORLD_LOBBY_SPAWN_LOCATION_PATH) as List<*>).filterIsInstance<Double>()
            val spawnRotation : List<Double>  = (get(GAME_WORLD_LOBBY_SPAWN_ROTATION_PATH) as List<*>).filterIsInstance<Double>()

            if (spawnLocation.size != 3)
                throw InvalidConfigurationException("$GAME_WORLD_LOBBY_SPAWN_LOCATION_PATH should be an array of three floating-point (decimal) numbers!")

            if (spawnRotation.size != 2)
                throw InvalidConfigurationException("$GAME_WORLD_LOBBY_SPAWN_ROTATION_PATH should be an array of two floating-point (decimal) numbers!")

            return Location(getGameWorld(), spawnLocation[0], spawnLocation[1], spawnLocation[2], spawnRotation[0].toFloat(), spawnRotation[1].toFloat())
        }

        fun getGameWorldLobbyNPCSpawnLocations() : Array<Location>
        {
            val npcSpawnCoordList : List<ArrayList<Double>>? = get(GAME_WORLD_LOBBY_NPCS_LOCATIONS_PATH) as? List<ArrayList<Double>>

            if(npcSpawnCoordList !is List<ArrayList<Double>>)
                throw InvalidConfigurationException("$GAME_WORLD_LOBBY_NPCS_LOCATIONS_PATH should be a list of arrays of three floating-point (decimal) numbers!")

            if(npcSpawnCoordList.isEmpty())
                return arrayOf()

            val npcSpawnLocationArray = arrayOfNulls<Location>(npcSpawnCoordList.size)

            for(i in npcSpawnCoordList.indices)
            {
                if(npcSpawnCoordList[i].size != 3)
                    throw InvalidConfigurationException("$GAME_WORLD_LOBBY_NPCS_LOCATIONS_PATH should be a list of arrays of three floating-point (decimal) numbers!")

                npcSpawnLocationArray[i] = Location(getGameWorld(), npcSpawnCoordList[i][0], npcSpawnCoordList[i][1], npcSpawnCoordList[i][2])
            }

            return npcSpawnLocationArray.requireNoNulls()
        }

        fun areLobbyNPCsEnabled() : Boolean
        {
            val enabled : Any? = get(GAME_WORLD_LOBBY_NPCS_ENABLED_PATH)

            if(enabled !is Boolean)
                throw InvalidConfigurationException("$GAME_WORLD_LOBBY_NPCS_ENABLED_PATH should be a boolean (true/false)")

            return enabled
        }

        fun getDefaultWorldName() : String
        {
            val defaultWorldName : Any? = get(DEFAULT_WORLD_NAME_PATH)

            if(defaultWorldName !is String)
                throw InvalidConfigurationException("$DEFAULT_WORLD_NAME_PATH needs to be a string (word)!")

            return defaultWorldName
        }

        fun getDefaultWorld() : World
        {
            val defaultWorld = Instance.server.getWorld(getDefaultWorldName())

            if(defaultWorld !is World)
                throw InvalidConfigurationException("$GAME_WORLD_NAME_PATH must be the name of a valid world folder in the server directory!")

            return defaultWorld
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

        server.pluginManager.registerEvents(DeadByMinecraftEventListener(), this)
        getCommand("dbm")!!.setExecutor(DeadByMinecraftCommandListener());

        WorldCreator(
            Config.getGameWorldName()
        )
        .generator("flat")
        .environment(World.Environment.NORMAL)
        .createWorld()

        if(DEBUG)
            DeadByMinecraftGameManager.createGame();
    }

    override fun onDisable()
    {
        DeadByMinecraftGameManager.getGames().forEach { it.stop() }
    }
}