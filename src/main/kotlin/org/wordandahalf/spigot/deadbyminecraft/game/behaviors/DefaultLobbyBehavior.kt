package org.wordandahalf.spigot.deadbyminecraft.game.behaviors

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffectType
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.actions.ActionHandler
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerJoinAction
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerLeaveAction
import org.wordandahalf.spigot.deadbyminecraft.config.Config
import org.wordandahalf.spigot.deadbyminecraft.config.LobbyWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.StaticBar
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.StaticText
import org.wordandahalf.spigot.deadbyminecraft.worlds.LobbyWorld

class DefaultLobbyBehavior(config: LobbyWorldConfig, world: LobbyWorld) : LobbyBehavior(config, world)
{
    private val playerNpcs : Array<NPC?> = arrayOfNulls(Config.Main.maxPlayers)

    init
    {
        DeadByMinecraft.Logger.info("A ${DefaultLobbyBehavior::class.java.simpleName} has been constructed for world '${world.bukkit.name}'")
    }

    @ActionHandler
    fun onPlayerJoin(action: PlayerJoinAction)
    {
        val player = action.player

        // If there is an available position and NPCs are enabled
        if(playerNpcs.indexOf(null) != -1 && config.npcs.enabled)
        {
            val nextIndex = playerNpcs.indexOf(null)
            val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.bukkit.displayName)

            val positions = config.npcs.locations[nextIndex]

            npc.spawn(Location(world, positions[0], positions[1], positions[2]))
            playerNpcs[nextIndex] = npc
        }

        // Display the default hotbar menu
        HotbarMenu.Lobby.DEFAULT_MENU.display(player.bukkit)

        // Display the bossbar
        player.userInterface.bossBar(
                StaticBar(
                        StaticText("<red>DeadByMinecraft Lobby #${player.data.gameID}"),
                        BossBar.Color.RED,
                        BossBar.Overlay.PROGRESS,
                        BossBar.Flag.CREATE_WORLD_FOG,
                        BossBar.Flag.DARKEN_SCREEN
                )
        )

        // Teleport the player
        val location = config.spawn.location
        val rotation = config.spawn.rotation

        player.bukkit.teleport(Location(world, location[0], location[1], location[2], rotation[0], rotation[1]))
    }

    @ActionHandler
    fun onPlayerLeave(action: PlayerLeaveAction)
    {
        val player = action.player

        // Remove the player's NPC
        for (i in playerNpcs.indices)
        {
            val npc = playerNpcs[i]

            if(npc?.name == player.bukkit.displayName)
            {
                npc.destroy()
                npc.owningRegistry.deregister(npc)

                playerNpcs[i] = null
            }
        }

        player.bukkit.removePotionEffect(PotionEffectType.SLOW)
        player.bukkit.removePotionEffect(PotionEffectType.INVISIBILITY)
        player.bukkit.teleport(Bukkit.getWorld(Config.Main.defaultWorldName)!!.spawnLocation)

        // Remove any hotbar menu
        HotbarMenu.EMPTY.display(player.bukkit)

        player.userInterface.bossBar(null)
    }
}