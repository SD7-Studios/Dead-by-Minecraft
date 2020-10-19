package org.wordandahalf.spigot.deadbyminecraft.config

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.gson.GsonConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import org.bukkit.configuration.InvalidConfigurationException
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import java.io.File

@ConfigSerializable
class Config
{
    @Setting(value = "min-players")
    var minPlayers = 4

    @Setting(value = "max-players")
    val maxPlayers = 10

    @Setting(value = "killer-ratio")
    val killerRatio = 0.25

    @Setting(value = "default-world-name")
    val defaultWorldName = "world"

    private fun saveTo(node: ConfigurationNode)
    {
        MAPPER.bind(this).serialize(node)
    }

    object Worlds
    {
        val Lobby = HashMap<String, LobbyWorldConfig>()
        val Game = HashMap<String, GameWorldConfig>()
    }

    companion object
    {
        private val MAIN_CONFIG_FILE                = File(DeadByMinecraft.Instance.dataFolder, "config.json")
        private val LOBBY_WORLD_CONFIG_FOLDER       = File(DeadByMinecraft.Instance.dataFolder, "worlds/lobby/")
        private val GAME_WORLD_CONFIG_FOLDER        = File(DeadByMinecraft.Instance.dataFolder, "worlds/game/")
        private const val DEFAULT_CONFIG_FILE_NAME  = "example.json.ignore"

        private val MAIN_CONFIG_LOADER = GsonConfigurationLoader.builder().setFile(MAIN_CONFIG_FILE).build()
        private val CONFIG_OPTIONS = ConfigurationOptions.defaults().withSerializers(TypeSerializerCollection.defaults().newChild())

        private val MAPPER = ObjectMapper.forClass(Config::class.java)

        lateinit var Main: Config

        private fun loadFrom(node: ConfigurationNode) : Config
        {
            return MAPPER.bindToNew().populate(node)
        }

        fun load()
        {
            // Ensure the data folder exists

            if(!DeadByMinecraft.Instance.dataFolder.exists())
                DeadByMinecraft.Instance.dataFolder.mkdirs()

            loadMainConfig()

            // Load world configs

            if(!LOBBY_WORLD_CONFIG_FOLDER.exists())
                LOBBY_WORLD_CONFIG_FOLDER.mkdirs()

            loadLobbyWorldConfigs()

            if(!GAME_WORLD_CONFIG_FOLDER.exists())
                GAME_WORLD_CONFIG_FOLDER.mkdirs()

            loadGameWorldConfigs()
            createExampleConfigurations()
        }

        private fun loadMainConfig()
        {
            val configRoot = MAIN_CONFIG_LOADER.load(CONFIG_OPTIONS)

            try
            {
                Main = loadFrom(configRoot)
            }
            catch (e: Exception)
            {
                throw InvalidConfigurationException(e.message)
            }

            if(!MAIN_CONFIG_FILE.exists())
            {
                val root = ConfigurationNode.root(CONFIG_OPTIONS.withHeader("Main configuration"))
                Main.saveTo(root)
                MAIN_CONFIG_LOADER.save(root)
            }
        }

        private fun loadLobbyWorldConfigs()
        {
            if(!LOBBY_WORLD_CONFIG_FOLDER.exists())
                LOBBY_WORLD_CONFIG_FOLDER.mkdirs()

            val configs = LOBBY_WORLD_CONFIG_FOLDER.listFiles { _, name -> name.endsWith("json") } ?: return

            configs.forEach {
                DeadByMinecraft.Logger.info("Loading config for lobby world '${it.nameWithoutExtension}'.")
                val loader = GsonConfigurationLoader.builder().setFile(it).build()
                val configRoot = loader.load(CONFIG_OPTIONS)

                try
                {
                    Worlds.Lobby[it.nameWithoutExtension] = LobbyWorldConfig.loadFrom(configRoot)
                }
                catch (e: Exception)
                {
                    DeadByMinecraft.Logger.warning("Error loading configuration: ${e.message}")
                }
            }
        }

        private fun loadGameWorldConfigs()
        {
            if(!GAME_WORLD_CONFIG_FOLDER.exists())
                GAME_WORLD_CONFIG_FOLDER.mkdirs()

            val configs = GAME_WORLD_CONFIG_FOLDER.listFiles { _, name -> name.endsWith("json") } ?: return

            configs.forEach {
                DeadByMinecraft.Logger.info("Loading config for game world '${it.nameWithoutExtension}'.")
                val loader = GsonConfigurationLoader.builder().setFile(it).build()
                val configRoot = loader.load(CONFIG_OPTIONS)

                try
                {
                    Worlds.Game[it.nameWithoutExtension] = GameWorldConfig.loadFrom(configRoot)
                }
                catch (e: Exception)
                {
                    DeadByMinecraft.Logger.warning("Error loading configuration: ${e.message}")
                }
            }
        }

        private fun createExampleConfigurations()
        {
            val exampleLobbyWorldConfig = File(LOBBY_WORLD_CONFIG_FOLDER, DEFAULT_CONFIG_FILE_NAME)
            if(!exampleLobbyWorldConfig.exists())
            {
                DeadByMinecraft.Logger.info("Creating example lobby world config at '${exampleLobbyWorldConfig.path}'")
                val root = ConfigurationNode.root(CONFIG_OPTIONS.withHeader("Example lobby world configuration"))
                val loader = GsonConfigurationLoader.builder().setFile(exampleLobbyWorldConfig).build()
                LobbyWorldConfig().saveTo(root)
                loader.save(root)
            }

            val exampleGameWorldConfig = File(GAME_WORLD_CONFIG_FOLDER, DEFAULT_CONFIG_FILE_NAME)
            if(!exampleGameWorldConfig.exists())
            {
                DeadByMinecraft.Logger.info("Creating example game world config at '${exampleGameWorldConfig.path}'")
                val root = ConfigurationNode.root(CONFIG_OPTIONS.withHeader("Example game world configuration"))
                val loader = GsonConfigurationLoader.builder().setFile(exampleGameWorldConfig).build()
                GameWorldConfig().saveTo(root)
                loader.save(root)
            }
        }
    }
}