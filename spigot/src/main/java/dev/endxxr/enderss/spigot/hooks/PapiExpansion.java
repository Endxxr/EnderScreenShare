package dev.endxxr.enderss.spigot.hooks;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "enderss";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Endxxr";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (!player.isOnline()) return null;

        SsPlayer ssPlayer = EnderSSProvider.getApi().getPlayersManager().getPlayer(player.getUniqueId());
        if(ssPlayer == null) return null;

        String yes = SpigotConfig.PLACEHOLDER_YES.getString();
        String no = SpigotConfig.PLACEHOLDER_NO.getString();

        if(params.equalsIgnoreCase("suspect")){
            return Bukkit.getPlayer(ssPlayer.getUUID()).getName();
        }

        if(params.equalsIgnoreCase("staff")){
            return Bukkit.getPlayer(ssPlayer.getUUID()).getName();
        }

        if (params.equalsIgnoreCase("is_staff")) {
            return ssPlayer.isStaff() ? yes : no;
        }

        if (params.equalsIgnoreCase("is_controlling")) {
            return ssPlayer.getControlled()!=null ? yes : no;
        }

        if (params.equalsIgnoreCase("is_frozen")) {
            return ssPlayer.isFrozen() ? yes : no;
        }

        return null; // Placeholder is unknown by the Expansion
    }


}
