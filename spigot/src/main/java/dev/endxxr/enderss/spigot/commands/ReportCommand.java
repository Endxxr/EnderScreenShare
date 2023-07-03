package dev.endxxr.enderss.spigot.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.spigot.utils.SpigotChat;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

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


        if (target==null){
            player.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return true;
        }

        if (target==player) {
            player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF.getMessage()));
            return true;
        }

        SsPlayer targetSS = api.getPlayersManager().getPlayer(target.getUniqueId());

        if (targetSS==null){
            player.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return true;
        }


        if (targetSS.isStaff()) {
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
            if (api.getPlayersManager().isStaffOffline()) {
                player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_NO_STAFF.getMessage()));
                return true;
            }
        }

        //STAFF

        api.getPlayersManager().broadcastStaff(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_REPORT_RECEIVED.getMessage()
                        .replace("%REPORTER%", player.getName())
                        .replace("%REPORTED%", target.getName())
                        .replace("%REASON%", reasonString)));

        //BUTTONS; //reports.buttons.x.y
        if (GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getSection().getKeys(false).size() > 0) {
            Set<UUID> onlineStaff = api.getPlayersManager().getStaffers().keySet();
            sendButtons(onlineStaff, player, target, reasonString);
        }

        player.sendMessage(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_REPORT_SENT.getMessage()
                .replaceAll("%SUSPECT%", target.getName())));
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+ GlobalConfig.REPORTS_COOLDOWN.getLong()*1000);
        return true;
    }

    private void sendButtons(Set<UUID> onlineStaff, Player player, Player target, String reasonString) {

        HashMap<String, String> stringButtons = GlobalConfig.getReportButtons(player.getName(),
                target.getName(),
                reasonString,
                Bukkit.getServerName());

        List<TextComponent> buttons = SpigotChat.buildButtons(stringButtons, GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean());

        //Check if inline
        if (GlobalConfig.REPORTS_BUTTONS_IN_LINE.getBoolean()) {
            ComponentBuilder cb = new ComponentBuilder("");
            for (TextComponent component : buttons) {
                cb.append(component);
            }
            for (UUID uuid : onlineStaff) {

                Player staffPlayer = Bukkit.getPlayer(uuid);
                SsPlayer staff = api.getPlayersManager().getPlayer(uuid);

                if (staff != null && staff.hasAlerts()) {
                    staffPlayer.spigot().sendMessage(cb.create());
                }
            }
        } else {
            for (TextComponent component : buttons) {
                for (UUID uuid : onlineStaff) {

                    Player staffPlayer = Bukkit.getPlayer(uuid);
                    SsPlayer staff = api.getPlayersManager().getPlayer(uuid);

                    if (staff != null && staff.hasAlerts()) {
                        staffPlayer.spigot().sendMessage(component);
                    }
                }
            }
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    String prefix = args.length == 0 ? "" : args[0];
    List<String> players = new ArrayList<>();

    if (args.length>1) return Collections.emptyList();

    for (SsPlayer ss : api.getPlayersManager().getRegisteredPlayers() ) {
        Player player = Bukkit.getPlayer(ss.getUUID());
        if (player.getName().toLowerCase().startsWith(prefix) && !ss.isStaff()) { //If start with that letter and isn't staff
            players.add(player.getName());
        }
    }
    return players;
    }
}
