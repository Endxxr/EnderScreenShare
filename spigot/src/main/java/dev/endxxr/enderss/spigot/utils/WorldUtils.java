package dev.endxxr.enderss.spigot.utils;

import dev.endxxr.enderss.api.exceptions.ConfigException;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldUtils {

    public static Location getSpawnLocation(SsPlayer player) {
        return getLocation(player, "spawn");
    }

    public static Location getFallbackLocation(SsPlayer player) {
        return getLocation(player, "fallback");
    }


    private static Location getLocation(SsPlayer ssPlayer, String path) {
        String key = ssPlayer.isStaff() ? path + ".staff" : path + ".suspect";
        String coordinates = SpigotConfig.config.getString(key);
        String[] split = coordinates.split(",");


        try {
            if (split.length != 6) LogUtils.prettyPrintUserMistake(new ConfigException("Invalid spawn coordinates, args must be 6"), "Please check your spigot.yml file.");
            World world = Bukkit.getWorld(split[0]);
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = Float.parseFloat(split[4]);
            float pitch = Float.parseFloat(split[5]);

            if (world == null) LogUtils.prettyPrintUserMistake(new ConfigException("Invalid spawn coordinates, world not found"), "Please check your spigot.yml file.");

            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            LogUtils.prettyPrintUserMistake(e, "Some coordinates are not numbers. Please check your spigot.yml file.");
            return null;
        }
    }
}
