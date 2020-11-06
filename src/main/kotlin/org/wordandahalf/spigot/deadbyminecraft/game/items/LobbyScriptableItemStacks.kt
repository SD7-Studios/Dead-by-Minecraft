package org.wordandahalf.spigot.deadbyminecraft.game.items

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
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
            // Give the player the survivor role
            val player = DeadByMinecraftPlayer.of(t.player)
            player.data.role = SurvivorRole()
            // Display the survivor menu
            HotbarMenu.Lobby.SURVIVOR_MENU.display(t.player)

            // Display a message
            player.userInterface.subtitle(
                RevealingText(
                        1000,
                    "<color:#FFFBCD><italic>You chose to be a <color:#33FF33><bold>${player.data.role.toString()}</bold><color:#FFFBCD>!",
                )
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
            val game = player.data.getGame()

            if(game?.hasKiller() == false)
            {
                // Display the killer selection menu
                HotbarMenu.Lobby.KILLER_SELECTION_MENU.display(t.player)
            }
            else
            {
                // Display a message
                player.userInterface.subtitle(
                    RevealingText(1000, "<color:#FFFBCD>The killer limit has been reached.")
                )
            }
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
            // Give the player the killer role
            val player = DeadByMinecraftPlayer.of(t.player)
            player.data.role = killerRole.getConstructor().newInstance()
            // Display the survivor menu
            HotbarMenu.Lobby.KILLER_MENU.display(t.player)

            // Display a message
            player.userInterface.subtitle(
                RevealingText(
                    1000,
                    "<italic><color:#FFFBCD>You chose to be the <bold><color:#990000>${player.data.role.toString()}</bold><color:#FFFBCD>!"
                )
            )
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