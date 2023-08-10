package dev.endxxr.enderss.spigot.managers;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.player.SpigotPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SpigotPlayersManager extends PlayersManager {
    @Override
    public @NotNull SsPlayer registerPlayer(@NotNull UUID uuid) {
        return registeredPlayers.computeIfAbsent(uuid, SpigotPlayer::new);
    }

    @Override
    public void broadcastStaff(String formattedMessage) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            SsPlayer ssPlayer = this.getPlayer(onlinePlayer.getUniqueId());
            if (ssPlayer==null) continue;
            if (ssPlayer.isStaff() && ssPlayer.hasAlerts()) {
                onlinePlayer.sendMessage(formattedMessage);
            }
        }

    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return Objects.requireNonNull(Bukkit.getPlayer(uuid)).hasPermission(permission);
    }

    @Override
    public @NotNull List<String> getControllablePlayers(String initialChars) {

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().startsWith(initialChars)).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach( proxiedPlayer -> {
            SsPlayer ssPlayer = this.getPlayer(proxiedPlayer.getUniqueId());
            if (ssPlayer != null && !ssPlayer.isFrozen()) {
                if (ssPlayer.isStaff() && GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                    results.add(proxiedPlayer.getName());
                } else if (!ssPlayer.isStaff()) {
                    results.add(proxiedPlayer.getName());
                }
            }
        });
        players.clear();
        return results;
    }

    @Override
    public Object getPlatformPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
