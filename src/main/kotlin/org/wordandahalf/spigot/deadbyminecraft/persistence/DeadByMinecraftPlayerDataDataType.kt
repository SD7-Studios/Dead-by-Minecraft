package org.wordandahalf.spigot.deadbyminecraft.persistence

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftPlugin
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftPlayer

class DeadByMinecraftPlayerDataDataType : PersistentDataType<PersistentDataContainer, DeadByMinecraftPlayer.Data>
{
    companion object
    {
        val TYPE = DeadByMinecraftPlayerDataDataType()
    }

    override fun getPrimitiveType(): Class<PersistentDataContainer>
    {
        return PersistentDataContainer::class.java
    }

    override fun getComplexType(): Class<DeadByMinecraftPlayer.Data>
    {
        return DeadByMinecraftPlayer.Data::class.java
    }

    override fun toPrimitive(p0: DeadByMinecraftPlayer.Data, p1: PersistentDataAdapterContext): PersistentDataContainer
    {
        DeadByMinecraftPlugin.Logger.warning("DeadByMinecraftPlayerDataDataType#toPrimitive not yet implemented!")
        return p1.newPersistentDataContainer()
    }

    override fun fromPrimitive(p0: PersistentDataContainer, p1: PersistentDataAdapterContext): DeadByMinecraftPlayer.Data
    {
        DeadByMinecraftPlugin.Logger.warning("DeadByMinecraftPlayerDataDataType#fromPrimitive not yet implemented!")
        return DeadByMinecraftPlayer.Data()
    }
}