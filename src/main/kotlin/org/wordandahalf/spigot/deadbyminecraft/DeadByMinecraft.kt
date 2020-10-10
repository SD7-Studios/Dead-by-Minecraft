package org.wordandahalf.spigot.deadbyminecraft

import org.bukkit.plugin.java.JavaPlugin
import org.wordandahalf.spigot.deadbyminecraft.events.DeadByMinecraftCommandListener
import org.wordandahalf.spigot.deadbyminecraft.events.DeadByMinecraftEventListener
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import java.util.logging.Logger

class DeadByMinecraft : JavaPlugin()
{
    // Static variables
    companion object
    {
        lateinit var instance : DeadByMinecraft
        lateinit var Logger : Logger

        const val DEBUG : Boolean = true
    }

    override fun onEnable()
    {
        instance = this
        Logger = this.logger

        instance.logger.info("Dead by Minecraft has loaded!")

        // Saves the provided configuration file if none exists
        saveDefaultConfig()

        // Registers the event listener
        server.pluginManager.registerEvents(DeadByMinecraftEventListener(), this)

        // Registers the /dbm command
        getCommand("dbm")!!.setExecutor(DeadByMinecraftCommandListener());

        // Loads world templates into memory
        DeadByMinecraftWorlds.loadTemplates()

        // If debug mode is enabled, automatically start a game
        if(DEBUG)
            DeadByMinecraftGameManager.createGame();
    }

    override fun onDisable()
    {
        // Stop all games
        DeadByMinecraftGameManager.getGames().forEach { it.stop() }
    }
}