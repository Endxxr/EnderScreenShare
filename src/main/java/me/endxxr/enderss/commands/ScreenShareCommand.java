package me.endxxr.enderss.commands;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ScreenShareCommand extends Command implements TabExecutor {


    private final EnderSS instance;
    private final HashMap<String, TextComponent> premadeButtons = new HashMap<>();

    public ScreenShareCommand(EnderSS instance) {
        super("screenshare", "enderss.screenshare", "ss", "freeze", "controllo");
        this.instance = instance;

        TextComponent hack = new TextComponent(ChatUtils.format(Config.BUTTONS_HACK.getString()));
        TextComponent admission = new TextComponent(ChatUtils.format(Config.BUTTONS_ADMISSION.getMessage()));
        TextComponent refuse = new TextComponent(ChatUtils.format(Config.BUTTONS_REFUSE.getMessage()));
        TextComponent quit = new TextComponent(ChatUtils.format(Config.BUTTONS_QUIT.getString()));
        TextComponent clean = new TextComponent(ChatUtils.format(Config.BUTTONS_CLEAN.getString()));

        premadeButtons.put("hack", hack);
        premadeButtons.put("refuse", refuse);
        premadeButtons.put("admission", admission);
        premadeButtons.put("quit", quit);
        premadeButtons.put("clean", clean);

    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /*
             CHECKS
         */


        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;

        }

        final ProxiedPlayer staff = (ProxiedPlayer) sender;

        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(args[0]);
        if (suspect == null) { //If the player is offline, sus will be null
            sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        if (suspect.hasPermission("enderss.exempt") || suspect.hasPermission("enderss.bypass")) {
            sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspect.getName()));
            return;
        }

        final SsPlayer susSession = instance.getPlayersManager().getPlayer(suspect);
        final SsPlayer staffSession = instance.getPlayersManager().getPlayer(staff);

        if (staff.equals(suspect)) { //Staff is trying to control themselves
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (susSession.isStaff()) {
            if (!Config.CONFIG_STAFF_CONTROLLABLE.getBoolean()) {
                staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspect.getName()));
                return;
            } else {
                susSession.setStaffIgnored(false); //Set temporally to false //TODO
            }
        }

        if (susSession.isFrozen()) { //The suspect is already being controlled
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_ALREADY_IN_SS.getString(), "%SUSPECT%", suspect.getName()));
            return;
        }
        if (staffSession.getControlled().equals(suspect)) { //The staffer is already controlling this player
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_ALREADY_SS_PLAYER.getString(), "%SUSPECT%", suspect.getName()));
            return;
        }
        if (staffSession.getControlled() != null) { //The staff is already controlling someone
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspect.getName()));
            return;
        }

        /*
            CONNECT PHASE
         */


        ServerInfo server = ProxyServer.getInstance().getServerInfo(Config.CONFIG_SSSERVER.getString()); //Gets the screenshare server from the config


        if (server.canAccess(staff) || server.canAccess(suspect)) { //Checks if the staff and the suspect can access the screenshare server
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
            return;
        }


        CompletableFuture<Boolean> connectStaff = CompletableFuture.supplyAsync(() -> {
            AtomicBoolean staffConnected = new AtomicBoolean(false);
            ServerConnectRequest request = ServerConnectRequest.builder()
                    .target(server)
                    .reason(ServerConnectEvent.Reason.PLUGIN)
                    .callback(((result, error) -> {
                        if (error == null && (result == ServerConnectRequest.Result.ALREADY_CONNECTED || result == ServerConnectRequest.Result.ALREADY_CONNECTING || result == ServerConnectRequest.Result.SUCCESS)) {
                            staffConnected.set(true);
                        } else {
                            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
                            ;

                        }

                    }))
                    .build();
            staff.connect(request);
            return staffConnected.get();
        });
        if (!connectStaff.join()) {
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANT_CONNECT_TO_SS.getMessage()));
            return;
        }

        CompletableFuture<Boolean> connectSuspect = CompletableFuture.supplyAsync(() -> {
            AtomicBoolean susConnected = new AtomicBoolean(false);
            ServerConnectRequest request = ServerConnectRequest.builder()
                    .target(server)
                    .reason(ServerConnectEvent.Reason.PLUGIN)
                    .callback(((result, error) -> {
                        if (error == null && (result == ServerConnectRequest.Result.ALREADY_CONNECTED || result == ServerConnectRequest.Result.ALREADY_CONNECTING || result == ServerConnectRequest.Result.SUCCESS)) {
                            susConnected.set(true);
                        } else {
                            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANT_CONNECT_SUSPECT.getMessage()));
                            ;
                            instance.getLogger().severe("Error while connecting suspect to screenshare server: " + error.getMessage());
                        }

                    }))
                    .build();
            suspect.connect(request);
            return susConnected.get();
        });

        if (!connectSuspect.join()) {
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANT_CONNECT_SUSPECT.getMessage()));
            return;
        }

        susSession.setFrozen(true);
        susSession.setStaffer(staff);
        staffSession.setControlled(suspect);


        /*
            MESSAGES
         */

        if (Config.START_TITLE_SEND.getBoolean()) { //Send the title to the suspect
            ProxyServer.getInstance().createTitle()
                    .title(new TextComponent(ChatUtils.format(Config.START_TITLE_TITLE.getString())))
                    .subTitle(new TextComponent(ChatUtils.format(Config.START_TITLE_SUBTITLE.getString())))
                    .fadeIn(Config.START_TITLE_FADEIN.getInt())
                    .stay(Config.START_TITLE_STAY.getInt())
                    .fadeOut(Config.START_TITLE_FADEOUT.getInt())
                    .send(suspect);
        }

        if (Config.CONFIG_CLEAR_CHAT.getBoolean()) { //Clears the chat
            for (int i = 0; i < 300; i++) {
                suspect.sendMessage(new TextComponent(""));
            }
        }
        for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
            SsPlayer session = instance.getPlayersManager().getPlayer(receiver);
            if (session.isStaff() && session.isAlerts()) {
                receiver.sendMessage(ChatUtils.format(Config.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staff.getName(), "%SUSPECT%", suspect.getName()));
            }
        }

        suspect.sendMessage(ChatUtils.format(Config.START_SS_MESSAGE.getMessage(), "%STAFF%", staff.getName()));

        if (Config.START_ANYDESK_SEND.getBoolean()) {
            suspect.sendMessage(ChatUtils.format(Config.START_ANYDESK_MESSAGE.getMessage()));
            suspect.sendMessage("");
        }
        if (Config.START_TEAMSPEAK_SEND.getBoolean()) {
            suspect.sendMessage(ChatUtils.format(Config.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspect.sendMessage("");
        }
        if (Config.START_DISCORD_SEND.getBoolean()) {
            suspect.sendMessage(ChatUtils.format(Config.START_DISCORD_MESSAGE.getMessage()));
            suspect.sendMessage("");
        }



        //BUTTONS
        final List<TextComponent> buttons = new ArrayList<>();
        if (Config.START_BUTTONS.getSection().getKeys().size() > 0) {
            boolean runCommand = Config.BUTTONS_CONFIRM_BUTTONS.getBoolean();
            for (String button : Config.START_BUTTONS_ELEMENTS.getSection().getKeys()) {
                String type = Config.START_BUTTONS_ELEMENTS.getButtonType(button).toLowerCase();
                if (premadeButtons.containsKey(type)) { //Checks if the button is a premade button
                    TextComponent component = premadeButtons.get(type);
                    String command = Config.valueOf("BAN_COMMAND_" + type.toUpperCase()).getString().replace("%SUSPECT%", suspect.getName());
                    ClickEvent.Action action = runCommand ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(command)));
                    component.setClickEvent(new ClickEvent(action, command));
                    buttons.add(component);
                } else {
                    String text = Config.START_BUTTONS_ELEMENTS.getButtonText(button);
                    String command = Config.START_BUTTONS_ELEMENTS.getButtonCommand(button).replace("%SUSPECT%", suspect.getName());
                    ClickEvent.Action action = runCommand ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
                    TextComponent component = new TextComponent(ChatUtils.format(text));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(command)));
                    component.setClickEvent(new ClickEvent(action, command));
                    buttons.add(component);
                }
            }
            if (buttons.size() > 0) {
                if (Config.START_BUTTONS_IN_LINE.getBoolean()) {
                    final ComponentBuilder builder = new ComponentBuilder("");
                    for (TextComponent component : buttons) {
                        builder.append(component);
                    }
                    staff.sendMessage(builder.create());
                } else {
                    for (TextComponent component : buttons) {
                        staff.sendMessage(component);
                    }
                }
            }
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (instance.getPlayersManager().getPlayer(player).isStaff() && instance.getPlayersManager().getPlayer(player).isAlerts()) {
                player.sendMessage(ChatUtils.format(Config.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staff.getName(), "%SUSPECT%", suspect.getName()));
            }
        }

        instance.getScoreboardManager().createScoreboards(staff, suspect);

    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        List<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().startsWith(args[0])).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach(player -> results.add(player.getName()));
        players.clear();
        return results;
    }
}