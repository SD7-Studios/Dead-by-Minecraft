package org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.bar

import net.kyori.adventure.bossbar.BossBar
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.Text

class StaticBar(private val text: Text, private val progress: Float, private val color: BossBar.Color, private val overlay: BossBar.Overlay, private vararg val flags: BossBar.Flag) : Bar()
{
    override fun build(): BossBar?
    {
        return BossBar.bossBar(text.build() ?: return null, progress, color, overlay, mutableSetOf(*flags))
    }

    override fun dispose()
    {
        text.dispose()
    }

    override fun tick()
    {
        text.tick()
    }
}