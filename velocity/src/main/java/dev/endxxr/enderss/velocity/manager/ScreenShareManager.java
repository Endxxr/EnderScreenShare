package dev.endxxr.enderss.velocity.manager;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import com.velocitypowered.api.proxy.player.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.events.velocity.SsStartEvent;
import dev.endxxr.enderss.api.exceptions.ConfigException;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ScreenShareManager implements dev.endxxr.enderss.api.objects.managers.ScreenShareManager {
    private final EnderSS api = EnderSSProvider.getApi();
    private final ProxyServer server;
    private final Map<String, TextComponent> premadeButtons = new HashMap<>();

    public ScreenShareManager(ProxyServer server) {

        this.server = server;

        TextComponent hack = ChatUtils.formatAdventureComponent(GlobalConfig.BUTTONS_HACK.getString());
        TextComponent admission = ChatUtils.formatAdventureComponent(GlobalConfig.BUTTONS_ADMISSION.getString());
        TextComponent refuse = ChatUtils.formatAdventureComponent(GlobalConfig.BUTTONS_REFUSE.getString());
        TextComponent quit = ChatUtils.formatAdventureComponent(GlobalConfig.BUTTONS_QUIT.getString());
        TextComponent clean = ChatUtils.formatAdventureComponent(GlobalConfig.BUTTONS_CLEAN.getString());

        premadeButtons.put("hack", hack);
        premadeButtons.put("refuse", refuse);
        premadeButtons.put("admission", admission);
        premadeButtons.put("quit", quit);
        premadeButtons.put("clean", clean);


    }

    @Override
    public void startScreenShare(UUID staff, UUID suspect) {


        Player staffPlayer = server.player(staff);
        Player suspectPlayer = server.player(suspect);


        if (staff.equals(suspect)) { //Staff is trying to control themselves
            staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (!staffPlayer.hasPermission("enderss.staff") && !staffPlayer.hasPermission("enderss.control")) {
            staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (suspectPlayer.hasPermission("enderss.exempt") || suspectPlayer.hasPermission("enderss.bypass")) {
            staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspectPlayer.username()));
            return;
        }


        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff);
        SsPlayer suspectSS = api.getPlayersManager().getPlayer(suspect);

        if (suspectSS.isStaff()) {
            if (!GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspectPlayer.username()));
                return;
            }
        }
        if (staffSS.getControlled()!=null) {
            if (staffSS.getControlled().equals(suspectSS)) { //The staffer is already controlling this player
                staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_SS_PLAYER.getMessage(), "%SUSPECT%", suspectPlayer.username()));
            } else {
                staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.username()));
            }
            return;
        }

        if (suspectSS.isFrozen()) { //The suspect is already being controlled
            staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_ALREADY_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.username()));
            return;
        }


        RegisteredServer serverInfo = server.server(ProxyConfig.SS_SERVER.getString()); //Gets the screenshare server from the config
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
            if (!staffConnected.isSuccessful() || !suspectConnected.isSuccessful()) {
                staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
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
            server.showTitle(Title.title(
                    ChatUtils.formatAdventureComponent(GlobalConfig.START_TITLE_TITLE.getMessage()),
                    ChatUtils.formatAdventureComponent(GlobalConfig.START_TITLE_SUBTITLE.getMessage()),
                    Title.Times.of(Duration.ofMillis(GlobalConfig.START_TITLE_FADEIN.getLong()), Duration.ofMillis(GlobalConfig.START_TITLE_STAY.getLong()), Duration.ofMillis(GlobalConfig.START_TITLE_FADEOUT.getLong()))
            ));

        }

        if (GlobalConfig.START_CLEAR_CHAT.getBoolean()) { //Clears the chat
            for (int i = 0; i < 300; i++) {
                suspectPlayer.sendMessage(Component.empty());
            }
        }

        suspectPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.START_SS_MESSAGE.getMessage(), "%STAFF%", staffPlayer.username()));

        if (GlobalConfig.START_ANYDESK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.START_ANYDESK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(Component.empty());
        }
        if (GlobalConfig.START_TEAMSPEAK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(Component.empty());
        }
        if (GlobalConfig.START_DISCORD_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.START_DISCORD_MESSAGE.getMessage()));
            suspectPlayer.sendMessage(Component.empty());
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
                        command = "/clean " + suspectPlayer.username();
                    } else {
                        command = GlobalConfig.valueOf("BAN_COMMAND_" + type.toUpperCase()).getString().replace("%SUSPECT%", suspectPlayer.username());
                    }

                    component = component.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(command)));
                    component = component.clickEvent(ClickEvent.clickEvent(action, command));
                    buttons.add(component);

                } else {
                    String text = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonText(button);
                    String command = "/"+GlobalConfig.START_BUTTONS_ELEMENTS.getButtonCommand(button).replace("%SUSPECT%", suspectPlayer.username());
                    TextComponent component = ChatUtils.formatAdventureComponent(text);
                    component = component.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(command)));
                    component = component.clickEvent(ClickEvent.clickEvent(action, command));
                    buttons.add(component);
                }
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

        for (Player player : server.connectedPlayers()) {
            if (api.getPlayersManager().getPlayer(player.id()).isStaff() && api.getPlayersManager().getPlayer(player.id()).hasAlerts()) {
                player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staffPlayer.username(), "%SUSPECT%", suspectPlayer.username()));
            }
        }

        api.getPlugin().getLog().info("Staff " + staffPlayer.username() + " is now controlling " + suspectPlayer.username());
        api.getPlugin().sendPluginMessage(staffSS, suspectSS, PluginMessageType.START);
        server.eventManager().fireAndForget(new SsStartEvent(staffPlayer, suspectPlayer));
    }

    @Override
    public void clearPlayer(UUID target) {

        ProxyPlayer targetSS = (ProxyPlayer) api.getPlayersManager().getPlayer(target);
        Player targetPlayer = server.player(target);

        if (targetSS.isStaff() && !targetSS.isFrozen() && targetSS.getControlled()!=null) {
            targetSS.setControlled(null);
            return;
        }

        targetSS.setStaffer(null);
        targetSS.setFrozen(false);

        if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
            targetPlayer.createConnectionRequest(server.server(targetSS.getLastServer())).fireAndForget();
        } else {
            targetPlayer.createConnectionRequest(server.server(ProxyConfig.FALLBACK_SERVER.getString())).fireAndForget();
        }

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {
                server.player(online.getUUID()).sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", "Console",
                        "%SUSPECT%", targetPlayer.username()));
            }
        }

        api.getPlugin().getLog().info("Player " + targetPlayer.username() + " is now free");
        api.getPlugin().sendPluginMessage(targetSS, PluginMessageType.END);
    }

    @Override
    public void clearPlayer(UUID staff, UUID suspect) {

        Player staffPlayer = server.player(staff);
        Player suspectPlayer = server.player(suspect);
        ProxyPlayer ssStaff = (ProxyPlayer) api.getPlayersManager().getPlayer(staff);
        ProxyPlayer ssSuspect = (ProxyPlayer) api.getPlayersManager().getPlayer(suspect);

        if (!ssSuspect.isFrozen()) {
            staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_SUSPECT_NOT_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.username()));
            return;
        }

        // If the staff is trying to clear a player that is not controlled by him
        if (!staffPlayer.hasPermission("enderss.admin") && !ssStaff.getControlled().equals(ssSuspect)) {
            staffPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NOT_CONTROLLING.getMessage(), "%SUSPECT%", suspectPlayer.username()));
            return;
        }
        ssStaff.setControlled(null);
        ssSuspect.setStaffer(null);
        ssSuspect.setFrozen(false);

        if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
            if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
                staffPlayer.createConnectionRequest(server.server(ssStaff.getLastServer())).fireAndForget();
            } else {
                staffPlayer.createConnectionRequest(server.server(ProxyConfig.FALLBACK_SERVER.getString())).fireAndForget();
            }
        }

        if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
            suspectPlayer.createConnectionRequest(server.server(ssSuspect.getLastServer())).fireAndForget();
        } else {
            suspectPlayer.createConnectionRequest(server.server(ProxyConfig.FALLBACK_SERVER.getString())).fireAndForget();
        }

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {
                server.player(online.getUUID()).sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", staffPlayer.username(),
                        "%SUSPECT%", suspectPlayer.username()));
            }
        }

        suspectPlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_CONTROL_ENDED.getMessage(),
                "%STAFF%", staffPlayer.username(),
                "%SUSPECT%", suspectPlayer.username()));

        api.getPlugin().getLog().info(staffPlayer.username()+" has freed "+suspectPlayer.username());
        api.getPlugin().sendPluginMessage(ssStaff, ssSuspect, PluginMessageType.END);
    }
}
