package org.wordandahalf.spigot.deadbyminecraft.item.menu

import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.item.*
import java.security.InvalidParameterException

class HotbarMenu(vararg val items: ScriptableItemStack?)
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

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HotbarMenu

        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int
    {
        return items.contentHashCode()
    }
}