package org.wordandahalf.spigot.deadbyminecraft.item

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class GoBackItem : ScriptableItemStack(Executor::class)
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            t.player.sendMessage("Hello, world!")
        }
    }

    override fun getMaterial(): Material { return Material.GOLD_NUGGET }
}