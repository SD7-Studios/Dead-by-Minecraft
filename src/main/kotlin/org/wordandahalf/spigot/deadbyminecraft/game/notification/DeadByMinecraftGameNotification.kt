package org.wordandahalf.spigot.deadbyminecraft.game.notification

import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

abstract class DeadByMinecraftGameNotification(protected val text: TextComponent)
{
    /**
     * Sends the notification to the player
     */
    abstract fun send(p: Player)
}