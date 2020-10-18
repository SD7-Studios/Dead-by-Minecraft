# Dead by Minecraft
A remake of the popular horror game *Dead by Daylight* in *Minecraft* using Spigot 1.16.2.

### Requirements:
The plugin's dependencies are all managed through Maven; the server requires the following:
- Paper 1.16.2
- Citizens2
- SlimeWorldManager
- ProtocolLib
- The resource pack
- Correct configuration

The plugin allows for completely custom worlds, an example lobby and game world will be available for download. Worlds must be in Slime format and have a valid configuration in its proper place.

Lobby worlds should have a config in `<server root>/plugins/dead-by-minecraft/worlds/lobby/<name of lobby world>.json`. The name of the config *must* correspond with the name of the world. Configs for game worlds are to be created similarly, except in `<server root>/plugins/dead-by-minecraft/worlds/game/`.

Documentation for configurations will be written once the plugin is feature complete.

Lobby and game worlds are randomly chosen when a game is created.