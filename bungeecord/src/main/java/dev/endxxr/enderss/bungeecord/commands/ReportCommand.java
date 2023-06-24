package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;


public class ReportCommand extends Command implements TabExecutor {

    private final EnderSS api;
    private final HashMap<UUID, Long> coolDowns = new HashMap<>();
    public ReportCommand() {
        super("report", "enderss.report");
        this.api = EnderSSProvider.getApi();

    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        //CHECKS
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("enderss.staff") && !player.hasPermission("enderss.report")) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (coolDowns.containsKey(player.getUniqueId())) {
            if (coolDowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage(BungeeChat.formatComponent(GlobalConfig.REPORTS_MESSAGES_WAIT.getMessage(), "%SECONDS%", String.valueOf((coolDowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)));
                return;
            }
        }

        if (args.length == 0) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target==player) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF.getMessage()));
            return;
        }

        if (target==null){
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }

        SsPlayer targetSS = api.getPlayersManager().getPlayer(target.getUniqueId());
        if (targetSS==null) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }

        if (targetSS.isStaff()) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.REPORTS_MESSAGES_CANNOT_REPORT_STAFF.getMessage()));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_REASON.getMessage()));
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
                player.sendMessage(BungeeChat.formatComponent(GlobalConfig.REPORTS_NO_STAFF.getMessage()));
                return;
            }
        }

        //STAFF
        api.getPlayersManager().broadcastStaff(ChatUtils.format(GlobalConfig.REPORTS_MESSAGES_REPORT_RECEIVED.getMessage()
                .replace("%REPORTER%", player.getName())
                .replace("%REPORTED%", target.getName())
                .replace("%REASON%", reasonString)
                .replace("%SERVER%", player.getServer().getInfo().getName())));

        //BUTTONS;
        if (GlobalConfig.REPORTS_BUTTONS_ELEMENTS.getSection().getKeys(false).size() > 0) {
            sendButtons(api.getPlayersManager().getStaffers().keySet(), player, target, reasonString);
        }

        player.sendMessage(BungeeChat.formatComponent(GlobalConfig.REPORTS_MESSAGES_REPORT_SENT.getMessage()
                .replaceAll("%SUSPECT%", target.getName())));
        coolDowns.put(player.getUniqueId(), System.currentTimeMillis()+ GlobalConfig.REPORTS_COOLDOWN.getLong()*1000);
    }

    private void sendButtons(Set<UUID> onlineStaff, ProxiedPlayer player, ProxiedPlayer target, String reasonString) {

        HashMap<String, String> stringButtons = GlobalConfig.getReportButtons(player.getName(),
                target.getName(),
                reasonString,
                player.getServer().getInfo().getName());

        List<TextComponent> buttons = BungeeChat.buildButtons(stringButtons, GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean());

        //Check if inline
        if (GlobalConfig.REPORTS_BUTTONS_IN_LINE.getBoolean()) {
            ComponentBuilder cb = new ComponentBuilder();
            for (TextComponent component : buttons) {
                cb.append(component);
            }
            for (UUID uuid : onlineStaff) {

                ProxiedPlayer staffPlayer = ProxyServer.getInstance().getPlayer(uuid);
                SsPlayer staff = api.getPlayersManager().getPlayer(uuid);

                if (staff != null && staff.hasAlerts()) {
                    staffPlayer.sendMessage(cb.create());
                }
            }
        } else {
            for (TextComponent component : buttons) {
                for (UUID uuid : onlineStaff) {

                    ProxiedPlayer staffPlayer = ProxyServer.getInstance().getPlayer(uuid);
                    SsPlayer staff = api.getPlayersManager().getPlayer(uuid);

                    if (staff != null && staff.hasAlerts()) {
                        staffPlayer.sendMessage(component);
                    }
                }
            }
        }
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList<>();
        String prefix = args.length == 0 ? "" : args[0];
        if (args.length>1) return Collections.emptyList();
        for (SsPlayer ss : api.getPlayersManager().getRegisteredPlayers() ) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(ss.getUUID());
            if (player.getName().toLowerCase().startsWith(prefix) && !ss.isStaff()) { //If start with that letter and isn't staff
                players.add(player.getName());
            }
        }
        return players;
    }
}
