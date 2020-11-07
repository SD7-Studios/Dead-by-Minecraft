package org.wordandahalf.spigot.deadbyminecraft.player.ui

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.Bar
import org.wordandahalf.spigot.deadbyminecraft.player.ui.elements.Text
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Scheduler
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Disposable
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Tickable
import java.time.Duration

data class PlayerInterface(private val player : DeadByMinecraftPlayer) : Disposable, Tickable
{
    private val task = Scheduler.scheduleRepeating({ this.tick() }, 0, 1)

    private var bossBar: Bar? = null
    private var previousBossBar : BossBar? = null

    private var title : Text? = null
    private var subtitle : Text? = null
    private var actionBar : Text? = null

    // TODO: Scoreboard private val scoreboard = List<DeadByMinecraftPlayerInterfaceText?>(15) { null }

    override fun dispose()
    {
        bossBar?.dispose()
        title?.dispose()
        subtitle?.dispose()
        actionBar?.dispose()

        task.cancel()
    }

    override fun tick()
    {
        // Update the interface components
        bossBar?.dispose()
        title?.tick()
        subtitle?.tick()
        actionBar?.tick()

        // Handle the bossbar
        val bar = bossBar?.build()
        if(bar is BossBar)
        {
            if(previousBossBar is BossBar)
            {
                DeadByMinecraft.Audience.player(player.bukkit).hideBossBar(previousBossBar!!)
            }

            DeadByMinecraft.Audience.player(player.bukkit).showBossBar(bar)
            previousBossBar = bar
        }
        else
        {
            // If the bossBar isn't null and the build bar was, it is requesting to be removed
            if(bossBar is Bar)
            {
                bossBar?.dispose()
                bossBar = null
            }
            else
            {
                // If it is null and the previous value wasn't, we need to hide the bar
                if(previousBossBar != null)
                {
                    DeadByMinecraft.Audience.player(player.bukkit).hideBossBar(previousBossBar!!)
                }
            }
        }

        // Handle the title
        val titleText = title?.build()
        val subtitleText = subtitle?.build()

        if(titleText is Component || subtitleText is Component)
        {
            // Remove the title if the text is null
            // See DeadByMinecraftPlayerInterfaceElement#build
            if(titleText !is Component && title is Text)
            {
                title?.dispose()
                title = null
            }
            // Remove the subtitle if the text is null
            if(subtitleText !is Component && subtitle is Text)
            {
                subtitle?.dispose()
                subtitle = null
            }

            // Send the title to the player!
            // The fade-in is disabled because it mucks with animated titles.
            DeadByMinecraft.Audience.player(player.bukkit)
                .showTitle(
                    Title.title(
                        titleText ?: Component.empty(),
                        subtitleText ?: Component.empty(),
                        Title.Times.of(Duration.ZERO, Ticks.duration(70), Ticks.duration(20))
                    )
                )
        }

        // Handle the actionbar
        val actionBarText = actionBar?.build()
        if(actionBarText is Component)
        {
            DeadByMinecraft.Audience.player(player.bukkit).sendActionBar(actionBarText)
        }
        else
        {
            // Remove the actionbar if the text is null
            if(actionBar is Text)
            {
                actionBar?.dispose()
                actionBar = null
            }
        }
    }

    fun bossBar(bar: Bar?) { bossBar?.dispose(); bossBar = bar}

    fun actionBar(text: Text?) { actionBar?.dispose(); actionBar = text }
    fun title(text: Text?) { title?.dispose(); title = text }
    fun subtitle(text: Text?) { subtitle?.dispose(); subtitle = text }
}