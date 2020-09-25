package org.wordandahalf.spigot.deadbyminecraft.persistence

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.wordandahalf.spigot.deadbyminecraft.game.role.DeadByMinecraftRole

class DeadByMinecraftRoleDataType : PersistentDataType<PersistentDataContainer, DeadByMinecraftRole>
{
    companion object
    {
        val TYPE = DeadByMinecraftRoleDataType()
    }

    override fun getPrimitiveType(): Class<PersistentDataContainer>
    {
        return PersistentDataContainer::class.java
    }

    override fun getComplexType(): Class<DeadByMinecraftRole>
    {
        return DeadByMinecraftRole::class.java
    }

    override fun toPrimitive(p0: DeadByMinecraftRole, p1: PersistentDataAdapterContext): PersistentDataContainer
    {
        TODO("Not yet implemented")
    }

    override fun fromPrimitive(p0: PersistentDataContainer, p1: PersistentDataAdapterContext): DeadByMinecraftRole
    {
        TODO("Not yet implemented")
    }
}