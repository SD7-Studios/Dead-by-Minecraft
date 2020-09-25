package org.wordandahalf.spigot.deadbyminecraft

import org.bukkit.configuration.InvalidConfigurationException

object DeadByMinecraftConfig
{
    private const val MAX_PLAYERS_PATH = "max-players"

    private const val LOBBY_WORLD_NAME_PATH = "lobby.world-name"
    private const val LOBBY_WORLD_TIME_PATH = "lobby.time"
    private const val LOBBY_SPAWN_LOCATION_PATH = "lobby.spawn-location"
    private const val LOBBY_SPAWN_ROTATION_PATH = "lobby.spawn-rotation"
    private const val LOBBY_NPCS_ENABLED_PATH = "lobby.npcs.enabled"
    private const val LOBBY_NPCS_LOCATIONS_PATH = "lobby.npcs.locations"

    private const val GAME_WORLD_NAME_PATH = "game.world-name"
    private const val GAME_WORLD_TIME_PATH = "game.time"

    private const val DEFAULT_WORLD_NAME_PATH = "default-world.name"

    fun get(key: String) : Any?
    {
        return DeadByMinecraftPlugin.Instance.config.get(key)
    }

    fun defaultWorldName() : String
    {
        val defaultWorldName : Any? = get(DEFAULT_WORLD_NAME_PATH)

        if(defaultWorldName !is String)
            throw InvalidConfigurationException("$DEFAULT_WORLD_NAME_PATH needs to be a string (word)!")

        return defaultWorldName
    }

    fun maxPlayers() : Int
    {
        val maxPlayers : Any? = get(MAX_PLAYERS_PATH)

        if(maxPlayers !is Integer)
            throw InvalidConfigurationException("$MAX_PLAYERS_PATH must be an integer (number)! Found a(n) ${ get(MAX_PLAYERS_PATH)?.javaClass?.simpleName } instead.")

        return maxPlayers.toInt()
    }

    fun lobbyWorldName() : String
    {
        if(get(LOBBY_WORLD_NAME_PATH) !is String)
            throw InvalidConfigurationException("$LOBBY_WORLD_NAME_PATH must be a string (word)!")

        return get(LOBBY_WORLD_NAME_PATH) as String
    }

    fun lobbyWorldTime() : Integer
    {
        if(get(LOBBY_WORLD_TIME_PATH) !is Integer)
            throw InvalidConfigurationException("$LOBBY_WORLD_TIME_PATH must be an integer!")

        return get(LOBBY_WORLD_TIME_PATH) as Integer
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

    fun gameWorldName() : String
    {
        if(get(GAME_WORLD_NAME_PATH) !is String)
            throw InvalidConfigurationException("$GAME_WORLD_NAME_PATH must be a string (word)!")

        return get(GAME_WORLD_NAME_PATH) as String
    }

    fun gameWorldTime() : Integer
    {
        if(get(GAME_WORLD_TIME_PATH) !is Integer)
            throw InvalidConfigurationException("$GAME_WORLD_TIME_PATH must be an integer!")

        return get(GAME_WORLD_TIME_PATH) as Integer
    }
}