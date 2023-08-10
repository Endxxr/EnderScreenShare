package dev.endxxr.enderss.bungeecord.managers;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.events.bungee.SsStartEvent;
import dev.endxxr.enderss.api.exceptions.ConfigException;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class BungeeScreenShareManager implements dev.endxxr.enderss.api.objects.managers.ScreenShareManager {

    private final EnderSS api = EnderSSProvider.getApi();
    
    @Override
    public void startScreenShare(UUID staff, UUID suspect) {


        ProxiedPlayer staffPlayer = ProxyServer.getInstance().getPlayer(staff);
        ProxiedPlayer suspectPlayer = ProxyServer.getInstance().getPlayer(suspect);


        if (staff.equals(suspect)) { //Staff is trying to control themselves
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (!staffPlayer.hasPermission("enderss.staff") && !staffPlayer.hasPermission("enderss.control")) {
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (suspectPlayer.hasPermission("enderss.exempt") || suspectPlayer.hasPermission("enderss.bypass")) {
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }


        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff);
        SsPlayer suspectSS = api.getPlayersManager().getPlayer(suspect);

        if (staffSS == null || suspectSS == null) {
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }


        if (suspectSS.isStaff()) {
            if (!GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
                return;
            }
        }

        if (staffSS.getControlled()!=null) {
            if (staffSS.getControlled().equals(suspectSS)) { //The staffer is already controlling this player
                staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_SS_PLAYER.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            } else {
                staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            }
            return;
        }

        if (suspectSS.isFrozen()) { //The suspect is already being controlled
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }


        ServerInfo server = ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()); //Gets the screenshare server from the config
        if (server ==  null) {
            LogUtils.prettyPrintUserMistake(new ConfigException("Nonexistent ScreenShare Server"), "The ScreenShare server is not defined in the config or nonexistent!");
            return;
        }

        if (!server.canAccess(staffPlayer) || !server.canAccess(suspectPlayer)) { //Checks if the staff and the suspect can access the screenshare server
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
            return;
        }

        //Sets the players as controlled and controller
        //We set it here to prevent the player from being controlled by multiple staff members
        suspectSS.setFrozen(true);
        suspectSS.setStaffer(staffSS);
        staffSS.setControlled(suspect);

        CompletableFuture<Boolean> connectionRequest  = CompletableFuture.supplyAsync(() -> {
            AtomicBoolean connected = new AtomicBoolean(true);
            ServerConnectRequest request = ServerConnectRequest.builder()
                    .target(server)
                    .reason(ServerConnectEvent.Reason.PLUGIN)
                    .callback(((result, error) -> {
                        if (error != null || result == ServerConnectRequest.Result.FAIL || result == ServerConnectRequest.Result.EVENT_CANCEL ) {
                            api.getPlugin().getLog().warning("Error while connecting a player to the screenshare server");
                            api.getPlugin().getLog().warning("Error: " + error);
                            connected.set(false);
                        }
                    }))
                    .build();

            staffPlayer.connect(request);
            suspectPlayer.connect(request);
            return connected.get();
        });

        try {
            //Waits for the connection to be completed
            if (!connectionRequest.get()) {
                staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
                staffSS.setControlled(null);
                suspectSS.setStaffer(null);
                suspectSS.setFrozen(false);
                return;
            }
        } catch (Exception e) {
            LogUtils.prettyPrintUserMistake(e, "Error while connecting a player to the screenshare server");
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
            staffSS.setControlled(null);
            suspectSS.setStaffer(null);
            suspectSS.setFrozen(false);
            return;
        }


        if (GlobalConfig.START_TITLE_SEND.getBoolean()) { //Send the title to the suspect
            api.getPlugin().runTaskLater( () -> ProxyServer.getInstance().createTitle()
                    .title(new TextComponent(BungeeChat.formatComponent(GlobalConfig.START_TITLE_TITLE.getString(), "%STAFF%", staffPlayer.getName())))
                    .subTitle(new TextComponent(BungeeChat.formatComponent(GlobalConfig.START_TITLE_SUBTITLE.getString(), "%STAFF%", staffPlayer.getName())))
                    .fadeIn(GlobalConfig.START_TITLE_FADEIN.getInt())
                    .stay(GlobalConfig.START_TITLE_STAY.getInt())
                    .fadeOut(GlobalConfig.START_TITLE_FADEOUT.getInt())
                    .send(suspectPlayer), GlobalConfig.START_TITLE_DELAY.getInt());
        }

        if (GlobalConfig.START_CLEAR_CHAT.getBoolean()) { //Clears the chat
            for (int i = 0; i < 300; i++) {
                suspectPlayer.sendMessage(new TextComponent(""));
            }
        }

        suspectPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.START_SS_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName()));

        if (GlobalConfig.START_ANYDESK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.START_ANYDESK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(new TextComponent(""));
        }
        if (GlobalConfig.START_TEAMSPEAK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(new TextComponent(""));
        }
        if (GlobalConfig.START_DISCORD_SEND.getBoolean()) {
            suspectPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.START_DISCORD_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(new TextComponent(""));
        }



        //BUTTONS
        if (GlobalConfig.START_BUTTONS.getSection().getKeys(false).size() > 0) {
            sendScreenShareButtons(staffPlayer, suspectPlayer);
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

            SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
            if (ssPlayer == null) {
                continue;
            }

            if (ssPlayer.isStaff() && ssPlayer.hasAlerts()) {
                player.sendMessage(BungeeChat.formatComponent(GlobalConfig.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName(), "%SUSPECT%", suspectPlayer.getName()));
            }
        }

        api.getPlugin().getLog().info("Staff " + staffPlayer.getName() + " is now controlling " + suspectPlayer.getName());
        api.getPlugin().sendPluginMessage(staffSS, suspectSS, PluginMessageType.START);
        ProxyServer.getInstance().getPluginManager().callEvent(new SsStartEvent(staffPlayer, suspectPlayer));
    }

    @Override
    public void clearPlayer(UUID target) {

        ProxyPlayer targetSS = (ProxyPlayer) api.getPlayersManager().getPlayer(target);
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

        if (targetSS == null || targetPlayer == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("The player " + target + " is not registered in the plugin"), "Illegal use of API!");
            return;
        }

        if (targetSS.isStaff() && !targetSS.isFrozen() && targetSS.getControlled()!=null) {
            targetSS.setControlled(null);
            return;
        }

        targetSS.setStaffer(null);
        targetSS.setFrozen(false);

        if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
            targetPlayer.connect(ProxyServer.getInstance().getServerInfo(targetSS.getLastServer()));
        } else {
            targetPlayer.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
        }

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {
                ProxyServer.getInstance().getPlayer(online.getUUID()).sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", "Console",
                        "%SUSPECT%", targetPlayer.getName()));
            }
        }

        api.getPlugin().getLog().info("Player " + targetPlayer.getName() + " is now free");
        api.getPlugin().sendPluginMessage(targetSS, PluginMessageType.END);
    }

    @Override
    public void clearPlayer(UUID staff, UUID suspect) {

        ProxiedPlayer staffPlayer = ProxyServer.getInstance().getPlayer(staff);
        ProxiedPlayer suspectPlayer = ProxyServer.getInstance().getPlayer(suspect);
        ProxyPlayer ssStaff = (ProxyPlayer) api.getPlayersManager().getPlayer(staff);
        ProxyPlayer ssSuspect = (ProxyPlayer) api.getPlayersManager().getPlayer(suspect);

        if (staffPlayer == null || suspectPlayer == null || ssStaff == null || ssSuspect == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("The player " + staff + " or " + suspect + " is not registered in the plugin"), "Illegal use of API!");
            return;
        }


        if (!ssSuspect.isFrozen()) {
            staffPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_SUSPECT_NOT_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }


        ssStaff.setControlled(null);
        ssSuspect.setStaffer(null);
        ssSuspect.setFrozen(false);

        if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
            if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
                staffPlayer.connect(ProxyServer.getInstance().getServerInfo(ssStaff.getLastServer()));
            } else {
                staffPlayer.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
            }
        }

        if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
            suspectPlayer.connect(ProxyServer.getInstance().getServerInfo(ssSuspect.getLastServer()));
        } else {
            suspectPlayer.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
        }

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {
                ProxyServer.getInstance().getPlayer(online.getUUID()).sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", staffPlayer.getName(),
                        "%SUSPECT%", suspectPlayer.getName()));
            }
        }

        suspectPlayer.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_INFO_CONTROL_ENDED.getMessage(),
                "%STAFF%", staffPlayer.getName(),
                "%SUSPECT%", suspectPlayer.getName()));

        api.getPlugin().getLog().info(staffPlayer.getName()+" has freed "+suspectPlayer.getName());
        api.getPlugin().sendPluginMessage(ssStaff, ssSuspect, PluginMessageType.END);
    }

    private void sendScreenShareButtons(ProxiedPlayer staffPlayer, ProxiedPlayer suspectPlayer) {
        // Text - Action
        HashMap<String, String> stringButtons = GlobalConfig.getScreenShareButtons(suspectPlayer.getName());
        List<TextComponent> buttons = new ArrayList<>();

        ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        for (String key : stringButtons.keySet()) {
            TextComponent button = new TextComponent(key);
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(stringButtons.get(key))));
            button.setClickEvent(new ClickEvent(action, stringButtons.get(key)));
            buttons.add(button);
        }


        if (buttons.size() > 0) {
            if (GlobalConfig.START_BUTTONS_IN_LINE.getBoolean()) {
                ComponentBuilder builder = new ComponentBuilder("");
                for (TextComponent component : buttons) {
                    builder.append(component);
                }
                staffPlayer.sendMessage(builder.create());
            } else {
                for (TextComponent component : buttons) {
                    staffPlayer.sendMessage(component);
                }
            }
        }
    }
}
