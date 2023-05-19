package dev.endxxr.enderss.bungeecord.managers;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeePlayersManager extends PlayersManager {

    @Override
    public Collection<UUID> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getUniqueId).collect(Collectors.toList());
    }


    @Override
    public boolean hasPermission(String permission, UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid).hasPermission(permission);
    }
}
