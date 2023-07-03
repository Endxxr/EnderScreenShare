package dev.endxxr.enderss.spigot.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ControlsMessageListener implements PluginMessageListener {

    private final EnderPlugin plugin = EnderSSProvider.getApi().getPlugin();

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, byte @NotNull [] bytes) {

        if (!s.equals(plugin.CHANNEL_NAME)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();

        PluginMessageType type;
        try {
            type = PluginMessageType.valueOf(subChannel.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLog().warning("Got an invalid message from the proxy");
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

        UUID staffUUID = UUID.fromString(in.readUTF());
        UUID suspectUUID;

        try {
            suspectUUID = UUID.fromString(in.readUTF());
        } catch (Exception e) {
            suspectUUID = null;
        }

        Player staff = Bukkit.getPlayer(staffUUID);
        Player suspect = Bukkit.getPlayer(suspectUUID);

        ScreenShareManager manager = EnderSSProvider.getApi().getScreenShareManager();


        if (suspect == null && !staff.hasPermission("enderss.staff")) { // Staff quits
            manager.clearPlayer(staffUUID);
            return;
        }

        if (suspect == null && staff.hasPermission("enderss.staff")) { //
            manager.clearPlayer(staffUUID);
            return;
        }

        if (staff == null) {
            return; //what
        }

        manager.clearPlayer(staffUUID, suspectUUID);
    }

    private void startSpigotScreenShare(ByteArrayDataInput in) {
        EnderSSProvider.getApi().getPlugin().runTaskLater(() -> {
            String staffUUID = in.readUTF();
            String suspectUUID = in.readUTF();

            Player staff = Bukkit.getPlayer(UUID.fromString(staffUUID));
            Player suspect = Bukkit.getPlayer(UUID.fromString(suspectUUID));


            if (staff == null || suspect == null) return;

            EnderSSProvider.getApi().getScreenShareManager().startScreenShare(staff.getUniqueId(), suspect.getUniqueId());

        }, 20);
    }
}
