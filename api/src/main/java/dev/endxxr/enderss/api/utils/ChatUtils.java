package dev.endxxr.enderss.api.utils;

import dev.endxxr.enderss.api.EnderSSProvider;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {



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
        String PREFIX = EnderSSProvider.getApi().getPlugin().getGeneralConfig().getString("prefix");
        message = message.replace("%PREFIX%", PREFIX);
        message = formatHex(message);
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }

        return message;
    }



    public static String formatHex(String message) { //From https://www.spigotmc.org/threads/hex-color-code-translate.449748/
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent formatComponent(String message) {
        return new TextComponent(format(message));
    }

    public static TextComponent formatComponent(String message, String... placeholders) {
        return new TextComponent(format(message, placeholders));
    }

    public static net.kyori.adventure.text.@NonNull TextComponent formatAdventureComponent(String message, String... placeholders) {
        return Component.text(format(message, placeholders));
    }




}
