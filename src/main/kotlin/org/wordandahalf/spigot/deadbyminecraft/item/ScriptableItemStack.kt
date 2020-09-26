package org.wordandahalf.spigot.deadbyminecraft.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import java.util.function.BiConsumer

/**
 * An ItemStack that will execute a snippet of code when used (right-clicked or left-clicked)
 */
abstract class ScriptableItemStack(private val executor: Executor)
{
    abstract class Executor : BiConsumer<PlayerInteractEvent, ItemStack>
    companion object
    {
        private val registeredExecutors = HashMap<String, Executor>()

        fun isScriptableItemStack(itemStack: ItemStack) : Boolean
        {
            return itemStack.itemMeta!!.persistentDataContainer.has(NamespacedKey(DeadByMinecraft.instance, "executor"), PersistentDataType.STRING)
        }

        fun getExecutor(itemStack: ItemStack) : Executor?
        {
            return registeredExecutors[
                // An item's meta should never be null!
                itemStack.itemMeta!!.persistentDataContainer[
                    NamespacedKey(DeadByMinecraft.instance, "executor"), PersistentDataType.STRING
                ]
            ]
        }

        @Deprecated("Please, please, please! Don't use this!")
        fun getItem(className: String) : ItemStack?
        {
            val clazz = Class.forName(className)

            if(clazz.superclass.name == ScriptableItemStack::class.java.name)
            {
                return ((clazz as Class<out Executor>).constructors[0].newInstance() as ScriptableItemStack).toItemStack()
            }

            return null
        }
    }

    init
    {
        registeredExecutors.putIfAbsent(this.getID(), executor)
    }

    fun toItemStack() : ItemStack
    {
        val stack = ItemStack(getMaterial(), getAmount())
        // stack.itemMeta should never be null, otherwise hell must've frozen over
        val meta = stack.itemMeta!!

        // Store the executor's name in the item's meta for retrieval upon use.
        meta.persistentDataContainer.set(NamespacedKey(DeadByMinecraft.instance, "executor"), PersistentDataType.STRING, this.getID())

        // Set the display name, if any
        meta.setDisplayName(getDisplayName())

        // Item should not be breakable
        meta.isUnbreakable = true

        stack.itemMeta = meta

        return stack
    }

    fun getDisplayName() : String? { return null }
    abstract fun getMaterial() : Material
    fun getAmount() : Int { return 1 }
    fun getID() : String { return this.toString() + this.executor.toString() }
}