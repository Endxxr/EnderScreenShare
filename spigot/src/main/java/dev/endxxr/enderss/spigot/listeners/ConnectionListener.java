package dev.endxxr.enderss.spigot.listeners;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.spigot.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import litebans.api.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConnectionListener implements Listener {

    private final EnderSS api;

    public ConnectionListener() {
        this.api = EnderSSProvider.getApi();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        api.getPlayersManager().registerPlayer(player.getUniqueId()); //Register player
        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(ChatUtils.format("&8[&5&ls&d&lEnder&5&lSS&8]&f New version available!"));
        }
    }


    /*
        NOTE: this method is fired even if the player is kicked for a ban. If we want to use a ban-on-quit feature, we need to check if the player is banned before executing the commands.
        We can use LiteBans API for this.
     */

    @EventHandler
    public void onProxyQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
        api.getPlayersManager().unregisterPlayer(ssPlayer); //Remove player from the list

       /*
            SUSPECT QUITS
        */

        if (SpigotConfig.PROXY_MODE.getBoolean()) return;

        if (ssPlayer.isFrozen()) { // If the player controlled quits
            SsPlayer ssStaff = ssPlayer.getStaffer();
            Player staff = Bukkit.getPlayer(ssStaff.getUUID());
            ssStaff.setControlled(null);

            //Send messages
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("enderss.staff") || api.getPlayersManager().getPlayer(onlinePlayer.getUniqueId()).hasAlerts()) {
                    onlinePlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_INFO_PLAYER_QUIT.getMessage(), "%SUSPECT%", player.getName(), "%STAFF%", staff.getName()));
                }
            }

            //Ban On Quit
            List<String> banOnQuitCommands = GlobalConfig.BAN_ON_QUIT_COMMANDS.getStringList();
            if (!banOnQuitCommands.isEmpty()) {

                if (api.getPlugin().isLiteBansPresent() && GlobalConfig.BAN_ON_QUIT_PREVENT_DOUBLE_BAN.getBoolean()) {
                    if (CompletableFuture.supplyAsync(() -> Database.get().isPlayerBanned(player.getUniqueId(), null)).join()) {
                        return;
                    }
                }

                for (String command : banOnQuitCommands) {
                    command = command.replace("%SUSPECT%", player.getName());
                    api.getPlugin().dispatchCommand(null, command);
                }
            }

            api.getPlugin().sendPluginMessage(
                    ssStaff,
                    ssPlayer,
                    PluginMessageType.END
            );

            Bukkit.getPluginManager().callEvent(new SsEndEvent(staff, player, SSEndCause.SUSPECT_QUIT));
            return;
        }

        /*
            STAFFER QUITS
         */

        if (ssPlayer.getControlled()!=null) { //If the player was screensharing someone
            Player suspect = Bukkit.getPlayer(ssPlayer.getControlled().getUUID());
            api.getScreenShareManager().clearPlayer(suspect.getUniqueId());
            suspect.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_STAFF_OFFLINE.getMessage()));

            Bukkit.getPluginManager().callEvent(new SsEndEvent(player, suspect, SSEndCause.STAFF_QUIT));
        }
    }



}
