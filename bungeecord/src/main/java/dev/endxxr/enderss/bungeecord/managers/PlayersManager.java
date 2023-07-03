package dev.endxxr.enderss.bungeecord.managers;

import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlayersManager extends dev.endxxr.enderss.api.objects.managers.PlayersManager {

    @Override
    public @NotNull SsPlayer registerPlayer(@NotNull UUID uuid) {
        return registeredPlayers.computeIfAbsent(uuid, ProxyPlayer::new);
    }



    @Override
    public void broadcastStaff(String formattedMessage) {
        TextComponent component = new TextComponent(formattedMessage);
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            SsPlayer ssPlayer = this.getPlayer(onlinePlayer.getUniqueId());
            if (ssPlayer==null) continue;
            if (ssPlayer.isStaff() && ssPlayer.hasAlerts()) {
                onlinePlayer.sendMessage(component);
            }
        }

    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return ProxyServer.getInstance().getPlayer(uuid).hasPermission(permission);
    }

    @Override
    public @NotNull List<String> getControllablePlayers(String initialChars) {

        List<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().startsWith(initialChars)).collect(Collectors.toList());
        List<String> results = new ArrayList<>();

        players.forEach( proxiedPlayer -> {
            SsPlayer ssPlayer = this.getPlayer(proxiedPlayer.getUniqueId());
            if (ssPlayer==null) return;
            if (!ssPlayer.isFrozen()) {
                if (ssPlayer.isStaff() && GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                    results.add(proxiedPlayer.getName());
                } else if (!ssPlayer.isStaff()) {
                    results.add(proxiedPlayer.getName());
                }
            }
        });

        return results;
    }

    @Override
    public Object getPlatformPlayer(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid);
    }
}

