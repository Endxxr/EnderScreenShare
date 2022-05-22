package io.github.enderf5027.enderss.commands.EnderSsCommand;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.session.SessionManager;
import io.github.enderf5027.enderss.utils.SubCommand;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class AlertsCommand extends SubCommand {
    @Override
    public String getName() {
        return "alerts";
    }

    @Override
    public String getDescription() {
        return "Disable alerts";
    }

    @Override
    public String getSyntax() {
        return "/enderss alerts";
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        PlayerSession session = SessionManager.getSession(player);
        if (session.getAlertsEnabled()) { //Disabled the alerts
            session.setAlerts(false);
            player.sendMessage(format(config.alertsDisabled));
        } else {
            session.setAlerts(true);
            player.sendMessage(format(config.alertsEnabled));
        }
    }
}
