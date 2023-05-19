package dev.endxxr.enderss.spigot.managers;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotPlayerManager extends PlayersManager {
    @Override
    public Collection<UUID> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(String permission, UUID uuid) {
        return Objects.requireNonNull(Bukkit.getPlayer(uuid)).hasPermission(permission);
    }
}
