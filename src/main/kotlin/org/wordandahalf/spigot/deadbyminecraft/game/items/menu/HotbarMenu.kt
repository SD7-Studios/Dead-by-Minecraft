package org.wordandahalf.spigot.deadbyminecraft.game.items.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.items.*
import java.security.InvalidParameterException

/**
 * QoL class for easily switching between a set of ScriptableItemStacks
 */
class HotbarMenu(private vararg val items: ScriptableItemStack?)
{
    companion object { val EMPTY = HotbarMenu() }

    object Lobby
    {
        val DEFAULT_MENU            = HotbarMenu(SelectSurvivorItem(), SelectKillerItem(), null, null, null, null, null, null, null)
        val SURVIVOR_MENU           = HotbarMenu(null, null, null, null, null, null, null, null, GoBackItem { t, _ -> DEFAULT_MENU.display(t.player) })
        val KILLER_SELECTION_MENU   = HotbarMenu(SelectTrapperItem(), SelectWraithItem(), SelectNurseItem(), null, null, null, null, null, GoBackItem { t, _ -> DEFAULT_MENU.display(t.player) })
        val KILLER_MENU             = HotbarMenu(null, null, null, null, null, null, null, null, GoBackItem { t, _ -> KILLER_SELECTION_MENU.display(t.player) })
    }

    init
    {
        if(items.size > 9)
            throw InvalidParameterException("HotbarMenus can have at most nine items!")
    }

    fun display(player: Player)
    {
        items.forEachIndexed {
            i, item ->
            if(item is ScriptableItemStack)
                player.inventory.setItem(i, items[i]!!.toItemStack())
            else
                player.inventory.setItem(i, null)
        }
    }
}