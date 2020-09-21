package org.wordandahalf.spigot.deadbyminecraft.game.states

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftPlugin
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftPlayer

class DeadByMinecraftLobbyState(game: DeadByMinecraftGame) : DeadByMinecraftGameState(game)
{
    private val playerNpcs : Array<NPC?> = arrayOfNulls(DeadByMinecraftPlugin.Config.getMaxPlayers())

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
        if(playerNpcs.indexOf(null) != -1)
        {
            val nextIndex = playerNpcs.indexOf(null)

            val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.bukkit.displayName)
            npc.spawn(DeadByMinecraftPlugin.Config.getGameWorldLobbyNPCSpawnLocations()[nextIndex])
            playerNpcs[nextIndex] = npc
        }

        player.bukkit.gameMode = GameMode.ADVENTURE
        player.bukkit.velocity = Vector().zero()
        player.bukkit.teleport(DeadByMinecraftPlugin.Config.getGameWorldLobbySpawnLocation())
        player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false, false))
        player.bukkit.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false, false))
    }

    override fun onPlayerLeave(player: DeadByMinecraftPlayer)
    {
        playerNpcs.filter { it?.name == player.bukkit.displayName }.forEach {
            it?.destroy()
            it?.owningRegistry?.deregister(it)
        }
        player.bukkit.removePotionEffect(PotionEffectType.SLOW)
        player.bukkit.removePotionEffect(PotionEffectType.INVISIBILITY)
        player.bukkit.teleport(DeadByMinecraftPlugin.Config.getDefaultWorld().spawnLocation)
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

        game.getPlayers().forEach {
            it.bukkit.removePotionEffect(PotionEffectType.SLOW)
            it.bukkit.removePotionEffect(PotionEffectType.INVISIBILITY)
        }
    }

    override fun toString() : String
    {
        return "Lobby"
    }
}