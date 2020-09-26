package org.wordandahalf.spigot.deadbyminecraft.item.menu

import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.item.*
import java.security.InvalidParameterException

/**
 * QoL class for easily switching between a set of ScriptableItemStacks
 */
class HotbarMenu(private vararg val items: ScriptableItemStack?)
{
    companion object { val EMPTY = HotbarMenu() }

    object Lobby
    {
        val DEFAULT_MENU = HotbarMenu(SelectSurvivorItem(), SelectKillerItem())
        val SURVIVOR_MENU = HotbarMenu(null, null, null, null, null, null, null, null, GoBackItem(DEFAULT_MENU))
        val KILLER_MENU = HotbarMenu(SelectTrapperItem(), SelectWraithItem(), SelectNurseItem(), null, null, null, null, null, GoBackItem(DEFAULT_MENU))
    }

    init
    {
        if(items.size > 9)
            throw InvalidParameterException("HotbarMenus can have at most nine items!")
    }

    fun display(player: Player)
    {
        for(i in 0..8)
        {
            if(i < items.size && items[i] is ScriptableItemStack)
            {
                player.inventory.setItem(i, items[i]?.toItemStack())
            }
            else
            {
                player.inventory.setItem(i, null)
            }
        }
    }
}