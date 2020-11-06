package org.wordandahalf.spigot.deadbyminecraft.player.ui.elements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.Template
import net.kyori.adventure.util.Ticks

/**
 * Text that is removed after a provided number of milliseconds
 */
class TimedText(private val milliseconds: Long, private val text: String, private vararg val templates: Template) : Text()
{
    private var timeRemaining = milliseconds.toDouble()

    override fun build(): Component?
    {
        return if(timeRemaining > 0)
            MiniMessage.get().parse(text, *templates)
        else
            null
    }

    override fun dispose() {}

    override fun tick()
    {
        timeRemaining -= Ticks.SINGLE_TICK_DURATION_MS
    }
}