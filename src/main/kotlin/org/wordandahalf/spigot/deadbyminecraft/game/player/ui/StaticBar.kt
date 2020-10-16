package org.wordandahalf.spigot.deadbyminecraft.game.player.ui

import net.kyori.adventure.bossbar.BossBar

class StaticBar(private val text: Text, private val color: BossBar.Color, private val overlay: BossBar.Overlay, private vararg val flags: BossBar.Flag) : Bar()
{
    override fun build(): BossBar?
    {
        return BossBar.bossBar(text.build() ?: return null, 1f, color, overlay, mutableSetOf(*flags))
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