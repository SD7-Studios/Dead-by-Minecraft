package org.wordandahalf.spigot.deadbyminecraft.items.menu

import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.items.*
import java.security.InvalidParameterException

/**
 * QoL class for easily switching between a set of ScriptableItemStacks
 */
class HotbarMenu(private vararg val items: ScriptableItemStack?)
{
    companion object { val EMPTY = HotbarMenu() }

    object Lobby
    {
        val DEFAULT_MENU            = HotbarMenu(SelectSurvivorItem(), SelectKillerItem())
        val SURVIVOR_MENU           = HotbarMenu(null, null, null, null, null, null, null, null, GoBackItem { t, _ -> DeadByMinecraftPlayer.of(t.player).data.role = null; DEFAULT_MENU.display(t.player) })
        val KILLER_SELECTION_MENU   = HotbarMenu(SelectTrapperItem(), SelectWraithItem(), SelectNurseItem(), null, null, null, null, null, GoBackItem { t, _ -> DEFAULT_MENU.display(t.player) })
        val KILLER_MENU             = HotbarMenu(null, null, null, null, null, null, null, null, GoBackItem { t, _ -> DeadByMinecraftPlayer.of(t.player).data.role = null; KILLER_SELECTION_MENU.display(t.player) })
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