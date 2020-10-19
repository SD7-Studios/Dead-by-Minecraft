package org.wordandahalf.spigot.deadbyminecraft.scheduling

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft

object Scheduler
{
    /**
     * Returns a task that will run on the next server tick.
     */
    fun run(task: () -> Unit) : BukkitTask
    {
        return Bukkit.getScheduler().runTask(DeadByMinecraft.Instance, task)
    }

    /**
     * Returns a task that will run on the next server tick.
     */
    fun run(task: Runnable) : BukkitTask
    {
        return Bukkit.getScheduler().runTask(DeadByMinecraft.Instance, task)
    }

    /**
     * Returns a task that will run asynchronously.
     * Asynchronous tasks should never access any API in Bukkit.
     */
    fun runAsync(task: () -> Unit) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskAsynchronously(DeadByMinecraft.Instance, task)
    }

    /**
     * Returns a task that will run asynchronously.
     * Asynchronous tasks should never access any API in Bukkit.
     */
    fun runAsync(task: Runnable) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskAsynchronously(DeadByMinecraft.Instance, task)
    }

    /**
     * Returns a task that will run after the specified number of server ticks.
     */
    fun runDelayed(task: () -> Unit, delay: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskLater(DeadByMinecraft.Instance, task, delay)
    }

    /**
     * Returns a task that will run after the specified number of server ticks.
     */
    fun runDelayed(task: Runnable, delay: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskLater(DeadByMinecraft.Instance, task, delay)
    }

    /**
     * Returns a task that will run asynchronously after the specified number of server ticks.
     * Asynchronous tasks should never access any API in Bukkit.
     */
    fun runDelayedAsync(task: () -> Unit, delay: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(DeadByMinecraft.Instance, task, delay)
    }

    /**
     * Returns a task that will run asynchronously after the specified number of server ticks.
     * Asynchronous tasks should never access any API in Bukkit.
     */
    fun runDelayedAsync(task: Runnable, delay: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(DeadByMinecraft.Instance, task, delay)
    }

    /**
     * Returns a task that will run until it is cancelled, starting delay ticks after scheduled, repeating every period ticks.
     */
    fun scheduleRepeating(task: () -> Unit, delay: Long, period: Long) : BukkitTask
    {
        DeadByMinecraft.Logger.info("Scheduled new task (delay=$delay,period=$period)")

        return Bukkit.getScheduler().runTaskTimer(DeadByMinecraft.Instance, task, delay, period)
    }

    /**
     * Returns a task that will run until it is cancelled, starting delay ticks after scheduled, repeating every period ticks.
     */
    fun scheduleRepeating(task: Runnable, delay: Long, period: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskTimer(DeadByMinecraft.Instance, task, delay, period)
    }

    /**
     * Returns a task that will run asynchronously until it is cancelled, starting delay ticks after scheduled, repeating every period ticks.
     * Asynchronous tasks should never access any API in Bukkit.
     */
    fun scheduleRepeatingAsync(task: () -> Unit, delay: Long, period: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(DeadByMinecraft.Instance, task, delay, period)
    }

    /**
     * Returns a task that will run asynchronously until it is cancelled, starting delay ticks after scheduled, repeating every period ticks.
     * Asynchronous tasks should never access any API in Bukkit.
     */
    fun scheduleRepeatingAsync(task: Runnable, delay: Long, period: Long) : BukkitTask
    {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(DeadByMinecraft.Instance, task, delay, period)
    }
}