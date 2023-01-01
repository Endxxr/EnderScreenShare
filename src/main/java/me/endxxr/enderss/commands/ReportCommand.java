package me.endxxr.enderss.commands;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.*;


public class ReportCommand extends Command implements TabExecutor {

    private final EnderSS plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    public ReportCommand() {
        super("report", "enderss.report", "report", "segnala");
        plugin = EnderSS.getInstance();

    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        //CHECKS
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (cooldowns.containsKey(player.getUniqueId())) {
            if (cooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(ChatUtils.format(Config.REPORTS_MESSAGES_WAIT.getMessage(), "%SECONDS%", String.valueOf((cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)));
                return;
            }
        }

        if (args.length == 0) {
            player.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
        if (target==player) {
            player.sendMessage(ChatUtils.format(Config.REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF.getMessage()));
            return;
        }

        if (target==null){
            player.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }

        if (plugin.getPlayersManager().getPlayer(target).isStaff()) {
            player.sendMessage(ChatUtils.format(Config.REPORTS_MESSAGES_CANNOT_REPORT_STAFF.getMessage()));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_NO_REASON.getMessage()));
            return;
        }

        //MESSAGES
        final StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        final String reasonString = reason.toString().trim();
        if (Config.REPORTS_NO_STAFF_ENABLED.getBoolean()) {
            if (!plugin.getPlayersManager().isStaffOnline()) {
                player.sendMessage(ChatUtils.format(Config.REPORTS_NO_STAFF.getMessage()));
                return;
            }
        }

        //STAFF
        Set<UUID> onlineStaff = plugin.getPlayersManager().getStaffers().keySet();
        for (UUID uuid : onlineStaff) {
            ProxiedPlayer staff = plugin.getProxy().getPlayer(uuid);
            if (plugin.getPlayersManager().getPlayer(staff).isAlerts()) {
                TextComponent message = new TextComponent(ChatUtils.format(Config.REPORTS_MESSAGES_REPORT_RECEIVED.getMessage()
                        .replace("%REPORTER%", player.getName())
                        .replace("%REPORTED%", target.getName())
                        .replace("%REASON%", reasonString)
                        .replace("%SERVER%", player.getServer().getInfo().getName())));
                staff.sendMessage(message);
            }
        }

        //BUTTONS; //reports.buttons.x.y
        if (Config.REPORTS_BUTTONS_ELEMENTS.getSection().getKeys().size() > 0) {
            List<TextComponent> buttons = new ArrayList<>();
            ClickEvent.Action action = Config.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
            Configuration section = Config.REPORTS_BUTTONS_ELEMENTS.getSection();
            for (String key : section.getKeys()) {
                String name = Config.REPORTS_BUTTONS_ELEMENTS.getButtonText(key); //Button name
                String command = Config.REPORTS_BUTTONS_ELEMENTS.getButtonCommand(key); //Button command
                if (name == null || command == null) {
                    plugin.getLogger().info("The report button: " + key + " is not configured correctly!");
                    continue;
                }

                TextComponent component = new TextComponent(ChatUtils.format(name));
                String formattedCommand = command
                        .replace("%REPORTER%", player.getName())
                        .replace("%REPORTED%", target.getName())
                        .replace("%REASON%", reasonString)
                        .replace("%SERVER%", player.getServer().getInfo().getName());

                component.setClickEvent(new ClickEvent(action, formattedCommand));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/" + formattedCommand)));
                buttons.add(component);
            }

            //Check if inline
            if (Config.REPORTS_BUTTONS_IN_LINE.getBoolean()) {
                ComponentBuilder cb = new ComponentBuilder();
                for (TextComponent component : buttons) {
                    cb.append(component);
                }
                for (UUID staff : onlineStaff) {
                    ProxiedPlayer staffPlayer = plugin.getProxy().getPlayer(staff);
                    if (plugin.getPlayersManager().getPlayer(staffPlayer).isAlerts()) {
                        staffPlayer.sendMessage(cb.create());
                    }
                }
            } else {
                for (TextComponent component : buttons) {
                    for (UUID staff : onlineStaff) {
                        ProxiedPlayer staffPlayer = plugin.getProxy().getPlayer(staff);
                        if (plugin.getPlayersManager().getPlayer(staffPlayer).isAlerts()) {
                            staffPlayer.sendMessage(component);
                        }
                    }
                }
            }
        }

        player.sendMessage(ChatUtils.format(Config.REPORTS_MESSAGES_REPORT_SENT.getMessage()
                .replaceAll("%SUSPECT%", target.getName())));
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+Config.REPORTS_COOLDOWN.getLong()*1000);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase()) && !plugin.getPlayersManager().getPlayer(player).isStaff()) { //If start with that letter and isn't staff
                    players.add(player.getName());
                }
            }
            return players;
        } else {
            return Collections.emptyList();
        }
    }
}
