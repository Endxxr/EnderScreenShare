package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("enderss.staff") && !player.hasPermission("enderss.report")) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (cooldowns.containsKey(player.getUniqueId())) {
            if (cooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_WAIT.getMessage(), "%SECONDS%", String.valueOf((cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)));
                return;
            }
        }

        if (args.length == 0) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        Optional<Player> optionalTarget = server.getPlayer(args[0]);
        if (!optionalTarget.isPresent()) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }

        Player target = optionalTarget.get();

        if (target==player) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF.getMessage()));
            return;
        }

        SsPlayer targetSS = api.getPlayersManager().getPlayer(target.getUniqueId());

        if (targetSS==null) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }

        if (targetSS.isStaff()) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_STAFF.getMessage()));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_REASON.getMessage()));
            return;
        }

        //MESSAGES
        final StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        final String reasonString = reason.toString().trim();
        if (GlobalConfig.REPORTS_NO_STAFF_ENABLED.getBoolean()) {
            if (api.getPlayersManager().isStaffOffline()) {
                player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.REPORTS_NO_STAFF.getMessage()));
                return;
            }
        }


        api.getPlayersManager().broadcastStaff(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_REPORT_RECEIVED.getMessage()
                .replace("%REPORTER%", player.getUsername())
                .replace("%REPORTED%", target.getUsername())
                .replace("%REASON%", reasonString)
                .replace("%SERVER%", player.getCurrentServer().map(serverConn -> serverConn.getServerInfo().getName()).orElse("Unknown"))));

        if (GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getSection().getKeys(false).size() > 0) {
            Set<UUID> onlineStaff = api.getPlayersManager().getStaffers().keySet();
            sendButtons(onlineStaff, player, target, reasonString);
        }

        player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.REPORTS_MESSAGES_REPORT_SENT.getMessage()
                .replaceAll("%SUSPECT%", target.getUsername())));
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+ GlobalConfig.REPORTS_COOLDOWN.getLong()*1000);
    }

    private void sendButtons(Set<UUID> onlineStaff, Player player, Player target, String reasonString) {

        HashMap<String, String> stringButtons = GlobalConfig.getReportButtons(player.getUsername(),
                target.getUsername(),
                reasonString,
                player.getCurrentServer().map(serverConn -> serverConn.getServerInfo().getName()).orElse("Unknown"));


        List<TextComponent> buttons = new ArrayList<>();
        ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        for (String key : stringButtons.keySet()) {
            TextComponent button = Component.text(key)
                    .hoverEvent(HoverEvent.showText(Component.text(stringButtons.get(key))))
                    .clickEvent(ClickEvent.clickEvent(action, stringButtons.get(key)));
            buttons.add(button);
        }

        //Check if inline
        if (GlobalConfig.REPORTS_BUTTONS_IN_LINE.getBoolean()) {
            ComponentBuilder<TextComponent, TextComponent.Builder> cb = Component.text();
            for (TextComponent component : buttons) {
                cb.append(component);
            }
            for (UUID uuid : onlineStaff) {

                Optional<Player> optionalStaff = server.getPlayer(uuid);
                if (!optionalStaff.isPresent()) continue;
                Player staffPlayer = optionalStaff.get();
                SsPlayer staff = api.getPlayersManager().getPlayer(uuid);

                if (staff != null && staff.hasAlerts()) {
                    staffPlayer.sendMessage(cb.build());
                }
            }
        } else {
            for (TextComponent component : buttons) {
                for (UUID uuid : onlineStaff) {

                    Optional<Player> optionalStaff = server.getPlayer(uuid);
                    if (!optionalStaff.isPresent()) continue;
                    Player staffPlayer = optionalStaff.get();
                    SsPlayer staff = api.getPlayersManager().getPlayer(uuid);

                    if (staff != null && staff.hasAlerts()) {
                        staffPlayer.sendMessage(component);
                    }
                }
            }
        }
    }


    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        String prefix = args.length == 0 ? "" : args[0];

        if (args.length>1) return Collections.emptyList();

        List<String> players = new ArrayList<>();
        for (SsPlayer ss : api.getPlayersManager().getRegisteredPlayers() ) {
            Optional<Player> optionalPlayer = server.getPlayer(ss.getUUID());
            if (!optionalPlayer.isPresent()) continue;
            Player player = optionalPlayer.get();

            if (player.getUsername().toLowerCase().startsWith(prefix) && !ss.isStaff()) { //If start with that letter and isn't staff
                players.add(player.getUsername());
            }
        }
        return players;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
