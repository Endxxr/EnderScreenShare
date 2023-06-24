package dev.endxxr.enderss.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager extends PlayersManager {

    private final ProxyServer server;

    public PlayerManager(ProxyServer server) {
        this.server = server;
    }

    @Override
    public @NotNull SsPlayer registerPlayer(@NotNull UUID uuid) {
        return registeredPlayers.computeIfAbsent(uuid, ProxyPlayer::new);
    }

    @Override
    public void broadcastStaff(String formattedMessage) {
        Component component = Component.text(formattedMessage);
        for (Player onlinePlayer : server.getAllPlayers()) {
            SsPlayer ssPlayer = this.getPlayer(onlinePlayer.getUniqueId());
            if (ssPlayer==null) continue;
            if (ssPlayer.isStaff() && ssPlayer.hasAlerts()) {
                onlinePlayer.sendMessage(component);
            }
        }
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {

        Optional<Player> player = server.getPlayer(uuid);
        return player.map(value -> value.hasPermission(permission)).orElse(false);

    }

    @Override
    public @NotNull List<String> getControllablePlayers(String initialChars) {
        List<Player> players = server.getAllPlayers().stream().filter(player -> player.getUsername().startsWith(initialChars)).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach( proxiedPlayer -> {
            SsPlayer ssPlayer = this.getPlayer(proxiedPlayer.getUniqueId());

            if (ssPlayer != null && !ssPlayer.isFrozen()) {
                if (ssPlayer.isStaff() && GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                    results.add(proxiedPlayer.getUsername());
                } else if (!ssPlayer.isStaff()) {
                    results.add(proxiedPlayer.getUsername());
                }
            }
        });
        players.clear();
        return results;
    }

    @Override
    public @Nullable Object getPlatformPlayer(@Nullable UUID uuid) {
        if (uuid==null) return null;
        return server.getPlayer(uuid);
    }
}
