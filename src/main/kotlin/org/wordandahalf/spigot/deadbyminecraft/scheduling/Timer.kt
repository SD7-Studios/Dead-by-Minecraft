package org.wordandahalf.spigot.deadbyminecraft.scheduling

class Timer(length: Int, private val callback: () -> Unit)
{
    var secondsRemaining = length
    private set

    private val timer = Scheduler.scheduleRepeating(this::update, 0, 20L)

    private fun update() {
        if (secondsRemaining > 0)
        {
            secondsRemaining -= 1
        }
        else
        {
            callback()
            timer.cancel()
        }
    }

    override fun toString(): String
    {
        return (secondsRemaining / 60).toString() + ":" + (if(secondsRemaining < 10) "0" else "") + secondsRemaining
    }
}