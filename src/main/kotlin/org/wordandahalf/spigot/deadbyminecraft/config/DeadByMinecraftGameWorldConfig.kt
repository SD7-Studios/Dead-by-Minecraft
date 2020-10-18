package org.wordandahalf.spigot.deadbyminecraft.config

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class DeadByMinecraftGameWorldConfig
{
    @Setting
    var time = 18000L

    @Setting
    var survivor = SurvivorSettings()

    @Setting
    var killer = KillerSettings()

    companion object
    {
        private val MAPPER = ObjectMapper.forClass(DeadByMinecraftGameWorldConfig::class.java)

        fun loadFrom(node: ConfigurationNode): DeadByMinecraftGameWorldConfig {
            return MAPPER.bindToNew().populate(node)
        }
    }

    fun saveTo(node: ConfigurationNode) {
        MAPPER.bind(this).serialize(node)
    }

    @ConfigSerializable
    class SurvivorSettings
    {
        @Setting(value = "spawn-locations")
        val spawnLocations = listOf(arrayOf(0.0, 0.0, 0.0))

        @Setting(value = "spawn-rotations")
        val spawnRotations = listOf(arrayOf(0.0, 0.0))
    }

    @ConfigSerializable
    class KillerSettings
    {
        @Setting(value = "spawn-locations")
        val spawnLocations = listOf(arrayOf(0.0, 0.0, 0.0))

        @Setting(value = "spawn-rotations")
        val spawnRotations = listOf(arrayOf(0.0, 0.0))
    }
}