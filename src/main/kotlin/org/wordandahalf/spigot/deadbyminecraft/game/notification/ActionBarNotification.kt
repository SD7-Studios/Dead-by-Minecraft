package org.wordandahalf.spigot.deadbyminecraft.game.notification

import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft

open class ActionBarNotification(text: TextComponent) : DeadByMinecraftGameNotification(text)
{
    override fun send(p: Player)
    {
        DeadByMinecraft.Audience.player(p).sendActionBar(text)
    }
}