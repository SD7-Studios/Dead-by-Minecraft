package org.wordandahalf.spigot.deadbyminecraft.game.notification

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftScheduler
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * Creates a notification that gradually reveals the message
 * @param text the formatted message
 * @param @time the time for it to reveal in milliseconds
 */
class RevealingActionBarNotification(text: TextComponent, private val time: Long) : ActionBarNotification(text)
{
    /**
     * A list of Triples, each containing a character of the message, whether it has been revealed, and its style.
     */
    private var message = ArrayList<Triple<Char, Boolean, Style>>()

    init
    {
        text.children().forEach {
            (it as TextComponent).content().forEach { c ->
                message.add(Triple(c, false, it.style()))
            }
        }
    }

    private class RevealingActionBarNotificationWorker(private val player: Player, private val notif: RevealingActionBarNotification) : Runnable
    {
        override fun run()
        {
            // If there are no more characters left to reveal
            if(notif.message.none { !it.second })
            {
                // Return, we're done!
                return
            }

            // Get a random character that hasn't been revealed
            var randomIndex = Random.nextInt(notif.message.size)
            while(notif.message[randomIndex].second)
                randomIndex = Random.nextInt(notif.message.size)

            // Reveal it
            val ref = notif.message[randomIndex]
            notif.message[randomIndex] = Triple(ref.first, true, ref.third)

            // Get the message
            val message = Component.text()
            for(v in notif.message)
            {
                if(!v.second)
                    message.append(Component.text("&", v.third.decorate(TextDecoration.OBFUSCATED)))
                else
                    message.append(Component.text(v.first, v.third))
            }

            // Send it
            DeadByMinecraft.Audience.player(player).sendActionBar(message)

            val timeInMills = notif.time.toDouble() / notif.message.size.toDouble()
            // 1 tick = 50 milliseconds
            // milli * 1 tick / 50 milli = tick
            val timeInTicks = timeInMills / 50.0
            DeadByMinecraftScheduler.runDelayed(RevealingActionBarNotificationWorker(player, notif), timeInTicks.roundToLong())
        }
    }

    override fun send(p: Player)
    {
        DeadByMinecraftScheduler.run(RevealingActionBarNotificationWorker(p, this))
    }
}