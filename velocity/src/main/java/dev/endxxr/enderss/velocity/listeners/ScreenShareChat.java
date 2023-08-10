package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.ChatSender;
import dev.endxxr.enderss.api.events.velocity.SsChatEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import net.kyori.adventure.text.TextComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ScreenShareChat {
    private final EnderSS api;
    private final ProxyServer server;
    public ScreenShareChat(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {

        if (!event.getResult().isAllowed()) return;
        
        Player sender = event.getPlayer();
        SsPlayer senderSS = api.getPlayersManager().getPlayer(sender.getUniqueId());
        ServerConnection serverConnection = sender.getCurrentServer().orElse(null);

        if (senderSS==null){
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }



        ChatSender senderType = null;
        if (senderSS.isStaff() && !senderSS.isFrozen() && senderSS.getControlled()!=null) { //Because staff can be frozen
            senderType = ChatSender.STAFF;
        } else if (senderSS.isFrozen()) {
            senderType = ChatSender.SUSPECT;
        } else if (serverConnection != null && serverConnection.getServerInfo().getName().equalsIgnoreCase(ProxyConfig.SS_SERVER.getString()) && senderSS.isStaff()) {
            senderType = ChatSender.NOT_INVOLVED;
        }

        if (senderType == null) return;
        if (GlobalConfig.CHAT_CANCEL_EVENT.getBoolean()) event.setResult(PlayerChatEvent.ChatResult.denied());

        ChatSender finalSenderType = senderType;
        api.getPlugin().runTaskAsync(() -> {
            String originalMessage = event.getMessage();
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
                    .replace("%player%", sender.getUsername())
                    .replace("%message%", originalMessage);

            SsChatEvent ssChatEvent = new SsChatEvent(originalMessage, formattedMessage, sender);
            try {
                ssChatEvent = server.getEventManager().fire(ssChatEvent).get();
            } catch (Exception e) {
                LogUtils.prettyPrintException(e, "Error while firing SsChatEvent");
            }


            if (ssChatEvent.getResult()== ResultedEvent.GenericResult.denied()) return;
            formattedMessage = ssChatEvent.getMessage();

            switch (finalSenderType) {
                case STAFF:
                    sendStaffMessage(senderSS, formattedMessage);
                    break;
                case SUSPECT:
                    sendSuspectMessage(senderSS, formattedMessage);
                    break;
                case NOT_INVOLVED:
                    sendNotInvolvedMessage(formattedMessage);
                    break;
            }
        });




    }

    private void sendNotInvolvedMessage(String message) {

        TextComponent formattedMessage = VelocityChat.formatAdventureComponent(message);
        Collection<Player> players = server.getServer(ProxyConfig.SS_SERVER.getString()).map(RegisteredServer::getPlayersConnected).orElse(Collections.emptyList());

        for (Player player : players) {
            SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
            if (GlobalConfig.CHAT_NOT_INVOLVED_EVERYONE.getBoolean()) {
                player.sendMessage(formattedMessage);
            } else if (playerSS != null && playerSS.isStaff()) {
                player.sendMessage(formattedMessage);
            }
        }


    }

    private void sendStaffMessage(SsPlayer ssSender, String message) {

        Optional<Player> optionalReceiver = server.getPlayer(ssSender.getControlled().getUUID());
        if (!optionalReceiver.isPresent()) return;
        Player receiver = optionalReceiver.get();

        TextComponent formattedMessage = VelocityChat.formatAdventureComponent(message);


        receiver.sendMessage(formattedMessage);
        if (GlobalConfig.CHAT_STAFFER_READS_STAFFERS.getBoolean()) {

            Collection<Player> players = server.getServer(ProxyConfig.SS_SERVER.getString()).map(RegisteredServer::getPlayersConnected).orElse(Collections.emptyList());

            players.forEach(player -> {
                SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
                if (player == receiver) return; // if the receiver is a staff member, don't send the message to him
                if (playerSS == null || !playerSS.isStaff()) return;
                player.sendMessage(formattedMessage);
            });
        }
    }

    private void sendSuspectMessage(SsPlayer sender, String message) {
        TextComponent formattedMessage = VelocityChat.formatAdventureComponent(message);
        Optional<Player> optionalSender = server.getPlayer(sender.getUUID());
        Optional<Player> optionalReceiver = server.getPlayer(sender.getStaffer().getUUID());

        if (!optionalSender.isPresent() || !optionalReceiver.isPresent()) return;

        Player suspectSender = optionalSender.get();
        Player staffReceiver = optionalReceiver.get();


        suspectSender.sendMessage(formattedMessage);
        staffReceiver.sendMessage(formattedMessage);

       Collection<Player> connectedPlayers = server.getServer(ProxyConfig.SS_SERVER.getString()).map(RegisteredServer::getPlayersConnected).orElse(Collections.emptyList());
        if (GlobalConfig.CHAT_NOT_INVOLVED_EVERYONE.getBoolean()) {
            for (Player player : connectedPlayers) {
                SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
                if (playerSS!=null && playerSS.isStaff() && playerSS.getControlled()==null) {
                    player.sendMessage(formattedMessage);
                }
            }
        }
    }





}
