package org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.bar

import net.kyori.adventure.bossbar.BossBar
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.StaticText

class LobbyInfoBar(private val player : DeadByMinecraftPlayer, private val stateInfo: (DeadByMinecraftPlayer) -> Pair<String, Float>) : Bar()
{
    var showInfo = false
    var tick = 2 * 20

    private val infoBar  = StaticBar(StaticText("<red>DeadByMinecraft Lobby #${player.data.gameID}"), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS, BossBar.Flag.CREATE_WORLD_FOG, BossBar.Flag.DARKEN_SCREEN)
    private fun stateBar() : StaticBar
    {
        val data = stateInfo(player)
        return StaticBar(StaticText(data.first), data.second, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS, BossBar.Flag.CREATE_WORLD_FOG, BossBar.Flag.DARKEN_SCREEN)
    }

    override fun build(): BossBar?
    {
        return if(showInfo) {
            infoBar.build()
        } else {
            stateBar().build()
        }
    }

    override fun dispose()
    {
        infoBar.dispose()
    }

    override fun tick()
    {
        if(tick > 0)
        {
            tick--
        }
        else
        {
            tick = 2 * 20
            showInfo = !showInfo
        }
    }
}