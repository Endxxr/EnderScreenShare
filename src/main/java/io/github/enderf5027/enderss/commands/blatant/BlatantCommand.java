package io.github.enderf5027.enderss.commands.blatant;

import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static io.github.enderf5027.enderss.session.SessionManager.getSession;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class BlatantCommand extends Command {
    public BlatantCommand() {
        super("blatant", "enderss.blatant");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!(getSession(player).isStaff())) {
            player.sendMessage(format(config.noperm));
            return;
        }
        if (args.length == 0) {
            player.sendMessage(format(config.noplayer));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target==null){
            player.sendMessage(format(config.playeroffline));
            return;
        }

        TextComponent confirm = new TextComponent(format(config.bconfirm));
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.blatant.replace("%SUSPECT%", target.getName())));
        player.sendMessage(confirm);



    }
}
