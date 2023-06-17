package dev.endxxr.enderss.spigot.managers;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.player.SpigotPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager extends PlayersManager {
    @Override
    public SsPlayer registerPlayer(UUID uuid) {
        return registeredPlayers.computeIfAbsent(uuid, SpigotPlayer::new);
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return Objects.requireNonNull(Bukkit.getPlayer(uuid)).hasPermission(permission);
    }

    @Override
    public List<String> getControllablePlayers(String initialChars) {

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().startsWith(initialChars)).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach( proxiedPlayer -> {
            SsPlayer ssPlayer = this.getPlayer(proxiedPlayer.getUniqueId());
            if (!ssPlayer.isFrozen()) {
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
}
