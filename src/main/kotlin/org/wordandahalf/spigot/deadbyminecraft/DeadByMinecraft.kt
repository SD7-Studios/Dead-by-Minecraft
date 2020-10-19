package org.wordandahalf.spigot.deadbyminecraft

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.wordandahalf.spigot.deadbyminecraft.config.Config
import org.wordandahalf.spigot.deadbyminecraft.events.listeners.CommandListener
import org.wordandahalf.spigot.deadbyminecraft.events.listeners.MinecraftEventListener
import org.wordandahalf.spigot.deadbyminecraft.game.GameManager
import org.wordandahalf.spigot.deadbyminecraft.game.worlds.Worlds
import java.util.logging.Logger

class DeadByMinecraft : JavaPlugin()
{
    // Static variables
    companion object
    {
        lateinit var Instance : DeadByMinecraft
        lateinit var Logger : Logger
        lateinit var Audience : BukkitAudiences

        const val DEBUG : Boolean = true
    }

    override fun onEnable()
{
        Instance = this
        Logger = this.logger

        Instance.logger.info("Dead by Minecraft has loaded!")

        Config.load()
        Logger.info("Max players: " + Config.Main.maxPlayers)

        // Registers the event listener
        server.pluginManager.registerEvents(MinecraftEventListener(), this)

        // Registers the /dbm command
        getCommand("dbm")!!.setExecutor(CommandListener());

        // Loads world templates into memory
        Worlds.loadTemplates()

        Audience = BukkitAudiences.create(this)

        // If debug mode is enabled, automatically start a game
        if(DEBUG)
            GameManager.create()
    }

    override fun onDisable()
    {
        // Stop all games
        GameManager.all().forEach { it.stop() }

        // Stop all tasks
        Bukkit.getScheduler().cancelTasks(this)
    }
}