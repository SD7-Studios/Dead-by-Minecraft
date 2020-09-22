package org.wordandahalf.spigot.deadbyminecraft.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftPlugin
import java.util.function.BiConsumer
import kotlin.reflect.KClass

abstract class ScriptableItemStack(private val executorClass: KClass<out Executor>)
{
    abstract class Executor : BiConsumer<PlayerInteractEvent, ItemStack>
    companion object
    {
        private val registeredExecutors = HashMap<String, Executor>()

        fun registerExecutor(executor: Executor, id: String)
        {
            DeadByMinecraftPlugin.Logger.info("Registered '${id}'!")
            registeredExecutors.putIfAbsent(id, executor)
        }

        fun getExecutor(itemStack: ItemStack) : Executor?
        {
            return registeredExecutors[
                // An item's meta should never be null!
                itemStack.itemMeta!!.persistentDataContainer[
                    NamespacedKey(DeadByMinecraftPlugin.Instance, "executor"), PersistentDataType.STRING
                ]
            ]
        }

        @Deprecated("Please, please, please stop using this!")
        fun getItem(className: String) : ItemStack?
        {
            val clazz = Class.forName(className)

            if(clazz.superclass.name == ScriptableItemStack::class.java.name)
            {
                DeadByMinecraftPlugin.Logger.info("Class is superclass!")
                return ((clazz as Class<out Executor>).constructors[0].newInstance() as ScriptableItemStack).toItemStack()
            }

            return null
        }
    }

    init
    {
        registerExecutor(executorClass.java.constructors[0].newInstance() as Executor, executorClass.java.name)
    }

    fun toItemStack() : ItemStack
    {
        val stack = ItemStack(getMaterial(), getAmount())
        // stack.itemMeta should never be null, otherwise hell must've frozen over
        val meta = stack.itemMeta!!

        meta.persistentDataContainer.set(NamespacedKey(DeadByMinecraftPlugin.Instance, "executor"), PersistentDataType.STRING, executorClass.java.name)
        meta.setDisplayName(getDisplayName())
        meta.isUnbreakable = true

        stack.itemMeta = meta

        return stack
    }

    fun getDisplayName() : String { return getMaterial().name }
    abstract fun getMaterial() : Material
    fun getAmount() : Int { return 1 }
}