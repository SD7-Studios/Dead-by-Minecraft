package org.wordandahalf.spigot.deadbyminecraft.game.states

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftConfig
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu

/**
 * Handles all logic for a DeadByMinecraftGame when it is in the lobby
 */
class DeadByMinecraftLobbyState(game: DeadByMinecraftGame) : DeadByMinecraftGameState(game)
{
    private val playerNpcs : Array<NPC?> = arrayOfNulls(DeadByMinecraftConfig.maxPlayers())

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
        if(playerNpcs.indexOf(null) != -1 && DeadByMinecraftConfig.areLobbyNPCsEnabled())
        {
            val nextIndex = playerNpcs.indexOf(null)
            val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.bukkit.displayName)

            val positions = DeadByMinecraftConfig.lobbyNPCSpawnLocations()[nextIndex]

            npc.spawn(Location(game.bukkitLobbyWorld(), positions[0], positions[1], positions[2]))
            playerNpcs[nextIndex] = npc
        }

        // Display the default hotbar menu
        HotbarMenu.Lobby.DEFAULT_MENU.display(player.bukkit)

        // Teleport the player
        val positions = DeadByMinecraftConfig.lobbySpawnLocation()
        player.bukkit.teleport(Location(game.bukkitLobbyWorld(), positions[0], positions[1], positions[2], positions[3].toFloat(), positions[4].toFloat()))

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
        player.bukkit.teleport(Bukkit.getWorld(DeadByMinecraftConfig.defaultWorldName())!!.spawnLocation)

        // Remove any hotbar menu
        HotbarMenu.EMPTY.display(player.bukkit)
    }

    /**
     * Called when the parent DeadByMinecraftGame switches to another state, before that state's #onEnter()
     */
    override fun onLeave()
    {
        playerNpcs.forEach {
            it?.destroy()
            it?.owningRegistry?.deregister(it)
        }

        game.players.forEach {
            it.bukkit.removePotionEffect(PotionEffectType.SLOW)
            it.bukkit.removePotionEffect(PotionEffectType.INVISIBILITY)
        }
    }

    override fun toString() : String
    {
        return "Lobby"
    }
}