package dev.endxxr.enderss.spigot.listeners;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.ChatSender;
import dev.endxxr.enderss.api.events.spigot.SsChatEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ScreenShareChat implements Listener {

    private final EnderSS api;
    public ScreenShareChat() {
        this.api = EnderSSProvider.getApi();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled() || event.getMessage().startsWith("/")) return;

        Player sender = event.getPlayer();
        SsPlayer ssSender = api.getPlayersManager().getPlayer(sender.getUniqueId());

        if (ssSender==null) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }

        ChatSender senderType = null;
        if (ssSender.isStaff() && !ssSender.isFrozen() && ssSender.getControlled()!=null) { //Because staff can be frozen
            senderType = ChatSender.STAFF;
        } else if (ssSender.isFrozen()) {
            senderType = ChatSender.SUSPECT;
        } else if (ssSender.isStaff() && ssSender.getControlled()==null) {
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
                luckPermsPrefix = api.getPlugin().getLuckPermsAPI().getPlayerAdapter(Player.class).getMetaData(sender).getPrefix();
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
            Bukkit.getPluginManager().callEvent(ssChatEvent);
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

        String formattedMessage = ChatUtils.format(message);

        for (Player player : Bukkit.getOnlinePlayers()) {

            SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
            if (GlobalConfig.CHAT_NOT_INVOLVED_EVERYONE.getBoolean()) {
                player.sendMessage(formattedMessage);
            } else if (playerSS==null || playerSS.isStaff()) {
                player.sendMessage(formattedMessage);
            }
        }


    }

    private void sendStaffMessage(SsPlayer ssSender, String message) {
        
        Player receiver = Bukkit.getPlayer(ssSender.getControlled().getUUID());
        String formattedMessage = ChatUtils.format(message);

        receiver.sendMessage(formattedMessage);
        if (GlobalConfig.CHAT_STAFFER_READS_STAFFERS.getBoolean()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                SsPlayer playerSS = api.getPlayersManager().getPlayer(player.getUniqueId());
                if (player == receiver) return; // if the receiver is a staff member, don't send the message to him
                if (playerSS==null || !playerSS.isStaff()) return;
                player.sendMessage(formattedMessage);
            });
        }
    }

    private void sendSuspectMessage(SsPlayer sender, String message) {
        String formattedMessage = ChatUtils.format(message);
        Bukkit.getPlayer(sender.getUUID()).sendMessage(formattedMessage);
        Bukkit.getPlayer(sender.getStaffer().getUUID()).sendMessage(formattedMessage);
    }

}
