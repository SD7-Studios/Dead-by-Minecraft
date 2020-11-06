package org.wordandahalf.spigot.deadbyminecraft.config

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.DefaultLobbyBehavior

@ConfigSerializable
class LobbyWorldConfig
{
    @Setting
    val behavior : String = DefaultLobbyBehavior::class.java.simpleName

    @Setting
    val time = 18000L

    @Setting
    val spawn = SpawnSettings()

    @Setting(value = "killer-position")
    val killerPosition = SpawnSettings()

    @Setting
    val npcs = NpcSettings()

    companion object
    {
        private val MAPPER = ObjectMapper.forClass(LobbyWorldConfig::class.java)

        fun loadFrom(node: ConfigurationNode): LobbyWorldConfig {
            return MAPPER.bindToNew().populate(node)
        }
    }

    fun saveTo(node: ConfigurationNode) {
        MAPPER.bind(this).serialize(node)
    }

    @ConfigSerializable
    class SpawnSettings
    {
        @Setting
        val location = arrayOf(0.0, 0.0, 0.0)

        @Setting
        val rotation = arrayOf(0f, 0f)
    }

    @ConfigSerializable
    class NpcSettings
    {
        @Setting
        val enabled = true

        @Setting
        val locations = listOf(arrayOf(0.0, 0.0, 0.0))
    }
}