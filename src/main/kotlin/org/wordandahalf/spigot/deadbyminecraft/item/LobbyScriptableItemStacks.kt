package org.wordandahalf.spigot.deadbyminecraft.item

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftSurvivorRole
import org.wordandahalf.spigot.deadbyminecraft.item.menu.HotbarMenu

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
            HotbarMenu.Lobby.KILLER_MENU.display(t.player)
        }
    }

    override fun getMaterial() : Material { return Material.GUNPOWDER }
}

class SelectTrapperItem : ScriptableItemStack(Executor())
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            t.player.sendMessage("Selected trapper!")
        }
    }

    override fun getMaterial() : Material { return Material.MAGMA_CREAM }
}

class SelectWraithItem : ScriptableItemStack(Executor())
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            t.player.sendMessage("Selected wraith!")
        }
    }

    override fun getMaterial() : Material { return Material.GHAST_TEAR }
}

class SelectNurseItem : ScriptableItemStack(Executor())
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            t.player.sendMessage("Selected nurse!")
        }
    }

    override fun getMaterial() : Material { return Material.BLAZE_POWDER }
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