package org.wordandahalf.spigot.deadbyminecraft.game.player.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.Template

/**
 * Interface text that never disappears unless it is manually removed or replaced
 */
class StaticText(private val text : String, private vararg val templates: Template) : Text()
{
    override fun build(): Component?
    {
        return MiniMessage.get().parse(text, *templates)
    }

    override fun dispose() {}

    override fun tick() {}
}