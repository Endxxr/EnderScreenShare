
<div align="center">
    <img height="512" src="images/enderss.png" width="512" alt="EnderSS logo"/>
</div>

# EnderScreenShare by Endxxr
>Supported Minecraft Versions: 1.8.8 - 1.20.1

Powerful and lightweight screenshare plugin for Spigot, Bungeecord and Velocity!

The idea of this plugin was born from the need to have a free screenshare plugin that works on every platform and at the same time be powerful and simple to use. 


For report a bug or support: 

> Discord Server: https://discord.gg/PhMbS3jtAJ 
>
> Discord: @endxxr
> 
> Telegram: @pellicani

# Features
✓ Full of commands and customizable messages

✓ Title, Buttons and notifications

✓ Reports

✓ User friendly 

✓ Lightweight

✓ PlaceholderAPI support

✓ Automatic ban on quit (with LiteBans support)

✓ Command Blocker when frozen 

✓ Automatic teleports at the start and end of the control

✓ Protections for the screenshare world and the player

✓ Private chat between the player and the staffer with LuckPerms support

✓ Available in both English and Italian language (check lang folder)

## Soft-Dependencies
Other plugins that are not required but are recommended to use with EnderSS

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - For placeholders
- [LiteBans](https://www.spigotmc.org/resources/litebans.3715/) - For automatic ban on quit
- [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/) - For private chat between the player and the staffer

## Getting Started

The plugin uses [bStats](https://bStats.org) to collect anonymous data about the plugin's usage. You can disable it in ``plugins/bStats/config.yml``.

### Spigot (Backend)
 
In this version of the plugin, you can choose whether to use the plugin in proxy mode or not. 

When ``proxy-mode`` is set to ``true`` in ``plugins/EnderSS/spigot.yml``, the plugin won't be able to start controls but only use the features in ``plugins/EnderSS/spigot.yml``. You have to install the plugin on the proxy in order to start controls.

When ``proxy-mode`` is set to ``false`` (default) in ``plugins/EnderSS/spigot.yml``, you will be able to start controls only using this plugin but only towards people connected to the same server where the plugin is installed.

#### Requirements

- Java 11 or higher
- Spigot 1.8.8 or higher

#### Installation
- Download the plugin from Spigot or GitHub releases 
- Put the ```.jar``` file in the plugin folder of your Spigot server 
- Start or restart your server
- Enjoy!

### Bungeecord / Velocity (Proxy)

You can pair this plugin with its Spigot version to have a complete experience.

#### Requirements

- Java 11 or higher
- Velocity 3.x.x or any recent Bungeecord version
- 2 or more Spigot server connected to the proxy

#### Installation
- Download the plugin from Spigot or GitHub releases
- Put the ```.jar``` file in the plugin folder of your Bungeecord/Velocity server and eventually Spigot server
- Start or restart the proxy and eventually the Spigot server
- Enjoy!

## Commands
ss | screenshare | control | freeze
````
/ss <player> - enderss.ss - Start a control on a player
````
enderss | enderscreenshare | ssplugin | endersettings [all the commands on Spigot have an "s" prefix]
````
/enderss [alerts|help|reload|setspawm|version] - enderss.settings - Some useful commands, I think
````
clean | legit
````
/clean <player> - enderss.clean - Terminate the control on a player
````
Blatant
````
/blatant <player> - enderss.blatant - Directly ban a player for blatant cheating
````
Report
````
/report <player> <reason> - enderss.report - Report a player 
````

## Permissions

On spigot, giving the permission ``enderss.staff`` and ``enderss.admin`` will automatically give all the required permission to perform a screenshare and administrate the server and give them the role of staffer.

On the other platforms, in most cases, you will have to give all the permissions manually.

Remember that all staffers need the permission ``enderss.staff`` !

#### Permission inherited from ``enderss.staff``
| Permission                                 |                              Description                               |
|:-------------------------------------------|:----------------------------------------------------------------------:|
| enderss.ss                                 |                      Start a control on a player                       |
| enderss.settings                           |           Access to /enderss command but not its subcommands           |
| enderss.settings.alerts                    |                 Toggle notifications regarding EnderSS                 |
| enderss.settings.help                      |       Access to the help subcommand, show all plugin's commands        |
| enderss.clean                              |                    Terminate a control on a player                     |
| enderss.blatant                            |                       Immediately bans a player                        |
#### Permission inherited from ``enderss.admin``
| Permission                |                              Description                               |
|:--------------------------|:----------------------------------------------------------------------:|
| enderss.staff             | Gives automatically all the needed permission to perform a screenshare |
| enderss.settings.reload   |                   Reload the plugin's configuration                    |
| enderss.settings.setspawn |                Set the spawn on the screenshare server                 |
| enderss.settings.version  |                       Show the plugin's version                        |
| enderss.protections       |                 Bypass all the protections of EnderSS                  |
| enderss.bybass            |                        Bypass being controlled                         |
#### All Permissions
| Permission                                 |                              Description                               |
|:-------------------------------------------|:----------------------------------------------------------------------:|
| enderss.staff                              | Gives automatically all the needed permission to perform a screenshare |
| enderss.admin                              |  Gives automatically all needed permission to administrate the server  |
| enderss.ss                                 |                      Start a control on a player                       |
| enderss.settings                           |           Access to /enderss command but not its subcommands           |
| enderss.settings.alerts                    |                 Toggle notifications regarding EnderSS                 |
| enderss.settings.help                      |       Access to the help subcommand, show all plugin's commands        |
| enderss.settings.reload                    |                   Reload the plugin's configuration                    |
| enderss.settings.setspawm                  |                Set the spawn on the screenshare server                 |
| enderss.settings.version                   |                       Show the plugin's version                        |
| enderss.clean                              |                    Terminate a control on a player                     |
| enderss.blatant                            |                       Immediately bans a player                        |
| enderss.report                             |                            Report a player                             |
| enderss.protections                        |                 Bypass all the protections of EnderSS                  |
| enderss.protections.world                  |                 Bypass the world protection of EnderSS                 |
| enderss.protections.world.build-break      |           Bypass the build break world protection of EnderSS           |
| enderss.protections.world.pvp              |               Bypass the pvp world protection of EnderSS               |
| enderss.protections.world.invulnerability  |         Bypass the invulnerability world protection of EnderSS         |
| enderss.protections.world.hunger           |             Bypass the hunger world protection of EnderSS              |
| enderss.protections.world.void             |              Bypass the void world protection of EnderSS               |
| enderss.protections.player.pvp             |              Bypass the pvp player protection of EnderSS               |
| enderss.protections.player.pvp             |              Bypass the pvp player protection of EnderSS               |
| enderss.protections.player.build-break     |          Bypass the build break player protection of EnderSS           |
| enderss.protections.player.pick-drop-items |        Bypass the pick drop items player protection of EnderSS         |
| enderss.protections.player.hunger          |             Bypass the hunger player protection of EnderSS             |
| enderss.protections.player.damage          |             Bypass the damage player protection of EnderSS             |
| enderss.protections.player.remove-effects  |         Bypass the remove effects player protection of EnderSS         |
| enderss.protections.player.adventure-mode  |         Bypass the adventure mode player protection of EnderSS         |


## Placeholders

The placeholders are only available on the Spigot version of the plugin. They're available as soon as you install the plugin 

| Placeholder           | Description                                               |
|:----------------------|:----------------------------------------------------------|
| %enderss_staff%       | Return the number of staffer who's controlling the player |
| %enderss_suspect%     | Return the name of the player who's being controlled      |
| %enderss_staffer%     | Return if the player is staffer or not                    |
| %enderss_controlling% | Return if the staffer is controlling someone              |
| %enderss_frozen%      | Return if the player is being controlled                  |

## Build
To build the plugin, you need to have Java 11 or higher and Maven installed on your computer.
Run the following command in the root directory of the project:
````
mvn clean package
````
The .jar file will be in the ``\universal\target`` folder.

## Api
EnderSS has an API that allows you to interact with the plugin. The jar of the API is downloadable in the GitHub Releases.  
Make sure to add the plugin as a dependency in your project.

### Get the API
````
EnderSS api = EnderSSProvider.getApi();
````