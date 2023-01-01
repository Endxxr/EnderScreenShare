package me.endxxr.enderss.listeners;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import me.endxxr.enderss.utils.PlayerUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.awt.*;
import java.lang.reflect.Proxy;

enum SenderType {
    STAFF,
    SUSPECT,
    NOT_INVOLVED,
    NONE
}
public class ScreenShareChat implements Listener {

    private final EnderSS plugin;
    public ScreenShareChat(EnderSS plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {

        if (event.isCancelled() || event.isCommand() || event.isProxyCommand()) return;

        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        SsPlayer ssSender = plugin.getPlayersManager().getPlayer(sender);


        SenderType senderType = SenderType.NONE;
        if (ssSender.isStaff() && !ssSender.isFrozen() && ssSender.getControlled()!=null) { //Because staff can be frozen
            senderType = SenderType.STAFF;
        } else if (ssSender.isFrozen()) {
            senderType = SenderType.SUSPECT;
        } else if (PlayerUtils.isInSsServer(sender) && ssSender.isStaff()) {
            senderType = SenderType.NOT_INVOLVED;
        }
        if (senderType == SenderType.NONE) return;

        if (Config.CHAT_CANCEL_EVENT.getBoolean()) event.setCancelled(true);

        SenderType finalSenderType = senderType;
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            String originalMessage = event.getMessage();
            String baseFormat = Config.valueOf("CHAT_FORMAT_" + finalSenderType.name().toUpperCase()).getString();
            String normalPrefix = Config.valueOf("CHAT_PREFIX_" + finalSenderType.name().toUpperCase()).getString();
            String luckPermsPrefix = "";
            if (plugin.isLuckPermsPresent()) {
                luckPermsPrefix = plugin.getLuckPerms().getPlayerAdapter(ProxiedPlayer.class).getMetaData(sender).getPrefix();
                if (luckPermsPrefix == null) luckPermsPrefix = "";
            } else if (originalMessage.contains("%luckperms%")) {
                plugin.getLogger().warning("LuckPerms not found, using a blank prefix");
            }

            String formattedMessage = baseFormat
                    .replace("%prefix%", normalPrefix)
                    .replace("%luckperms%", luckPermsPrefix)
                    .replace("%player%", sender.getDisplayName())
                    .replace("%message%", originalMessage);


            switch (finalSenderType) {
                case STAFF:
                    sendStaffMessage(ssSender, formattedMessage);
                    break;
                case SUSPECT:
                    sendSuspectMessage(ssSender, formattedMessage);
                    break;
                case NOT_INVOLVED:
                    sendNotInvolvedMessage(formattedMessage);
                    break;
            }
        });
        



    }

    private void sendNotInvolvedMessage(String message) {

        TextComponent formattedMessage = ChatUtils.format(message);

        for (ProxiedPlayer player : ProxyServer.getInstance().getServerInfo(Config.CONFIG_SSSERVER.getString()).getPlayers()) {

            SsPlayer ssPlayer = plugin.getPlayersManager().getPlayer(player);
            if (Config.CHAT_NOT_INVOLVED_EVERYONE.getBoolean()) {
                player.sendMessage(formattedMessage);
            } else if (ssPlayer.isStaff()) {
                player.sendMessage(formattedMessage);
            }
        }


    }

    private void sendStaffMessage(SsPlayer ssSender, String message) {
        
        ProxiedPlayer receiver = ssSender.getControlled();
        TextComponent formattedMessage = ChatUtils.format(message);

        receiver.sendMessage(formattedMessage);
        if (Config.CHAT_STAFFER_READS_STAFFERS.getBoolean()) {
            ProxyServer.getInstance().getServerInfo(Config.CONFIG_SSSERVER.getString()).getPlayers().forEach(player -> {
                SsPlayer ssPlayer = plugin.getPlayersManager().getPlayer(player);
                if (player == receiver) return; // if the receiver is a staff member, don't send the message to him
                if (!ssPlayer.isStaff()) return;
                player.sendMessage(formattedMessage);
            });
        }
    }

    private void sendSuspectMessage(SsPlayer sender, String message) {
        TextComponent formattedMessage = ChatUtils.format(message);
        ProxyServer.getInstance().getPlayer(sender.getUuid()).sendMessage(formattedMessage);
        sender.getStaffer().sendMessage(formattedMessage);
    }

}
