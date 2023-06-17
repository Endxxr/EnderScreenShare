package dev.endxxr.enderss.velocity.manager;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.velocity.EnderSSVelocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager extends PlayersManager {

    private final ProxyServer server;

    public PlayerManager(ProxyServer server) {
        this.server = server;
    }

    @Override
    public SsPlayer registerPlayer(UUID uuid) {
        return registeredPlayers.put(uuid, new ProxyPlayer(uuid));
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return Objects.requireNonNull(EnderSSVelocity.getInstance().getServer().player(uuid)).hasPermission(permission);
    }

    @Override
    public List<String> getControllablePlayers(String initialChars) {
        List<Player> players = server.connectedPlayers().stream().filter(player -> player.username().startsWith(initialChars)).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach( proxiedPlayer -> {
            SsPlayer ssPlayer = this.getPlayer(proxiedPlayer.id());
            if (!ssPlayer.isFrozen()) {
                if (ssPlayer.isStaff() && GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                    results.add(proxiedPlayer.username());
                } else if (!ssPlayer.isStaff()) {
                    results.add(proxiedPlayer.username());
                }
            }
        });
        players.clear();
        return results;
    }
}
