package org.wordandahalf.spigot.deadbyminecraft.game.items

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.wordandahalf.spigot.deadbyminecraft.actions.Actions
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerChooseKillerRoleAction
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerChooseSurvivorRoleAction
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.player.roles.SurvivorRole
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.KillerRole
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.NurseRole
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.TrapperRole
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.WraithRole
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.animations.RevealingText

//
// Items that are used in the lobby hotbar menus.
//

class SelectSurvivorItem : ScriptableItemStack(Executor())
{
    class Executor : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            val player = DeadByMinecraftPlayer.of(t.player)

            Actions.submit(PlayerChooseSurvivorRoleAction(player.bukkit.world, player))
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
            HotbarMenu.Lobby.KILLER_SELECTION_MENU.display(t.player)
        }
    }

    override fun getMaterial() : Material { return Material.GUNPOWDER }
}

abstract class SelectKillerRoleItem(killerRole: Class<out KillerRole>) : ScriptableItemStack(Executor(killerRole))
{
    class Executor(private val killerRole: Class<out KillerRole>) : ScriptableItemStack.Executor()
    {
        override fun accept(t: PlayerInteractEvent, u: ItemStack)
        {
            val player = DeadByMinecraftPlayer.of(t.player)

            Actions.submit(PlayerChooseKillerRoleAction(player.bukkit.world, player, killerRole.getConstructor().newInstance()))
        }
    }
}

class SelectTrapperItem : SelectKillerRoleItem(TrapperRole::class.java)
{
    override fun getMaterial() : Material { return Material.MAGMA_CREAM }
}

class SelectWraithItem : SelectKillerRoleItem(WraithRole::class.java)
{
    override fun getMaterial() : Material { return Material.GHAST_TEAR }
}

class SelectNurseItem : SelectKillerRoleItem(NurseRole::class.java)
{
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