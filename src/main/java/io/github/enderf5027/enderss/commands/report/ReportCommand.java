package io.github.enderf5027.enderss.commands.report;

import io.github.enderf5027.enderss.Enderss;
import io.github.enderf5027.enderss.session.SessionManager;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.enderf5027.enderss.session.SessionManager.getSession;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class ReportCommand extends Command implements TabExecutor {
    public ReportCommand() {
        super("report", "enderss.report");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length==0) {
            p.sendMessage(format(config.noplayer));
            return;
        }

        ProxiedPlayer reported = ProxyServer.getInstance().getPlayer(args[0]);

        if (reported == null) { //If the player is offline, reported will be null
            p.sendMessage(format(config.playeroffline));
            return;
        }

        if (args.length == 1) { //No reason given
            p.sendMessage(format(config.noreason));
            return;
        }

        final StringBuilder sb = new StringBuilder(); //Merge the args into one single reason
        for (int index = 1; index < args.length; index++) {
            sb.append(args[index]).append(" ");
        }

        final String reason = sb.toString().trim();
        if (config.nostaffenabled) {
            if (!SessionManager.isStaffOnline()) {
                p.sendMessage(format(config.nostaff));
                return;
            }
        }

        for (String message : config.reportsent) { //Sends the message
            p.sendMessage(format(message
                    .replace("%SUSPECT%", reported.getDisplayName())
                    .replace("%REASON%", reason)
                    .replace("%SERVER%", reported.getServer().getInfo().getName())
            ));
        }

        TextComponent ssbutton = new TextComponent(format(config.ssbutton));
        ssbutton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/ss " + reported.getDisplayName())));
        ssbutton.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ss " + reported.getDisplayName()));

        TextComponent servertp = new TextComponent(format(config.servertpbutton));
        servertp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/server "+ reported.getServer().getInfo().getName())));
        servertp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/server "+ reported.getServer().getInfo().getName()));

        TextComponent banbutton = new TextComponent(format(config.hackbutton));
        banbutton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.blatant.replace("%SUSPECT%" ,reported.getDisplayName()))));
        banbutton.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.blatant.replace("%SUSPECT%" ,reported.getDisplayName())));

        ComponentBuilder builder = new ComponentBuilder();
        builder.append(ssbutton).append(new TextComponent(" ")).append(servertp).append(new TextComponent(" ")).append(banbutton);
        final BaseComponent[] buttons = builder.create();

        ProxyServer.getInstance().getScheduler().runAsync(Enderss.plugin, () -> {
           for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
               for (String message : config.reportreceived) {
                   if (getSession(staff).getAlertsEnabled() && getSession(staff).isStaff()) {
                       staff.sendMessage(format(message
                               .replace("%REPORTER%", p.getDisplayName())
                               .replace("%REPORTED%", reported.getDisplayName())
                               .replace("%REASON%", reason)
                               .replace("%SERVER%", reported.getServer().getInfo().getName())
                       ));
                   }

               }
               if (getSession(staff).getAlertsEnabled() && getSession(staff).isStaff()) {staff.sendMessage(buttons );}
           }
        });
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

