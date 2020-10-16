package org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.scheduling.DeadByMinecraftScheduler
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Disposable
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Tickable
import java.time.Duration

data class DeadByMinecraftPlayerInterface(private val player : DeadByMinecraftPlayer) : Disposable, Tickable
{
    enum class Position
    {
        ACTION_BAR,
        TITLE,
        SUBTITLE
    }

    private val task = DeadByMinecraftScheduler.scheduleRepeating({ this.tick() }, 0, 1)

    private var title : Text? = null
    private var subtitle : Text? = null
    private var actionBar : Text? = null

    // TODO: Scoreboard private val scoreboard = List<DeadByMinecraftPlayerInterfaceText?>(15) { null }
    // TODO: BossBar

    override fun dispose()
    {
        title?.dispose()
        subtitle?.dispose()
        actionBar?.dispose()

        task.cancel()
    }

    override fun tick()
    {
        // Update the interface components
        title?.tick()
        subtitle?.tick()
        actionBar?.tick()

        // Handle the title
        val titleText = title?.build()
        val subtitleText = subtitle?.build()

        if(titleText is Component || subtitleText is Component)
        {
            // Remove the title if the text is null
            // See DeadByMinecraftPlayerInterfaceElement#build
            if(titleText !is Component && title is Text)
            {
                title = null
            }
            // Remove the subtitle if the text is null
            if(subtitleText !is Component && subtitle is Text)
            {
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
                actionBar = null
            }
        }
    }

    /**
     * Puts the provided text at the provided position.
     * Passing null for text will remove the current text at that position.
     */
    fun set(position: Position, text: Text?)
    {
        when(position)
        {
            Position.ACTION_BAR -> { actionBar?.dispose(); actionBar = text }
            Position.TITLE -> { title?.dispose(); title = text }
            Position.SUBTITLE -> { subtitle?.dispose(); subtitle = text }
        }
    }
}