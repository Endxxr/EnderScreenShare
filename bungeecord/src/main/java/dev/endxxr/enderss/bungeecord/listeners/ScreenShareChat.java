package dev.endxxr.enderss.bungeecord.listeners;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.ChatSender;
import dev.endxxr.enderss.api.events.bungee.SsChatEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ScreenShareChat implements Listener {

    private final EnderSS api;
    public ScreenShareChat() {
        this.api = EnderSSProvider.getApi();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {

        if (event.isCancelled() || event.isCommand() || event.isProxyCommand()) return;

        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        SsPlayer ssSender = api.getPlayersManager().getPlayer(sender.getUniqueId());

        if (ssSender==null) {
            sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }

        ChatSender senderType = null;
        if (ssSender.isStaff() && !ssSender.isFrozen() && ssSender.getControlled()!=null) { //Because staff can be frozen
            senderType = ChatSender.STAFF;
        } else if (ssSender.isFrozen()) {
            senderType = ChatSender.SUSPECT;
        } else if (sender.getServer().getInfo().getName().equalsIgnoreCase(ProxyConfig.SS_SERVER.getString()) && ssSender.isStaff()) {
            senderType = ChatSender.NOT_INVOLVED;
        }

        if (senderType == null) return;
        if (GlobalConfig.CHAT_CANCEL_EVENT.getBoolean()) event.setCancelled(true);

        ChatSender finalSenderType = senderType;
        api.getPlugin().runTaskAsync(() -> {
            String originalMessage = event.getMessage();
            String baseFormat = GlobalConfig.valueOf("CHAT_FORMAT_" + finalSenderType.name().toUpperCase()).getString();
            String normalPrefix = GlobalConfig.valueOf("CHAT_PREFIX_" + finalSenderType.name().toUpperCase()).getString();
            String luckPermsPrefix = "";
            if (api.getPlugin().isLuckPermsPresent()) {
                luckPermsPrefix = api.getPlugin().getLuckPermsAPI().getPlayerAdapter(ProxiedPlayer.class).getMetaData(sender).getPrefix();
                if (luckPermsPrefix == null) luckPermsPrefix = "";
            } else if (originalMessage.contains("%luckperms%")) {
                api.getPlugin().getLog().warning("LuckPerms not found, using a blank prefix");
            }

            String formattedMessage = baseFormat
                    .replace("%prefix%", normalPrefix)
                    .replace("%luckperms%", luckPermsPrefix)
                    .replace("%player%", sender.getName())
                    .replace("%message%", originalMessage);

            SsChatEvent ssChatEvent = new SsChatEvent(originalMessage, formattedMessage, sender);
            ProxyServer.getInstance().getPluginManager().callEvent(ssChatEvent);
            if (ssChatEvent.isCancelled()) return;
            formattedMessage = ssChatEvent.getMessage();

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

    private void sendStaffMessage(SsPlayer ssSender, String message) {

        ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(ssSender.getUUID());
        ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(ssSender.getControlled().getUUID());
        TextComponent formattedMessage = BungeeChat.formatComponent(message);

        sender.sendMessage(formattedMessage);
        receiver.sendMessage(formattedMessage);
        if (GlobalConfig.CHAT_STAFFER_READS_STAFFERS.getBoolean()) {
            ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()).getPlayers().forEach(player -> {
                SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
                if (player == receiver || player == sender) return; // if the receiver is a staff member, don't send the message to him
                if (playerSS==null || !playerSS.isStaff()) return;

                player.sendMessage(formattedMessage);
            });
        }
    }

    private void sendSuspectMessage(SsPlayer sender, String message) {
        TextComponent formattedMessage = BungeeChat.formatComponent(message);
        ProxyServer.getInstance().getPlayer(sender.getUUID()).sendMessage(formattedMessage);
        ProxyServer.getInstance().getPlayer(sender.getStaffer().getUUID()).sendMessage(formattedMessage);
    }

    private void sendNotInvolvedMessage(String message) {

        TextComponent formattedMessage = BungeeChat.formatComponent(message);

        for (ProxiedPlayer player : ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()).getPlayers()) {

            SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
            if (GlobalConfig.CHAT_NOT_INVOLVED_EVERYONE.getBoolean()) {
                player.sendMessage(formattedMessage);
            } else if (playerSS!=null && playerSS.isStaff()) {
                player.sendMessage(formattedMessage);
            }
        }


    }

}
