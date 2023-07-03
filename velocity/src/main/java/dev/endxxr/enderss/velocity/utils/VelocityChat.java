package dev.endxxr.enderss.velocity.utils;

import dev.endxxr.enderss.common.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

public class VelocityChat {
    public static net.kyori.adventure.text.@NonNull TextComponent formatAdventureComponent(String message, String... placeholders) {
        return Component.text(ChatUtils.format(message, placeholders));
    }


}
