package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ReportCommand implements SimpleCommand {
    
    private final EnderSS api;
    private final ProxyServer server;
    private final HashMap<UUID, Long> cooldowns;

    public ReportCommand(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
        this.cooldowns = new HashMap<>();
    }


    @Override
    public void execute(Invocation invocation) {

        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        
        //CHECKS
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("enderss.staff") && !player.hasPermission("enderss.report")) {
            player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (cooldowns.containsKey(player.id())) {
            if (cooldowns.get(player.id()) > System.currentTimeMillis()) {
                player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_WAIT.getMessage(), "%SECONDS%", String.valueOf((cooldowns.get(player.id()) - System.currentTimeMillis()) / 1000)));
                return;
            }
        }

        if (args.length == 0) {
            player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        Player target = server.player(args[0]);
        if (target==player) {
            player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF.getMessage()));
            return;
        }

        if (target==null){
            player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }

        if (api.getPlayersManager().getPlayer(target.id()).isStaff()) {
            player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_STAFF.getMessage()));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_REASON.getMessage()));
            return;
        }

        //MESSAGES
        final StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        final String reasonString = reason.toString().trim();
        if (GlobalConfig.REPORTS_NO_STAFF_ENABLED.getBoolean()) {
            if (!api.getPlayersManager().isStaffOnline()) {
                player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.REPORTS_NO_STAFF.getMessage()));
                return;
            }
        }

        //STAFF
        Set<UUID> onlineStaff = api.getPlayersManager().getStaffers().keySet();
        for (UUID uuid : onlineStaff) {
            SsPlayer staff = api.getPlayersManager().getPlayer(uuid);
            if (staff.hasAlerts()) {
                TextComponent message = ChatUtils.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_REPORT_RECEIVED.getMessage()
                        .replace("%REPORTER%", player.username())
                        .replace("%REPORTED%", target.username())
                        .replace("%REASON%", reasonString)
                        .replace("%SERVER%", player.connectedServer().serverInfo().name()));
                Player staffPlayer = server.player(uuid);
                staffPlayer.sendMessage(message);
            }
        }

        //BUTTONS; //reports.buttons.x.y
        if (GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getSection().getKeys(false).size() > 0) {

            List<TextComponent> buttons = getButtons(player.username(), target.username(), reasonString, player.connectedServer().serverInfo().name());

            //Check if inline
            if (GlobalConfig.REPORTS_BUTTONS_IN_LINE.getBoolean()) {
                ComponentBuilder<TextComponent, TextComponent.Builder> cb = Component.text();
                for (TextComponent component : buttons) {
                    cb.append(component);
                }
                for (UUID uuid : onlineStaff) {
                    Player staffPlayer = server.player(uuid);
                    if (api.getPlayersManager().getPlayer(uuid).hasAlerts()) {
                        staffPlayer.sendMessage(cb.build());
                    }
                }
            } else {
                for (TextComponent component : buttons) {
                    for (UUID uuid : onlineStaff) {
                        Player staffPlayer = server.player(uuid);
                        if (api.getPlayersManager().getPlayer(uuid).hasAlerts()) {
                            staffPlayer.sendMessage(component);
                        }
                    }
                }
            }
        }

        player.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_REPORT_SENT.getMessage()
                .replaceAll("%SUSPECT%", target.username())));
        cooldowns.put(player.id(), System.currentTimeMillis()+ GlobalConfig.REPORTS_COOLDOWN.getLong()*1000);
    }

    private List<TextComponent> getButtons(String reporterName, String reportedName, String reason, String serverName) {
        List<TextComponent> buttons = new ArrayList<>();
        ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        ConfigurationSection section = GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getSection();
        for (String key : section.getKeys(false)) {
            String name = GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getButtonText(key); //Button name
            String command = GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getButtonCommand(key); //Button command
            if (name == null || command == null) {
                api.getPlugin().getLog().info("The report button: " + key + " is not configured correctly!");
                continue;
            }

            TextComponent component = ChatUtils.formatAdventureComponent(name);
            String formattedCommand = "/"+command
                    .replace("%REPORTER%", reporterName)
                    .replace("%REPORTED%", reportedName)
                    .replace("%REASON%", reason)
                    .replace("%SERVER%", serverName);

            component = component.clickEvent(ClickEvent.clickEvent(action, formattedCommand));
            component = component.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(formattedCommand)));
            buttons.add(component);
        }
        return buttons;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            for (SsPlayer ss : api.getPlayersManager().getRegisteredPlayers() ) {
                Player player = server.player(ss.getUUID());
                if (player.username().toLowerCase().startsWith(args[0].toLowerCase()) && !ss.isStaff()) { //If start with that letter and isn't staff
                    players.add(player.username());
                }
            }
            return players;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
