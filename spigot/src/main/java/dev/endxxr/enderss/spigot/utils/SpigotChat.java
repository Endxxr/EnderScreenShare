package dev.endxxr.enderss.spigot.utils;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class SpigotChat {
    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     *
     *
     * Formats the text and set the placeholders
     *
     * @param message the message
     * @param placeholders the placeholders, must be in pairs (placeholder, value)
     * @return the formatted text
     */

    public static String format(String message, String... placeholders) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message.replace("%PREFIX%", GlobalConfig.PREFIX.getString());
    }







}
