package io.github.enderf5027.enderss.commands.ss;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static io.github.enderf5027.enderss.session.SessionManager.getSession;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;
import static io.github.enderf5027.enderss.utils.ChatUtils.sendListMessage;

public class SsCommand extends Command {

    public SsCommand() {
        super("ss", "enderss.staff", "screenshare", "freeze", "controllo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            ProxyServer.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', config.console));
            return;

        }
        ProxiedPlayer staff = (ProxiedPlayer) sender;
        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage((format(config.noplayer)));
            return;
        }
        ProxiedPlayer sus = ProxyServer.getInstance().getPlayer(args[0]);
        if (sus == null) { //If the player is offline, sus will be null
            sender.sendMessage(format(config.playeroffline));
            return;
        }

        PlayerSession susSession = getSession(sus);
        PlayerSession staffSession = getSession(staff);

        if (staff.equals(sus)) { //Staff is trying to control themselves
            staff.sendMessage(format(config.cantcontrolyourself));
            return;
        }

        if (susSession.isStaff()) { //You can't control a staff member, why you would do that + it might create some conflicts with the placeholders
            staff.sendMessage(format(config.cantcontrolstaff));
            return;
        }

        if (susSession.isFrozen() && susSession.getStaffer() == staff) { //The staffer is already controlling this player
            staff.sendMessage(format(config.alreadyssplayer, sus));
            return;
        }
        if (susSession.isFrozen()) { //The suspect is already being controlled
            staff.sendMessage(format(config.alreadyinss, sus));
            return;
        }
        if (staffSession.getScreenSharing() != null) { //The staff is already controlling someone
            staff.sendMessage(format(config.staffinss, sus));
        }

        ServerInfo ss = ProxyServer.getInstance().getServerInfo(config.ScreenShareServer); //Gets the screenshare server from the config

        sus.connect(ss);
        staff.connect(ss);
        susSession.setFrozen(true);
        susSession.setStaffer(staff);
        staffSession.setPlayerScreenShared(sus);

        if (config.sendTitle) { //Send the title to the suspect
            ProxyServer.getInstance().createTitle()
                    .title(new TextComponent(format(config.title)))
                    .subTitle(new TextComponent(format(config.subtitle)))
                    .fadeIn(config.fadein)
                    .stay(config.stay)
                    .fadeOut(config.fadeout)
                    .send(sus);
        }
        if (config.clearchat) { //Clears the chat
            for (int i = 0; i < 300; i++) {
                sus.sendMessage("");
            }
        }
        for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
            if (getSession(receiver).isStaff()) {
                sendListMessage(config.staffmessage, staff, sus, receiver);
            }
        }
        sendListMessage(config.ssmessage, staff, sus, sus);

        if (config.sendAnydesk) {
            sus.sendMessage(format(config.anydesk));
            sus.sendMessage("");
        }
        if (config.sendTeamspeak) {
            sus.sendMessage(format(config.teamspeak));
            sus.sendMessage("");
        }
        if (config.sendDiscord) {
            sus.sendMessage(format(config.discord));
            sus.sendMessage("");
        }

        TextComponent hack = new TextComponent(format(config.bhack));
        hack.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.hack.replace("%SUSPECT%", sus.getName()))));

        TextComponent admission = new TextComponent(format(config.badmission));
        admission.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.admission.replace("%SUSPECT%", sus.getName()))));

        TextComponent refuse = new TextComponent(format(config.brefuse));
        refuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.refuse.replace("%SUSPECT%", sus.getName()))));

        TextComponent quit = new TextComponent(format(config.bquit));
        quit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.quit.replace("%SUSPECT%", sus.getName()))));

        TextComponent clean = new TextComponent(format(config.bclean));
        clean.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/clean " + sus.getName())));

        if (config.confirmbutton) {
            hack.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.hack.replace("%SUSPECT%", sus.getName())));
            admission.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.admission.replace("%SUSPECT%", sus.getName())));
            refuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.refuse.replace("%SUSPECT%", sus.getName())));
            quit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.quit.replace("%SUSPECT%", sus.getName())));
            clean.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clean " + sus.getName()));
        } else {
            hack.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.hack.replace("%SUSPECT%", sus.getName())));
            admission.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.admission.replace("%SUSPECT%", sus.getName())));
            refuse.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.refuse.replace("%SUSPECT%", sus.getName())));
            quit.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, config.quit.replace("%SUSPECT%", sus.getName())));
            clean.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clean " + sus.getName()));
        }

        staff.sendMessage(hack);
        staff.sendMessage(admission);
        staff.sendMessage(refuse);
        staff.sendMessage(quit);
        staff.sendMessage(clean);

    }

}

