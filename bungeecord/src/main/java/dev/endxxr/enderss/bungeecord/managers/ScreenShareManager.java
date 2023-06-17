package dev.endxxr.enderss.bungeecord.managers;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.events.bungee.SsStartEvent;
import dev.endxxr.enderss.api.exceptions.ConfigException;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenShareManager implements dev.endxxr.enderss.api.objects.managers.ScreenShareManager {

    private final EnderSS api = EnderSSProvider.getApi();
    private final Map<String, TextComponent> premadeButtons = new HashMap<>();

    public ScreenShareManager() {

        TextComponent hack = new TextComponent(ChatUtils.formatComponent(GlobalConfig.BUTTONS_HACK.getString()));
        TextComponent admission = new TextComponent(ChatUtils.formatComponent(GlobalConfig.BUTTONS_ADMISSION.getString()));
        TextComponent refuse = new TextComponent(ChatUtils.formatComponent(GlobalConfig.BUTTONS_REFUSE.getString()));
        TextComponent quit = new TextComponent(ChatUtils.formatComponent(GlobalConfig.BUTTONS_QUIT.getString()));
        TextComponent clean = new TextComponent(ChatUtils.formatComponent(GlobalConfig.BUTTONS_CLEAN.getString()));

        premadeButtons.put("hack", hack);
        premadeButtons.put("refuse", refuse);
        premadeButtons.put("admission", admission);
        premadeButtons.put("quit", quit);
        premadeButtons.put("clean", clean);


    }
    
    @Override
    public void startScreenShare(UUID staff, UUID suspect) {


        ProxiedPlayer staffPlayer = ProxyServer.getInstance().getPlayer(staff);
        ProxiedPlayer suspectPlayer = ProxyServer.getInstance().getPlayer(suspect);


        if (staff.equals(suspect)) { //Staff is trying to control themselves
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (!staffPlayer.hasPermission("enderss.staff") && !staffPlayer.hasPermission("enderss.control")) {
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (suspectPlayer.hasPermission("enderss.exempt") || suspectPlayer.hasPermission("enderss.bypass")) {
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }


        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff);
        SsPlayer suspectSS = api.getPlayersManager().getPlayer(suspect);

        if (suspectSS.isStaff()) {
            if (!GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
                return;
            }
        }
        if (staffSS.getControlled()!=null) {
            if (staffSS.getControlled().equals(suspectSS)) { //The staffer is already controlling this player
                staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_SS_PLAYER.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            } else {
                staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            }
            return;
        }

        if (suspectSS.isFrozen()) { //The suspect is already being controlled
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }


        ServerInfo server = ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()); //Gets the screenshare server from the config
        if (server ==  null) {
            LogUtils.prettyPrintUserMistake(new ConfigException("Nonexistent ScreenShare Server"), "The ScreenShare server is not defined in the config or nonexistent!");
            return;
        }

        if (!server.canAccess(staffPlayer) || !server.canAccess(suspectPlayer)) { //Checks if the staff and the suspect can access the screenshare server
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
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
                staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
                staffSS.setControlled(null);
                suspectSS.setStaffer(null);
                suspectSS.setFrozen(false);
                return;
            }
        } catch (Exception e) {
            LogUtils.prettyPrintUserMistake(e, "Error while connecting a player to the screenshare server");
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
            staffSS.setControlled(null);
            suspectSS.setStaffer(null);
            suspectSS.setFrozen(false);
            return;
        }


        if (GlobalConfig.START_TITLE_SEND.getBoolean()) { //Send the title to the suspect
            api.getPlugin().runTaskLater( () -> ProxyServer.getInstance().createTitle()
                    .title(new TextComponent(ChatUtils.formatComponent(GlobalConfig.START_TITLE_TITLE.getString(), "%STAFF%", staffPlayer.getName())))
                    .subTitle(new TextComponent(ChatUtils.formatComponent(GlobalConfig.START_TITLE_SUBTITLE.getString(), "%STAFF%", staffPlayer.getName())))
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

        suspectPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.START_SS_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName()));

        if (GlobalConfig.START_ANYDESK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.START_ANYDESK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }
        if (GlobalConfig.START_TEAMSPEAK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }
        if (GlobalConfig.START_DISCORD_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.START_DISCORD_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }



        //BUTTONS
        if (GlobalConfig.START_BUTTONS.getSection().getKeys(false).size() > 0) {

            List<TextComponent> buttons = new ArrayList<>();
            ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;

            for (String button : GlobalConfig.START_BUTTONS_ELEMENTS.getSection().getKeys(false)) {
                String type = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonType(button).toLowerCase();
                if (premadeButtons.containsKey(type)) { //Checks if the button is a premade button
                    TextComponent component = premadeButtons.get(type);
                    String command;
                    if (type.equalsIgnoreCase("clean")) {
                        command = "/clean " + suspectPlayer.getName();
                    } else {
                        command = GlobalConfig.valueOf("BAN_COMMAND_" + type.toUpperCase()).getString().replace("%SUSPECT%", suspectPlayer.getName());
                    }
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(command)}));
                    component.setClickEvent(new ClickEvent(action, command));
                    buttons.add(component);

                } else {
                    String text = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonText(button);
                    String command = "/"+GlobalConfig.START_BUTTONS_ELEMENTS.getButtonCommand(button).replace("%SUSPECT%", suspectPlayer.getName());
                    TextComponent component = new TextComponent(ChatUtils.formatComponent(text));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(command)}));
                    component.setClickEvent(new ClickEvent(action, command));
                    buttons.add(component);
                }
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

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (api.getPlayersManager().getPlayer(player.getUniqueId()).isStaff() && api.getPlayersManager().getPlayer(player.getUniqueId()).hasAlerts()) {
                player.sendMessage(ChatUtils.formatComponent(GlobalConfig.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName(), "%SUSPECT%", suspectPlayer.getName()));
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
                ProxyServer.getInstance().getPlayer(online.getUUID()).sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
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

        if (!ssSuspect.isFrozen()) {
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_SUSPECT_NOT_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }

        // If the staff is trying to clear a player that is not controlled by him
        if (!staffPlayer.hasPermission("enderss.admin") && !ssStaff.getControlled().equals(ssSuspect)) {
            staffPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_NOT_CONTROLLING.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
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
                ProxyServer.getInstance().getPlayer(online.getUUID()).sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", staffPlayer.getName(),
                        "%SUSPECT%", suspectPlayer.getName()));
            }
        }

        suspectPlayer.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_INFO_CONTROL_ENDED.getMessage(),
                "%STAFF%", staffPlayer.getName(),
                "%SUSPECT%", suspectPlayer.getName()));

        api.getPlugin().getLog().info(staffPlayer.getName()+" has freed "+suspectPlayer.getName());
        api.getPlugin().sendPluginMessage(ssStaff, ssSuspect, PluginMessageType.END);
    }
}
