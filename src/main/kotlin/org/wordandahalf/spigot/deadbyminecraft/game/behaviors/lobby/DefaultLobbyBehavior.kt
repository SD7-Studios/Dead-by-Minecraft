package org.wordandahalf.spigot.deadbyminecraft.game.behaviors.lobby

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
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
import org.wordandahalf.spigot.deadbyminecraft.game.Game
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.player.roles.SurvivorRole
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.KillerRole
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.animations.RevealingText
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.bar.LobbyInfoBar
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Timer
import org.wordandahalf.spigot.deadbyminecraft.worlds.LobbyWorld
import java.util.*
import kotlin.math.ceil

class DefaultLobbyBehavior(config: LobbyWorldConfig, world: LobbyWorld) : LobbyBehavior(config, world)
{
    private val COUNTDOWN_TIMER_LENGTH = 15

    private val playerInfoBars = hashMapOf<DeadByMinecraftPlayer, LobbyInfoBar>()
    private var countdownTimer : Timer? = null
    private fun getStateData(player: DeadByMinecraftPlayer) : Pair<String, Float>
    {
        val game = player.data.getGame()!!

        // Waiting for more players to join
        if(game.numberOfPlayers() < Config.Main.minPlayers)
            return Pair("Waiting for players (${game.numberOfPlayers()}/${Config.Main.minPlayers})...", game.numberOfPlayers().toFloat() / Config.Main.minPlayers.toFloat())

        // Waiting for more survivors to be killers...
        if(game.getKillerRatio() < Config.Main.killerRatio)
            return Pair("Waiting for killers (${game.numberOfKillers()}/${ceil(Config.Main.killerRatio * game.numberOfPlayers()).toInt()})...", (game.getKillerRatio() / Config.Main.killerRatio).toFloat())

        if(countdownTimer == null)
            countdownTimer = Timer(COUNTDOWN_TIMER_LENGTH, this::startGame)

        return Pair("Game starting in: " + countdownTimer!!, countdownTimer!!.secondsRemaining.toFloat() / COUNTDOWN_TIMER_LENGTH.toFloat())
    }

    private val playerNpcs : Array<NPC?> = arrayOfNulls(Config.Main.maxPlayers)

    override fun dispose()
    {
        super.dispose()

        playerNpcs.forEach {
            it?.destroy()
            it?.owningRegistry?.deregister(it)
        }
    }

    private fun startGame()
    {
        world.players.forEach {
            val player = DeadByMinecraftPlayer.of(it)
            val game = player.data.getGame()!!

            Actions.submit(PlayerLeaveAction(world, DeadByMinecraftPlayer.of(it)))
            Actions.submit(PlayerJoinAction(game.gameWorld.bukkit, DeadByMinecraftPlayer.of(it)))
        }
    }

    @ActionHandler
    fun onPlayerJoin(action: PlayerJoinAction)
    {
        val player = action.player

        // TODO: too many people in one spot push each other around, hide other players
        // TODO: Timer doesn't stop when state changes from timer

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
        val newBar = LobbyInfoBar(player, this::getStateData)
        playerInfoBars[player] = newBar
        player.userInterface.bossBar(newBar)

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

        playerInfoBars.remove(player)
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
        if(player.data.role is SurvivorRole && player.data.getGame()!!.getKillerRatio() >= Config.Main.killerRatio)
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