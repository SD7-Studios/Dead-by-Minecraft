package org.wordandahalf.spigot.deadbyminecraft

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.item.ScriptableItemStack
import java.lang.NumberFormatException

/**
 * Debug commands. Not for real, end-user use.
 */
class DeadByMinecraftCommandListener : CommandExecutor
{
    private val registeredSubcommands:
        HashMap<String, SubcommandExecutor<*>> =
        hashMapOf(
            Pair("list", ListGamesSubcommandExecutor()),
            Pair("create", CreateSubcommandExecutor()),
            Pair("stop", StopGameSubcommandExecutor()),
            Pair("join", JoinGameSubcommandExecutor()),
            Pair("leave", LeaveGameSubcommandExecutor()),
            Pair("tp", TeleportToLobbySubcommandExecutor()),
            Pair("skin", SetSkinSubcommandExecutor()),
            Pair("give", GiveItemSubcommandExecutor())
        )

    private interface SubcommandExecutor<Sender>
    {
        fun execute(sender: Sender, args: Array<out String>) : Boolean

        /**
         * @return the number of arguments that the subcommand expects INCLUDING the subcommand itself.
         */
        fun getNumberOfArguments() : Int
    }

    private interface GenericSubcommandExecutor : SubcommandExecutor<CommandSender>
    private interface PlayerSubcommandExecutor : SubcommandExecutor<Player>
    private interface DeadByMinecraftPlayerSubcommandExecutor : SubcommandExecutor<DeadByMinecraftPlayer>

    private class ListGamesSubcommandExecutor : PlayerSubcommandExecutor
    {
        override fun execute(sender: Player, args: Array<out String>): Boolean
        {
            if(args.size != 1)
                return false

            if(DeadByMinecraftGameManager.getGames().isEmpty())
            {
                sender.sendMessage("There are no games currently running.")
                return true
            }

            val book = ItemStack(Material.WRITTEN_BOOK, 1)
            val data = book.itemMeta as BookMeta

            data.title = "Dead by Minecraft Games"
            data.author = ""

            DeadByMinecraftGameManager.getGames().forEach {
                data.addPage("Game #${it.id}\nIn ${it.state}\n${it.numberOfPlayers()}/${it.maxPlayers}")
            }

            book.itemMeta = data

            sender.openBook(book)

            return true
        }

        override fun getNumberOfArguments(): Int { return 1 }
    }

    private class CreateSubcommandExecutor : GenericSubcommandExecutor
    {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean
        {
            if(args.size != 1)
                return false

            val newGameID = DeadByMinecraftGameManager.createGame()

            sender.sendMessage("Created game #$newGameID!")
            return true
        }

        override fun getNumberOfArguments(): Int { return 1 }
    }

    private class StopGameSubcommandExecutor : GenericSubcommandExecutor
    {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean
        {
            return if(DeadByMinecraftGameManager.removeGame(args[1].toInt())) {
                sender.sendMessage("Stopped game ${args[1]}...")
                true
            } else {
                sender.sendMessage("Failed to game ${args[1]}!")
                true
            }
        }

        override fun getNumberOfArguments(): Int
        {
            return 2
        }
    }

    private class JoinGameSubcommandExecutor : PlayerSubcommandExecutor
    {
        override fun execute(sender: Player, args: Array<out String>): Boolean
        {
            if(args.size != 2)
                return false

            try
            {
                val gameToJoin = DeadByMinecraftGameManager.getGameByID(args[1].toInt())

                if(gameToJoin is DeadByMinecraftGame)
                {
                    gameToJoin.addPlayer(DeadByMinecraftPlayer.of(sender))
                }
                else
                {
                    sender.sendMessage("${args[1]} is not a valid game ID!")
                }
            }
            catch (e: NumberFormatException)
            {
                sender.sendMessage("${args[1]} is not a number!")
                return false
            }

            return true
        }

        override fun getNumberOfArguments(): Int { return 2 }
    }

    private class LeaveGameSubcommandExecutor : DeadByMinecraftPlayerSubcommandExecutor
    {
        override fun execute(sender: DeadByMinecraftPlayer, args: Array<out String>): Boolean
        {
            if(args.size != 1)
                return false

            if(sender.data.getGame() is DeadByMinecraftGame)
            {
                sender.data.getGame()?.removePlayer(sender)
            }
            else
            {
                sender.bukkit.sendMessage("You are not in a game!")
            }

            return true
        }

        override fun getNumberOfArguments(): Int { return 1 }
    }

    private class TeleportToLobbySubcommandExecutor : PlayerSubcommandExecutor
    {
        override fun execute(sender: Player, args: Array<out String>): Boolean
        {
            when(args[1])
            {
                "lobby" -> {
                    sender.teleport(Bukkit.getWorld(DeadByMinecraftConfig.lobbyWorldName())!!.spawnLocation)

                    return true
                }
                "game" -> {
                    sender.teleport(Bukkit.getWorld(DeadByMinecraftConfig.gameWorldName())!!.spawnLocation)

                    return true
                }
            }

            return false
        }

        override fun getNumberOfArguments(): Int { return 2 }
    }

    private class SetSkinSubcommandExecutor : PlayerSubcommandExecutor
    {
        override fun execute(sender: Player, args: Array<out String>): Boolean
        {
            return true
        }

        override fun getNumberOfArguments(): Int { return 4 }
    }

    private class GiveItemSubcommandExecutor : PlayerSubcommandExecutor
    {
        override fun execute(sender: Player, args: Array<out String>): Boolean
        {
            val itemStack = ScriptableItemStack.getItem(args[1])

            if(itemStack is ItemStack)
                sender.inventory.addItem(itemStack)

            return true
        }

        override fun getNumberOfArguments(): Int { return 2 }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if(args.isEmpty())
            return false

        val subCommandFunction : SubcommandExecutor<*>? = registeredSubcommands[args[0].toLowerCase()]

        if((subCommandFunction !is SubcommandExecutor) || (args.size != subCommandFunction.getNumberOfArguments()))
            return false

        if(subCommandFunction is PlayerSubcommandExecutor)
        {
            if(sender is Player)
            {
                return subCommandFunction.execute(sender, args)
            }
            else
            {
                sender.sendMessage("You must be a player to execute this command!")
            }
        }
        else
        if(subCommandFunction is DeadByMinecraftPlayerSubcommandExecutor)
        {
            if(sender is Player)
            {
                val deadByMinecraftPlayer : DeadByMinecraftPlayer? = DeadByMinecraftPlayer.of(sender)

                if(deadByMinecraftPlayer is DeadByMinecraftPlayer)
                    return subCommandFunction.execute(deadByMinecraftPlayer, args)
                else
                    sender.sendMessage("You must be in a game to execute this command!")
            }
            else
            {
                sender.sendMessage("You must be a player to execute this command!")
            }
        }
        else
        if(subCommandFunction is GenericSubcommandExecutor)
        {
            return subCommandFunction.execute(sender, args)
        }

        return false
    }
}