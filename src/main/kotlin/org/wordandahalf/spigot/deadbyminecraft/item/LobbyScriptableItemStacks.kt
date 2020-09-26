package org.wordandahalf.spigot.deadbyminecraft.item

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
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
            HotbarMenu.Lobby.SURVIVOR_MENU.display(t.player)
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

class GoBackItem(menu: HotbarMenu) : ScriptableItemStack(Executor(menu))
{
    class Executor(val menu: HotbarMenu) : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            menu.display(t.player)
        }
    }

    override fun getMaterial(): Material { return Material.GOLD_NUGGET }
}