package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.utils.ChatUtils;

public class ReloadCommand implements VelocitySubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.reload";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        long start = System.currentTimeMillis();
        EnderSSProvider.getApi().getPlugin().reload();
        EnderSSProvider.getApi().getPlugin().getLog()
                .info("Config reloaded in " + (System.currentTimeMillis() - start) + "ms");
        sender.sendMessage(ChatUtils.formatAdventureComponent("Config reloaded in " + (System.currentTimeMillis() - start) + "ms"));

    }
}
