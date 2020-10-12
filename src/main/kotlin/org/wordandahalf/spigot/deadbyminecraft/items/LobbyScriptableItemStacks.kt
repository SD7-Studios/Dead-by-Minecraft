package org.wordandahalf.spigot.deadbyminecraft.items

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.DeadByMinecraftSurvivorRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftKillerRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftNurseRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftTrapperRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftWraithRole
import org.wordandahalf.spigot.deadbyminecraft.items.menu.HotbarMenu

//
// Items that are used in the lobby hotbar menus.
//

class SelectSurvivorItem : ScriptableItemStack(Executor())
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            // Give the player the survivor role
            val player = DeadByMinecraftPlayer.of(t.player)
            player.data.role = DeadByMinecraftSurvivorRole()
            // Display the survivor menu
            HotbarMenu.Lobby.SURVIVOR_MENU.display(t.player)

            // Display a message
            player.sendMessage(ChatMessageType.ACTION_BAR,
                *ComponentBuilder()
                .color(ChatColor.GOLD)
                .append("You choose to be a ")
                .bold(true)
                .color(ChatColor.GREEN)
                .append(player.data.role.toString())
                .bold(false)
                .color(ChatColor.GOLD).append("!")
                .create()
            )
        }
    }

    override fun getMaterial() : Material { return Material.GLOWSTONE_DUST }
}

class SelectKillerItem : ScriptableItemStack(Executor())
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            val player = DeadByMinecraftPlayer.of(t.player)

            if(DeadByMinecraftGameManager.getGameByPlayer(player)?.hasKiller() == false)
            {
                HotbarMenu.Lobby.KILLER_SELECTION_MENU.display(t.player)
            }
            else
            {
                // Display a message
                player.sendMessage(ChatMessageType.ACTION_BAR,
                        *ComponentBuilder()
                                .color(ChatColor.RED)
                                .append("Someone already chose to be the killer!")
                                .create()
                )
            }
        }
    }

    override fun getMaterial() : Material { return Material.GUNPOWDER }
}

abstract class SelectKillerRoleItem(killerRole: Class<out DeadByMinecraftKillerRole>) : ScriptableItemStack(Executor(killerRole))
{
    class Executor(private val killerRole: Class<out DeadByMinecraftKillerRole>) : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            // Give the player the killer role
            val player = DeadByMinecraftPlayer.of(t.player)
            player.data.role = killerRole.getConstructor().newInstance()
            // Display the survivor menu
            HotbarMenu.Lobby.KILLER_MENU.display(t.player)

            // Display a message
            player.sendMessage(ChatMessageType.ACTION_BAR,
                    *ComponentBuilder()
                            .color(ChatColor.GOLD)
                            .append("You choose to be the ")
                            .bold(true)
                            .color(ChatColor.DARK_RED)
                            .append(player.data.role.toString())
                            .bold(false)
                            .color(ChatColor.GOLD).append("!")
                            .create()
            )
        }
    }
}

class SelectTrapperItem : SelectKillerRoleItem(DeadByMinecraftTrapperRole::class.java)
{
    override fun getMaterial() : Material { return Material.MAGMA_CREAM }
}

class SelectWraithItem : SelectKillerRoleItem(DeadByMinecraftWraithRole::class.java)
{
    override fun getMaterial() : Material { return Material.GHAST_TEAR }
}

class SelectNurseItem : SelectKillerRoleItem(DeadByMinecraftNurseRole::class.java)
{
    override fun getMaterial() : Material { return Material.GHAST_TEAR }
}

class GoBackItem(executor: (t: PlayerInteractEvent, u: ItemStack) -> Unit) : ScriptableItemStack(Executor(executor))
{
    class Executor(private val executor: (t: PlayerInteractEvent, u: ItemStack) -> Unit) : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            executor(t, u)
        }
    }

    override fun getMaterial(): Material { return Material.GOLD_NUGGET }
}