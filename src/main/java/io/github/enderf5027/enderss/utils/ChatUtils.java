package io.github.enderf5027.enderss.utils;

import io.github.enderf5027.enderss.session.SessionManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChatUtils {


    public static ArrayList<String> placeholders = new ArrayList<>();

    public ChatUtils(){
        placeholders.add("%SUSPECT%");
        placeholders.add("%STAFF%");
    }


    public static String PlaceHolderManager(String message, String placeholder, ProxiedPlayer player){
        switch (placeholder){
            case "%STAFF%":
                if (!SessionManager.getSession(player).isStaff()) { //The player gets the value "isStaff" on join, checking the permission "enderss.staff"
                    break;
                }
                message = message.replace(placeholder, player.getName());
                break;
            case  "%SUSPECT%":
                if (SessionManager.getSession(player).isStaff()) {
                    break;
                }
                message = message.replace(placeholder, player.getName());
                if (message.contains("%STAFF%")){
                    if (SessionManager.getSession(player).isFrozen()) {
                        ProxiedPlayer staffer = SessionManager.getSession(player).getStaffer();
                        message = message.replace("%STAFF%", staffer.getName());
                    } else {
                        message = message.replace("%STAFF%", "Staff"); //If the player isn't frozen, we can use Staff as a generic name or just change it with a replace
                    }
                }
            }
        return message;
    }

    public static String PlaceHolderManager(String message, ProxiedPlayer p1, ProxiedPlayer p2){
        if (message.contains("%SUSPECT%")) {
            message = PlaceHolderManager(message, "%SUSPECT%", p1);
            message = PlaceHolderManager(message, "%SUSPECT%", p2);
            if (SessionManager.getSession(p1).isStaff()) message = message.replace("Staff", p1.getName());
            if (SessionManager.getSession(p2).isStaff()) message = message.replace("Staff", p2.getName());
        }
        if (message.contains("%STAFF%")) {
            if (SessionManager.getSession(p1).isStaff()) message = message.replace("%STAFF%", p1.getName());
            if (SessionManager.getSession(p2).isStaff()) message = message.replace("%STAFF%", p2.getName());
        }
        return message;
    }



    public static TextComponent format(String message){ //For messages that don't allow placeholders
        for (String placeholder : placeholders) {
            if (message.contains(placeholder)){
                Logger logger = ProxyServer.getInstance().getLogger();
                logger.severe("You cant use "+placeholder+" in this message!");
                logger.severe("Message: "+message);
            }
        }
        message = addPrefix(message);

        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static TextComponent format(String message, ProxiedPlayer player){ //message that might contain ONLY 1 placeholder (usually is %SUSPECT%)
        for (String placeholder : placeholders) {
            if (message.contains(placeholder)){
                message = PlaceHolderManager(message, placeholder, player);
            }
        }
        message = addPrefix(message);
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static TextComponent format(String message, ProxiedPlayer staff, ProxiedPlayer suspect) { //message that might contain 2 (MAX) placeholders
        message = PlaceHolderManager(message, staff, suspect);
        message = addPrefix(message);
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }


    public static void sendListMessage(List<String> messages, ProxiedPlayer staff, ProxiedPlayer suspect, ProxiedPlayer receiver){
        for (String message : messages) {
            receiver.sendMessage(format(message, staff, suspect));
        }
    }

    public static TextComponent addPrefix(TextComponent message){ //Replace %prefix% with the prefix
        String prefix = config.prefix;
        String stringmessage = message.getText().replace("%PREFIX%", prefix);
        return format(stringmessage);
    }

    public static String addPrefix(String message){
        String prefix = config.prefix;
        message = message.replace("%PREFIX%", prefix);
        return message;
    }

    public static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);

        long minute = TimeUnit.SECONDS.toMinutes(seconds) -
                (TimeUnit.SECONDS.toHours(seconds)* 60);

        long second = TimeUnit.SECONDS.toSeconds(seconds) -
                (TimeUnit.SECONDS.toMinutes(seconds) *60);

        return hours + config.hours + minute + config.minutes + second + config.seconds;
    }


}
