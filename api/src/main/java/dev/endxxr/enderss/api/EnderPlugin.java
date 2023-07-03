package dev.endxxr.enderss.api;


import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import net.luckperms.api.LuckPerms;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.logging.Logger;

public interface EnderPlugin {

    String CHANNEL_NAME = "enderss:controls";

    void runTaskAsync(Runnable runnable);
    void runTaskTimer(Runnable runnable, int delay, int frequency);
    void runTaskLater(Runnable runnable, int delay);
    void dispatchCommand(SsPlayer sender, String command);
    void reload();
    void sendPluginMessage(SsPlayer staffer, SsPlayer suspect, PluginMessageType type);
    void sendPluginMessage(SsPlayer staffer, PluginMessageType type);
    Platform getPlatform();
    YamlFile getGeneralConfig();
    YamlFile getPlatformConfig();
    Logger getLog();
    LuckPerms getLuckPermsAPI();
    boolean isLuckPermsPresent();
    boolean isLiteBansPresent();
}
