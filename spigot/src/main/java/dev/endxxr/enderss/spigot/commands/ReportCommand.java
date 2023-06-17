package dev.endxxr.enderss.spigot.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;


public class ReportCommand implements CommandExecutor, TabExecutor {

    private final EnderSS api;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    public ReportCommand() {
        this.api = EnderSSProvider.getApi();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //CHECKS
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return true;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("enderss.staff") && !player.hasPermission("enderss.report")) {
            player.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return true;
        }

        if (cooldowns.containsKey(player.getUniqueId())) {
            if (cooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_WAIT.getMessage(), "%SECONDS%", String.valueOf((cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)));
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target==player) {
            player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF.getMessage()));
            return true;
        }

        if (target==null){
            player.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return true;
        }

        if (api.getPlayersManager().getPlayer(target.getUniqueId()).isStaff()) {
            player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_STAFF.getMessage()));
            return true;
        }

        if (args.length == 1) {
            player.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_REASON.getMessage()));
            return true;
        }

        //MESSAGES
        final StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        final String reasonString = reason.toString().trim();
        if (GlobalConfig.REPORTS_NO_STAFF_ENABLED.getBoolean()) {
            if (!api.getPlayersManager().isStaffOnline()) {
                player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_NO_STAFF.getMessage()));
                return true;
            }
        }

        //STAFF
        Set<UUID> onlineStaff = api.getPlayersManager().getStaffers().keySet();
        for (UUID uuid : onlineStaff) {
            SsPlayer staff = api.getPlayersManager().getPlayer(uuid);
            if (staff.hasAlerts()) {
                String message = ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_REPORT_RECEIVED.getMessage()
                        .replace("%REPORTER%", player.getName())
                        .replace("%REPORTED%", target.getName())
                        .replace("%REASON%", reasonString));
                Player staffPlayer = Bukkit.getPlayer(uuid);
                staffPlayer.sendMessage(message);
            }
        }

        //BUTTONS; //reports.buttons.x.y
        if (GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getSection().getKeys(false).size() > 0) {

            List<TextComponent> buttons = getButtons(player.getName(), target.getName(), reasonString);

            //Check if inline
            if (GlobalConfig.REPORTS_BUTTONS_IN_LINE.getBoolean()) {
                ComponentBuilder cb = new ComponentBuilder("");
                for (TextComponent component : buttons) {
                    cb.append(component);
                }
                for (UUID uuid : onlineStaff) {
                    Player staffPlayer = Bukkit.getPlayer(uuid);
                    if (api.getPlayersManager().getPlayer(uuid).hasAlerts()) {
                        staffPlayer.spigot().sendMessage(cb.create());
                    }
                }
            } else {
                for (TextComponent component : buttons) {
                    for (UUID uuid : onlineStaff) {
                        Player staffPlayer = Bukkit.getPlayer(uuid);
                        if (api.getPlayersManager().getPlayer(uuid).hasAlerts()) {
                            staffPlayer.spigot().sendMessage(component);
                        }
                    }
                }
            }
        }

        player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_REPORT_SENT.getMessage()
                .replaceAll("%SUSPECT%", target.getName())));
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+ GlobalConfig.REPORTS_COOLDOWN.getLong()*1000);
        return true;
    }

    private List<TextComponent> getButtons(String reporterName, String reportedName, String reason) {
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

            TextComponent component = new TextComponent(ChatUtils.format(name));
            String formattedCommand = "/"+command
                    .replace("%REPORTER%", reporterName)
                    .replace("%REPORTED%", reportedName)
                    .replace("%REASON%", reason);

            component.setClickEvent(new ClickEvent(action, formattedCommand));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(formattedCommand)}));
            buttons.add(component);
        }
        return buttons;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            for (SsPlayer ss : api.getPlayersManager().getRegisteredPlayers() ) {
                Player player = Bukkit.getPlayer(ss.getUUID());
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase()) && !ss.isStaff()) { //If start with that letter and isn't staff
                    players.add(player.getName());
                }
            }
            return players;
        } else {
            return Collections.emptyList();
        }
    }
}
