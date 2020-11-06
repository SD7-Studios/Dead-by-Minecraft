package org.wordandahalf.spigot.deadbyminecraft.actions

import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.Behavior
import java.lang.reflect.Method

object Actions
{
    private val actionHandlers : HashMap<Behavior, HashMap<Class<out Action>, Method>> = HashMap()

    fun register(behavior: Behavior)
    {
        val annotatedFunctions = HashMap<Class<out Action>, Method>()
        behavior::class.java.methods.forEach {
            if(it.getAnnotation(ActionHandler::class.java) != null)
            {
                if (it.parameterCount == 1)
                {
                    if (it.parameterTypes[0].superclass == Action::class.java)
                    {
                        annotatedFunctions[it.parameterTypes[0] as Class<out Action>] = it
                    }
                }
            }
        }

        actionHandlers[behavior] = annotatedFunctions
    }

    fun deregister(behavior: Behavior)
    {
        actionHandlers.remove(behavior)
    }

    fun submit(action: Action)
    {
        if(DeadByMinecraft.DEBUG)
            DeadByMinecraft.Logger.info("Submitting action ${action::class.java.simpleName} for world ${action.world.name}!")

        val handlers = actionHandlers.filter { it.key.world == action.world }

        if(handlers.size == 1)
        {
            handlers.values.first()[action::class.java]?.invoke(handlers.keys.first(), action)
        }
    }
}