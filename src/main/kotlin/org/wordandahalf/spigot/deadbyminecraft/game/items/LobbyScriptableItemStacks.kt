package org.wordandahalf.spigot.deadbyminecraft.game.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.Template
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import org.wordandahalf.spigot.deadbyminecraft.game.notification.RevealingActionBarNotification
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.DeadByMinecraftSurvivorRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftKillerRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftNurseRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftTrapperRole
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftWraithRole
import org.wordandahalf.spigot.deadbyminecraft.game.items.menu.HotbarMenu
import org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`.DeadByMinecraftPlayerInterface
import org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`.Text
import org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`.TimedText

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
            RevealingActionBarNotification(
                TextComponent.ofChildren(
                    Component.text(
                "You chose to be a ",
                        TextColor.color(0xFFFBCD), TextDecoration.ITALIC
                    ),
                    Component.text(
                        player.data.role.toString(),
                        TextColor.color(0x33FF33), TextDecoration.ITALIC, TextDecoration.BOLD
                    ),
                    Component.text(
                        "!",
                        TextColor.color(0xFFFBCD), TextDecoration.ITALIC
                    )
                ),
            750
            ).send(t.player)
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
                DeadByMinecraft.Audience.player(t.player).sendActionBar(
                    Component.text("The killer limit has been reached.", TextColor.fromHexString("#FFFBCD"))
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
            RevealingActionBarNotification(
                TextComponent.ofChildren(
                    Component.text(
                        "You chose to be the ", TextColor.color(0xFFFBCD), TextDecoration.ITALIC
                    ),
                    Component.text(
                        player.data.role.toString(),
                        TextColor.color(0x990000), TextDecoration.ITALIC, TextDecoration.BOLD
                    ),
                    Component.text(
                        "!",
                        TextColor.color(0xFFFBCD), TextDecoration.ITALIC
                    )
                ),
                1500
            ).send(t.player)
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