package dev.endxxr.enderss.common.utils;

import dev.endxxr.enderss.api.EnderSSProvider;
import org.jetbrains.annotations.NotNull;

public class ChatUtils {

    private static final String PREFIX = EnderSSProvider.getApi().getPlugin().getGeneralConfig().getString("prefix");

    /**
     *
     * Formats the text with the placeholders
     *
     * @param message the message
     * @param placeholders the placeholders, must be in pairs (placeholder, value)
     * @return the formatted text
     */

    public static String format(String message, String... placeholders) {

        if (message == null) return null;

        message = message.replace("%PREFIX%", PREFIX);
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return translateFormatChar(message);
    }

    private static String translateFormatChar(@NotNull String message) {
        char[] charArray = message.toCharArray();

        for(int i = 0; i < charArray.length - 1; ++i) {
            if (charArray[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(charArray[i + 1]) > -1) {
                charArray[i] = 167;
                charArray[i + 1] = Character.toLowerCase(charArray[i + 1]);
            }
        }

        return new String(charArray);
    }

}
