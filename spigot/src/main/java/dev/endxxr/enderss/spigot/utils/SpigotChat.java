package dev.endxxr.enderss.spigot.utils;

import dev.endxxr.enderss.common.utils.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpigotChat {

    public static List<TextComponent> buildButtons(HashMap<String, String> stringButtons, boolean confirmButton) {
        List<TextComponent> buttons = new ArrayList<>();
        ClickEvent.Action action = confirmButton ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        for (String key : stringButtons.keySet()) {
            TextComponent button = new TextComponent(key);
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(stringButtons.get(key))}));
            button.setClickEvent(new ClickEvent(action, stringButtons.get(key)));
            buttons.add(button);
        }
        return buttons;
    }

    public static TextComponent formatComponent(String message) {
        return new TextComponent(ChatUtils.format(message));
    }

    public static TextComponent formatComponent(String message, String... placeholders) {
        return new TextComponent(ChatUtils.format(message, placeholders));
    }


}
