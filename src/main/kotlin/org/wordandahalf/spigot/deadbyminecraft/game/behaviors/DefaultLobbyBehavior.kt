package org.wordandahalf.spigot.deadbyminecraft.game.behaviors

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import org.wordandahalf.spigot.deadbyminecraft.actions.*
import org.wordandahalf.spigot.deadbyminecraft.config.Config
import org.wordandahalf.spigot.deadbyminecraft.config.LobbyWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu
import org.wordandahalf.spigot.deadbyminecraft.player.roles.SurvivorRole
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.KillerRole
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.StaticBar
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.StaticText
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.animations.RevealingText
import org.wordandahalf.spigot.deadbyminecraft.worlds.LobbyWorld

class DefaultLobbyBehavior(config: LobbyWorldConfig, world: LobbyWorld) : LobbyBehavior(config, world)
{
    private val playerNpcs : Array<NPC?> = arrayOfNulls(Config.Main.maxPlayers)

    override fun dispose()
    {
        super.dispose()

        playerNpcs.forEach {
            it?.destroy()
            it?.owningRegistry?.deregister(it)
        }
    }

    @ActionHandler
    fun onPlayerJoin(action: PlayerJoinAction)
    {
        val player = action.player

        // If there is a position available and NPCs are enabled, spawn a NPC for the player.
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

        // Teleport the player.
        val location = config.spawn.location
        val rotation = config.spawn.rotation

        player.bukkit.teleport(Location(world, location[0], location[1], location[2], rotation[0], rotation[1]))

        player.bukkit.gameMode = GameMode.ADVENTURE
        player.bukkit.velocity = Vector().zero()
        player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false, false))
        player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false, false))
    }

    @ActionHandler
    fun onPlayerLeave(action: PlayerLeaveAction)
    {
        val player = action.player

        // Remove the player's NPC.
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

        // Remove any hotbar menu.
        HotbarMenu.EMPTY.display(player.bukkit)

        player.userInterface.bossBar(null)
    }

    @ActionHandler
    fun playerChoosesSurvivorRole(action: PlayerChooseSurvivorRoleAction)
    {
        val player = action.player

        // If the player is a killer, teleport them to the spawn.
        if(player.data.role is KillerRole)
        {
            val location = config.spawn.location
            val rotation = config.spawn.rotation

            player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, false))
            player.bukkit.teleport(Location(player.bukkit.world, location[0], location[1], location[2], rotation[0], rotation[1]))
        }

        // Display the survivor menu.
        HotbarMenu.Lobby.SURVIVOR_MENU.display(player.bukkit)

        player.data.role = SurvivorRole()

        // Display a message.
        player.userInterface.subtitle(
            RevealingText(
                    1000,
                    "<color:#FFFBCD><italic>You chose to be a <color:#33FF33><bold>${player.data.role.toString()}</bold><color:#FFFBCD>!",
            )
        )
    }

    @ActionHandler
    fun playerChoosesKillerRole(action: PlayerChooseKillerRoleAction)
    {
        val player = action.player

        // If the player is not already a killer and the game has enough killers, display a message.
        if(player.data.role !is KillerRole && player.data.getGame()!!.getKillerRatio() >= Config.Main.killerRatio)
        {
            player.userInterface.subtitle(
                RevealingText(1000, "<color:#FFFBCD>The killer limit has been reached.")
            )

            return
        }

        // If the player is not already a killer, teleport them to the killer position.
        if(player.data.role !is KillerRole)
        {
            val location = config.killerPosition.location
            val rotation = config.killerPosition.rotation

            player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, false))
            player.bukkit.teleport(Location(player.bukkit.world, location[0], location[1], location[2], rotation[0], rotation[1]))
        }

        // Display the killer's ability menu.
        HotbarMenu.Lobby.KILLER_MENU.display(player.bukkit)

        player.data.role = action.role

        // Display a message.
        player.userInterface.subtitle(
                RevealingText(
                        1000,
                        "<italic><color:#FFFBCD>You chose to be the <bold><color:#990000>${player.data.role.toString()}</bold><color:#FFFBCD>!"
                )
        )
    }
}