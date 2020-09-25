package org.wordandahalf.spigot.deadbyminecraft

import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.SlimeWorld
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import java.lang.Exception
import java.util.*
import java.util.logging.Logger

class DeadByMinecraftPlugin() : JavaPlugin()
{
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

                lobbyWorld = slimePlugin.loadWorld(loader, DeadByMinecraftConfig.lobbyWorldName(), !DEBUG, properties)
                slimePlugin.generateWorld(lobbyWorld)

                properties.setString(SlimeProperties.DIFFICULTY, "hard")
                properties.setBoolean(SlimeProperties.PVP, false)

                gameWorld = slimePlugin.loadWorld(loader, DeadByMinecraftConfig.gameWorldName(), !DEBUG, properties)
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

            Bukkit.getWorld(world.name)!!.time = DeadByMinecraftConfig.lobbyWorldTime().toLong()

            return world
        }

        fun cloneGameWorld() : SlimeWorld
        {
            val world = gameWorld.clone(GAME_WORLD_PREFIX + UUID.randomUUID().toString())
            (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(world)

            Bukkit.getWorld(world.name)!!.time = DeadByMinecraftConfig.gameWorldTime().toLong()

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