package dev.endxxr.enderss.velocity.manager;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.events.velocity.SsStartEvent;
import dev.endxxr.enderss.api.exceptions.ConfigException;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import dev.endxxr.enderss.velocity.utils.ConnectionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class VelocityScreenShareManager implements dev.endxxr.enderss.api.objects.managers.ScreenShareManager {
    private final EnderSS api = EnderSSProvider.getApi();
    private final ProxyServer server;

    public VelocityScreenShareManager(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void startScreenShare(UUID staff, UUID suspect) {


        Player staffPlayer = server.getPlayer(staff).orElse(null);
        Player suspectPlayer = server.getPlayer(suspect).orElse(null);

        if (staffPlayer == null || suspectPlayer == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff or Suspect is null"), "Staff or Suspect is null!");
            return;
        }

        if (staff.equals(suspect)) { //Staff is trying to control themselves
            staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (!staffPlayer.hasPermission("enderss.staff") && !staffPlayer.hasPermission("enderss.control")) {
            staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (suspectPlayer.hasPermission("enderss.exempt") || suspectPlayer.hasPermission("enderss.bypass")) {
            staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
            return;
        }


        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff);
        SsPlayer suspectSS = api.getPlayersManager().getPlayer(suspect);


        if (staffSS == null || suspectSS == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff or Suspect is null"), "Staff or Suspect is null!");
            return;
        }

        if (suspectSS.isStaff()) {
            if (!GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
                return;
            }
        }
        if (staffSS.getControlled()!=null) {
            if (staffSS.getControlled().equals(suspectSS)) { //The staffer is already controlling this player
                staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_SS_PLAYER.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
            } else {
                staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
            }
            return;
        }

        if (suspectSS.isFrozen()) { //The suspect is already being controlled
            staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
            return;
        }


        RegisteredServer serverInfo = server.getServer(ProxyConfig.SS_SERVER.getString()).orElse(null); //Gets the screenshare server from the config
        if (serverInfo ==  null) {
            LogUtils.prettyPrintUserMistake(new ConfigException("Nonexistent ScreenShare Server"), "The ScreenShare server is not defined in the config or nonexistent!");
            return;
        }


        //Sets the players as controlled and controller
        //We set it here to prevent the player from being controlled by multiple staff members
        suspectSS.setFrozen(true);
        suspectSS.setStaffer(staffSS);
        staffSS.setControlled(suspect);
        
        try {
            ConnectionRequestBuilder.Result staffConnected = staffPlayer.createConnectionRequest(serverInfo).connect().get();
            ConnectionRequestBuilder.Result suspectConnected = suspectPlayer.createConnectionRequest(serverInfo).connect().get();

            ConnectionRequestBuilder.Status staffStatus = staffConnected.getStatus();
            ConnectionRequestBuilder.Status suspectStatus = suspectConnected.getStatus();

            if (isNotSuccessful(staffStatus) || isNotSuccessful(suspectStatus)) {
                staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
                staffSS.setControlled(null);
                suspectSS.setStaffer(null);
                suspectSS.setFrozen(false);
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            LogUtils.prettyPrintException(e, "An error occurred while connecting the players to the ScreenShare server!");
            return;
        }

        if (GlobalConfig.START_TITLE_SEND.getBoolean()) { //Send the title to the suspect
            suspectPlayer.showTitle(Title.title(
                    VelocityChat.formatAdventureComponent(GlobalConfig.START_TITLE_TITLE.getMessage()),
                    VelocityChat.formatAdventureComponent(GlobalConfig.START_TITLE_SUBTITLE.getMessage()),
                    Title.Times.times(Duration.ofMillis(GlobalConfig.START_TITLE_FADEIN.getLong()), Duration.ofMillis(GlobalConfig.START_TITLE_STAY.getLong()), Duration.ofMillis(GlobalConfig.START_TITLE_FADEOUT.getLong()))
            ));

        }

        if (GlobalConfig.START_CLEAR_CHAT.getBoolean()) { //Clears the chat
            for (int i = 0; i < 300; i++) {
                suspectPlayer.sendMessage(Component.empty());
            }
        }

        suspectPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.START_SS_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getUsername()));

        if (GlobalConfig.START_ANYDESK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.START_ANYDESK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(Component.empty());
        }
        if (GlobalConfig.START_TEAMSPEAK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(Component.empty());
        }
        if (GlobalConfig.START_DISCORD_SEND.getBoolean()) {
            suspectPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.START_DISCORD_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(Component.empty());
        }



        //BUTTONS
        if (GlobalConfig.START_BUTTONS.getSection().getKeys(false).size() > 0) {
            sendScreenShareButtons(staffPlayer, suspectPlayer);
        }

        for (Player player : server.getAllPlayers()) {

            SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
            if (ssPlayer == null) {
                continue;
            }

            if (ssPlayer.isStaff() && ssPlayer.hasAlerts()) {
                player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getUsername(), "%SUSPECT%", suspectPlayer.getUsername()));
            }
        }

        api.getPlugin().getLog().info("Staff " + staffPlayer.getUsername() + " is now controlling " + suspectPlayer.getUsername());
        api.getPlugin().sendPluginMessage(staffSS, suspectSS, PluginMessageType.START);
        server.getEventManager().fireAndForget(new SsStartEvent(staffPlayer, suspectPlayer));
    }

    @Override
    public void clearPlayer(UUID target) {

        ProxyPlayer targetSS = (ProxyPlayer) api.getPlayersManager().getPlayer(target);
        Player targetPlayer = server.getPlayer(target).orElse(null);

        if (targetPlayer == null || targetSS == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Target not found!"), "Illegal use of API!");
            return;
        }

        if (targetSS.isStaff() && !targetSS.isFrozen() && targetSS.getControlled()!=null) {
            targetSS.setControlled(null);
            return;
        }

        targetSS.setStaffer(null);
        targetSS.setFrozen(false);

        ConnectionUtils.fallback(targetPlayer, targetSS, server);

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {

                Optional<Player> playerOptional = server.getPlayer(online.getUUID());
                if (!playerOptional.isPresent()) {
                    continue;
                }

                playerOptional.get().sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", "Console",
                        "%SUSPECT%", targetPlayer.getUsername()));
            }
        }

        api.getPlugin().getLog().info("Player " + targetPlayer.getUsername() + " is now free");
        api.getPlugin().sendPluginMessage(targetSS, PluginMessageType.END);
    }

    @Override
    public void clearPlayer(UUID staff, UUID suspect) {

        Optional<Player> staffOptional = server.getPlayer(staff);
        Optional<Player> suspectOptional = server.getPlayer(suspect);

        if (!staffOptional.isPresent() || !suspectOptional.isPresent()) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff or suspect doesn't exist"), "Illegal use of API!");
            return;
        }

        Player staffPlayer = staffOptional.get();
        Player suspectPlayer = suspectOptional.get();

        ProxyPlayer staffSS = (ProxyPlayer) api.getPlayersManager().getPlayer(staff);
        ProxyPlayer suspectSS = (ProxyPlayer) api.getPlayersManager().getPlayer(suspect);

        if (staffSS == null || suspectSS == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff or suspect doesn't exist"), "Illegal use of API!");
            return;
        }

        if (!suspectSS.isFrozen()) {
            staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_SUSPECT_NOT_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
            return;
        }

        // If the staff is trying to clear a player that is not controlled by him
        if (!staffPlayer.hasPermission("enderss.admin") && !staffSS.getControlled().equals(suspectSS)) {
            staffPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NOT_CONTROLLING.getMessage(), "%SUSPECT%", suspectPlayer.getUsername()));
            return;
        }
        staffSS.setControlled(null);
        suspectSS.setStaffer(null);
        suspectSS.setFrozen(false);

        if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
            ConnectionUtils.fallback(staffPlayer, staffSS, server);
        }

        ConnectionUtils.fallback(suspectPlayer, suspectSS, server);

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {

                Optional<Player> playerOptional = server.getPlayer(online.getUUID());
                if (!playerOptional.isPresent()) {
                    continue;
                }

                playerOptional.get().sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", staffPlayer.getUsername(),
                        "%SUSPECT%", suspectPlayer.getUsername()));
            }
        }

        suspectPlayer.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_CONTROL_ENDED.getMessage(),
                "%STAFF%", staffPlayer.getUsername(),
                "%SUSPECT%", suspectPlayer.getUsername()));

        api.getPlugin().getLog().info(staffPlayer.getUsername()+" has freed "+suspectPlayer.getUsername());
        api.getPlugin().sendPluginMessage(staffSS, suspectSS, PluginMessageType.END);
    }

    private void sendScreenShareButtons(Player staffPlayer, Player suspectPlayer) {
        // Text - Action
        HashMap<String, String> stringButtons = GlobalConfig.getScreenShareButtons(suspectPlayer.getUsername());
        List<TextComponent> buttons = new ArrayList<>();

        ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        for (String key : stringButtons.keySet()) {
            TextComponent button = Component.text(key)
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(stringButtons.get(key))))
                    .clickEvent(ClickEvent.clickEvent(action, stringButtons.get(key)));
            buttons.add(button);
        }


        if (buttons.size() > 0) {
            if (GlobalConfig.START_BUTTONS_IN_LINE.getBoolean()) {
                ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
                for (TextComponent component : buttons) {
                    builder.append(component);
                }
                staffPlayer.sendMessage(builder.build());
            } else {
                for (TextComponent component : buttons) {
                    staffPlayer.sendMessage(component);
                }
            }
        }
    }

    // Disconnected or cancelled
    private boolean isNotSuccessful(ConnectionRequestBuilder.Status status) {
        return status == ConnectionRequestBuilder.Status.SERVER_DISCONNECTED || status == ConnectionRequestBuilder.Status.CONNECTION_CANCELLED;
    }

}
