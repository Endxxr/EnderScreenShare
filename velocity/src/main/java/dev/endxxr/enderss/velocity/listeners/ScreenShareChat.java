package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.ChatSender;
import dev.endxxr.enderss.api.events.velocity.SsChatEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import net.kyori.adventure.text.TextComponent;

public class ScreenShareChat {
    private final EnderSS api;
    private final ProxyServer server;
    public ScreenShareChat(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {

        if (!event.result().isAllowed()) return;

        Player sender = event.player();
        SsPlayer ssSender = api.getPlayersManager().getPlayer(sender.id());

        ChatSender senderType = null;
        if (ssSender.isStaff() && !ssSender.isFrozen() && ssSender.getControlled()!=null) { //Because staff can be frozen
            senderType = ChatSender.STAFF;
        } else if (ssSender.isFrozen()) {
            senderType = ChatSender.SUSPECT;
        } else if (sender.connectedServer().serverInfo().name().equalsIgnoreCase(ProxyConfig.SS_SERVER.getString()) && ssSender.isStaff()) {
            senderType = ChatSender.NOT_INVOLVED;
        }

        if (senderType == null) return;
        if (GlobalConfig.CHAT_CANCEL_EVENT.getBoolean()) event.setResult(ResultedEvent.GenericResult.denied());

        ChatSender finalSenderType = senderType;
        api.getPlugin().runTaskAsync(() -> {
            String originalMessage = event.currentMessage();
            String baseFormat = GlobalConfig.valueOf("CHAT_FORMAT_" + finalSenderType.name().toUpperCase()).getString();
            String normalPrefix = GlobalConfig.valueOf("CHAT_PREFIX_" + finalSenderType.name().toUpperCase()).getString();
            String luckPermsPrefix = "";
            if (api.getPlugin().isLuckPermsPresent()) {
                luckPermsPrefix = api.getPlugin().getLuckPermsAPI().getPlayerAdapter(Player.class).getMetaData(sender).getPrefix();
                if (luckPermsPrefix == null) luckPermsPrefix = "";
            } else if (originalMessage.contains("%luckperms%")) {
                api.getPlugin().getLog().warning("LuckPerms not found, using a blank prefix");
            }

            String formattedMessage = baseFormat
                    .replace("%prefix%", normalPrefix)
                    .replace("%luckperms%", luckPermsPrefix)
                    .replace("%player%", sender.username())
                    .replace("%message%", originalMessage);

            SsChatEvent ssChatEvent = new SsChatEvent(originalMessage, formattedMessage, sender);
            server.eventManager().fireAndForget(ssChatEvent);
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

    private void sendNotInvolvedMessage(String message) {

        TextComponent formattedMessage = ChatUtils.formatAdventureComponent(message);

        for (Player player : server.server(ProxyConfig.SS_SERVER.getString()).connectedPlayers()) {

            SsPlayer SsPlayer = api.getPlayersManager().getPlayer(player.id());
            if (GlobalConfig.CHAT_NOT_INVOLVED_EVERYONE.getBoolean()) {
                player.sendMessage(formattedMessage);
            } else if (SsPlayer.isStaff()) {
                player.sendMessage(formattedMessage);
            }
        }


    }

    private void sendStaffMessage(SsPlayer ssSender, String message) {

        Player receiver = server.player(ssSender.getControlled().getUUID());
        TextComponent formattedMessage = ChatUtils.formatAdventureComponent(message);

        receiver.sendMessage(formattedMessage);
        if (GlobalConfig.CHAT_STAFFER_READS_STAFFERS.getBoolean()) {
            server.server(ProxyConfig.SS_SERVER.getString()).connectedPlayers().forEach(player -> {
                SsPlayer SsPlayer = api.getPlayersManager().getPlayer(player.id());
                if (player == receiver) return; // if the receiver is a staff member, don't send the message to him
                if (!SsPlayer.isStaff()) return;
                player.sendMessage(formattedMessage);
            });
        }
    }

    private void sendSuspectMessage(SsPlayer sender, String message) {
        TextComponent formattedMessage = ChatUtils.formatAdventureComponent(message);
        server.player(sender.getUUID()).sendMessage(formattedMessage);
        server.player(sender.getStaffer().getUUID()).sendMessage(formattedMessage);
    }





}
