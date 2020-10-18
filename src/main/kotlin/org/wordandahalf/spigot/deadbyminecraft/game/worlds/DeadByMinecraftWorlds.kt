package org.wordandahalf.spigot.deadbyminecraft.game.worlds

import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.SlimeWorld
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import org.bukkit.Bukkit
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.config.DeadByMinecraftConfig
import java.lang.Exception
import java.util.*
import kotlin.random.Random

object DeadByMinecraftWorlds
{
    private val loadedLobbyWorlds = HashMap<String, SlimeWorld>()
    private val loadedGameWorlds = HashMap<String, SlimeWorld>()

    /**
     * Loads the world templates if they have not already been loaded
     * @return true if already loaded or loaded successfully, false otherwise
     */
    fun loadTemplates() : Boolean
    {
        try {
            // The SlimeWorldManager provides all of the methods for loading templates
            val slimePlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin

            // The world templates are saved on disk (has various database support)
            val loader = slimePlugin.getLoader("file")

            // World properties
            val properties = SlimePropertyMap()
            properties.setString(SlimeProperties.DIFFICULTY, "peaceful")
            properties.setBoolean(SlimeProperties.ALLOW_ANIMALS, false)
            properties.setBoolean(SlimeProperties.ALLOW_MONSTERS, false)
            properties.setBoolean(SlimeProperties.PVP, false)
            properties.setString(SlimeProperties.WORLD_TYPE, "customized")

            // Load the lobby template, allow modification if debug mode is enabled
            DeadByMinecraftConfig.Worlds.Lobby.keys.forEach {
                loadedLobbyWorlds[it] = slimePlugin.loadWorld(loader, it, !DeadByMinecraft.DEBUG, properties)
                slimePlugin.generateWorld(loadedLobbyWorlds[it])
            }

            // The game world has different properties
            properties.setString(SlimeProperties.DIFFICULTY, "hard")
            properties.setBoolean(SlimeProperties.PVP, false)

            // ""
            DeadByMinecraftConfig.Worlds.Game.keys.forEach {
                loadedGameWorlds[it] = slimePlugin.loadWorld(loader, it, !DeadByMinecraft.DEBUG, properties)
                slimePlugin.generateWorld(loadedGameWorlds[it])
            }
        }
        catch (e: Exception)
        {
            DeadByMinecraft.Logger.warning(e.stackTraceToString())
            return false
        }

        return true
    }

    fun cloneLobby() : DeadByMinecraftLobbyWorld
    {
        // Load a copy of the lobby template with a guaranteed random name
        val templateWorld = loadedLobbyWorlds.values.toTypedArray()[Random.nextInt(loadedLobbyWorlds.size)]
        val clonedWorld = templateWorld.clone("${templateWorld.name}-${UUID.randomUUID()}")
        (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(clonedWorld)

        // Set the time as is it is in the config
        val config = DeadByMinecraftConfig.Worlds.Lobby[templateWorld.name]!!
        Bukkit.getWorld(clonedWorld.name)!!.time = config.time

        return DeadByMinecraftLobbyWorld(clonedWorld, config)
    }

    fun cloneGame() : DeadByMinecraftGameWorld
    {
        // Load a copy of the game template with a guaranteed random name
        val templateWorld = loadedGameWorlds.values.toTypedArray()[Random.nextInt(loadedGameWorlds.size)]
        val clonedWorld = templateWorld.clone("${templateWorld.name}-${UUID.randomUUID()}")
        (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(clonedWorld)

        // Set the time as is in the config
        val config = DeadByMinecraftConfig.Worlds.Game[templateWorld.name]!!
        Bukkit.getWorld(clonedWorld.name)!!.time = config.time

        return DeadByMinecraftGameWorld(clonedWorld, config)
    }
}