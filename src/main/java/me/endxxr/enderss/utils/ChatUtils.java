package me.endxxr.enderss.utils;

import me.endxxr.enderss.enums.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtils {

    public static TextComponent format(String message) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String formatString(String message) {
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

    public static TextComponent format(String message, String... placeholders) {
        TextComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        for (int i = 0; i < placeholders.length; i += 2) {
            component.setText(component.getText().replace(placeholders[i], placeholders[i + 1]));
        }
        component.setText(component.getText().replace("%PREFIX%", Config.PREFIX.getString()));
        return component;
    }



}
