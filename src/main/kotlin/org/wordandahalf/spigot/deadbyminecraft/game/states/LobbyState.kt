package org.wordandahalf.spigot.deadbyminecraft.game.states

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
import org.wordandahalf.spigot.deadbyminecraft.config.Config
import org.wordandahalf.spigot.deadbyminecraft.game.Game
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu
import org.wordandahalf.spigot.deadbyminecraft.game.player.ui.elements.StaticBar
import org.wordandahalf.spigot.deadbyminecraft.game.player.ui.elements.StaticText

/**
 * Handles all logic for a DeadByMinecraftGame when it is in the lobby
 */
class LobbyState(game: Game) : GameState(game)
{
    private enum class State
    {
        WAITING_FOR_PLAYERS,    // The lobby is waiting for more players
        WAITING_FOR_KILLERS,    // The lobby is waiting for one (or more) players to choose to be a killer
        FINAL_TIMER             // The lobby is waiting for the final countdown to end
    }

    private val playerNpcs : Array<NPC?> = arrayOfNulls(Config.Main.maxPlayers)
    private var state = State.WAITING_FOR_PLAYERS

    /**
     * Called when the parent DeadByMinecraftGame switches to this state, after #onLeave()
     * <br/>
     * Cannot depend on any state
     */
    override fun onEnter()
    {

    }

    override fun onPlayerJoin(player: DeadByMinecraftPlayer)
    {
        // If there is an available position and NPCs are enabled
        if(playerNpcs.indexOf(null) != -1 && game.lobbyWorld.config.npcs.enabled)
        {
            val nextIndex = playerNpcs.indexOf(null)
            val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.bukkit.displayName)

            val positions = game.lobbyWorld.config.npcs.locations[nextIndex]

            npc.spawn(Location(game.lobbyWorld.bukkit, positions[0], positions[1], positions[2]))
            playerNpcs[nextIndex] = npc
        }

        // Display the default hotbar menu
        HotbarMenu.Lobby.DEFAULT_MENU.display(player.bukkit)

        // Display the bossbar
        player.userInterface.bossBar(
                StaticBar(
                        StaticText("<red>DeadByMinecraft Lobby #${game.id}"),
                        BossBar.Color.RED,
                        BossBar.Overlay.PROGRESS,
                        BossBar.Flag.CREATE_WORLD_FOG,
                        BossBar.Flag.DARKEN_SCREEN
                )
        )

        // Teleport the player
        val location = game.lobbyWorld.config.spawn.location
        val rotation = game.lobbyWorld.config.spawn.rotation

        player.bukkit.teleport(Location(game.lobbyWorld.bukkit, location[0], location[1], location[2], rotation[0], rotation[1]))

        player.bukkit.gameMode = GameMode.ADVENTURE
        player.bukkit.velocity = Vector().zero()
        player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false, false))
        player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false, false))
    }

    override fun onPlayerLeave(player: DeadByMinecraftPlayer)
    {
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

    /**
     * Called when the parent DeadByMinecraftGame switches to another state, before that state's #onEnter()
     */
    override fun onLeave()
    {
        game.players.forEach {
            onPlayerLeave(it)
        }
    }

    private fun updateState()
    {

    }

    override fun toString() : String
    {
        return "Lobby"
    }
}