package dev.endxxr.enderss.spigot.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ControlsMessageListener implements PluginMessageListener {

    EnderPlugin plugin = EnderSSAPI.Provider.getApi().getPlugin();

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, byte @NotNull [] bytes) {

        if (!s.equals("ender:controls")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();

        PluginMessageType type;
        try {
            type = PluginMessageType.valueOf(subChannel);
        } catch (IllegalArgumentException e) {
            return;
        }

        switch (type) {
            case START:
                startSpigotScreenShare(in);
                break;

            case END:
                stopSpigotScreenShare(in);
                break;

            case RELOAD:
                plugin.getLog().warning("Got a reload message from the proxy");
                plugin.getLog().warning("Reloading the plugin...");
                plugin.reload();
                break;

        }
    }

    private void stopSpigotScreenShare(ByteArrayDataInput in) {
    }

    private void startSpigotScreenShare(ByteArrayDataInput in) {
        String staffUUID = in.readUTF();
        String suspectUUID = in.readUTF();

        Player staff = Bukkit.getPlayer(staffUUID);
        Player suspect = Bukkit.getPlayer(suspectUUID);


        if (staff == null || suspect == null) return;

        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            suspect.getActivePotionEffects().forEach(effect -> suspect.removePotionEffect(effect.getType()));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {

            suspect.setGameMode(GameMode.ADVENTURE);
        }


    }
}
