package org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`.animations

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.Template
import net.kyori.adventure.util.Ticks
import org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`.Text
import kotlin.math.floor
import kotlin.random.Random

/**\
 * A cool revealing animation!
 */
class RevealingText(milliseconds : Long, text: String, vararg template: Template) : Text()
{
    /**
     * A list of Triples, each containing a character of the message, whether it has been revealed, and its style.
     */
    private var message = ArrayList<Triple<Char, Boolean, Style>>()

    // The remaining number of characters to reveal.
    private var charactersRemaining = 0

    // The remaining number of ticks before the next character is revealed.
    private var ticksUntilNextCharacter = 0
    private var ticksPerCharacter = 0
    // If the length requires multiple characters to be revealed per tick, this flag will be set.
    private var multipleCharactersPerTick = false
    private var charactersPerTick = 0

    init
    {
        MiniMessage.get().parse(text, *template).children().forEach {
            (it as TextComponent).content().forEach { c ->
                message.add(Triple(c, false, it.style()))
            }
        }

        // Initialize charactersRemaining with the length of the parsed message
        charactersRemaining = message.size

        // Calculate the number of ticks until the next character is revealed by dividing the length (in milliseconds) by the number of milliseconds
        // per tick, giving the length in ticks.
        // This is then divided by the number of characters and rounded down to the nearest whole number.
        ticksPerCharacter = floor((milliseconds.toDouble() / Ticks.SINGLE_TICK_DURATION_MS.toDouble()) / message.size.toDouble()).toInt()
        ticksUntilNextCharacter = ticksPerCharacter

        if(ticksUntilNextCharacter == 0)
        {
            multipleCharactersPerTick = true
            charactersPerTick = floor(message.size.toDouble() / (milliseconds.toDouble() / Ticks.SINGLE_TICK_DURATION_MS.toDouble())).toInt()
        }
    }

    override fun build(): Component?
    {
        // Request to be removed when there are no character to remove and the last delay has ended.
        if(charactersRemaining == 0 && ticksUntilNextCharacter == 0)
            return null

        val component = Component.text()
        for(v in message)
        {
            if(!v.second)
            {
                component.append(Component.text('|', v.third.decorate(TextDecoration.OBFUSCATED)))
            }
            else
            {
                component.append(Component.text(if(v.first.isLetterOrDigit()) v.first + 65248 else v.first, v.third))
            }
        }

        return component.build()
    }

    override fun dispose() {}

    override fun tick()
    {
        // ticksUntilNextCharacter is ignored if this flag is set;
        // it means that the time delay needed between each character is less than one tick, so ticksUntilNextCharacter is zero.
        if(multipleCharactersPerTick)
        {
            var charactersToReveal = charactersPerTick

            // Since it is unlikely that the number of characters will be evenly divisible by number of characters
            // revealed per tick, the remainder needs to be accounted for.
            if(charactersPerTick > charactersRemaining)
                charactersToReveal = charactersRemaining

            // Reveal the needed number of characters
            for(i in 0..charactersToReveal)
            {
                revealCharacter()
            }

            // If there are no characters left, the last character needs time to be displayed,
            // so the current algorithm is abused to give at least one tick of delay.
            // This makes the animation less accurate chronologically, but that doesn't really matter
            // since it just a cool looking animation.
            if(charactersRemaining == 0)
            {
                multipleCharactersPerTick = false
                ticksUntilNextCharacter = 1
            }
        }
        else
        {
            if(ticksUntilNextCharacter == 0)
            {
                // See comment on line 101--this is to allow for a delay after the last character when
                // the delay per character is less than one tick.
                if(charactersRemaining > 0)
                {
                    revealCharacter()

                    ticksUntilNextCharacter = ticksPerCharacter
                }
            }
            else
            {
                ticksUntilNextCharacter--
            }
        }
    }

    private fun revealCharacter()
    {
        // Get a random character that hasn't been revealed
        var randomIndex = Random.nextInt(message.size)

        // Ensure that the next while loop can never hang
        if(message.none { !it.second })
            return

        while(message[randomIndex].second)
            randomIndex = Random.nextInt(message.size)

        // Reveal it
        val ref = message[randomIndex]
        message[randomIndex] = Triple(ref.first, true, ref.third)

        // Decrement the number of characters remaining
        charactersRemaining--
    }
}