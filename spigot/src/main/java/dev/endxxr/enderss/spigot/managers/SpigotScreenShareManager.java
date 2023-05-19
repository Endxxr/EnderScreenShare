package dev.endxxr.enderss.spigot.managers;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.events.spigot.SsStartEvent;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.spigot.utils.SpigotChat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class SpigotScreenShareManager implements ScreenShareManager {

    private final EnderSSAPI api = EnderSSAPI.Provider.getApi();
    private final Map<String, TextComponent> premadeButtons = new HashMap<>();

    public SpigotScreenShareManager() {


        TextComponent hack = new TextComponent(SpigotChat.format(GlobalConfig.BUTTONS_HACK.getString()));
        TextComponent admission = new TextComponent(SpigotChat.format(GlobalConfig.BUTTONS_ADMISSION.getString()));
        TextComponent refuse = new TextComponent(SpigotChat.format(GlobalConfig.BUTTONS_REFUSE.getString()));
        TextComponent quit = new TextComponent(SpigotChat.format(GlobalConfig.BUTTONS_QUIT.getString()));
        TextComponent clean = new TextComponent(SpigotChat.format(GlobalConfig.BUTTONS_CLEAN.getString()));

        premadeButtons.put("hack", hack);
        premadeButtons.put("refuse", refuse);
        premadeButtons.put("admission", admission);
        premadeButtons.put("quit", quit);
        premadeButtons.put("clean", clean);


    }


    // Freeze the player
    @Override
    public void startScreenShare(UUID staff, UUID suspect) {

        Player staffPlayer = Bukkit.getPlayer(staff);
        Player suspectPlayer = Bukkit.getPlayer(suspect);

        if (staffPlayer == null || suspectPlayer == null) return;

        if (staff.equals(suspect)) {
            staffPlayer.sendMessage(SpigotChat.format(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (suspectPlayer.hasPermission("enderss.exempt") || suspectPlayer.hasPermission("enderss.bypass")) {
            staffPlayer.sendMessage(SpigotChat.format(GlobalConfig.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }

        SSPlayer staffSS = api.getPlayersManager().getPlayer(staff);
        SSPlayer suspectSS = api.getPlayersManager().getPlayer(suspect);

        if (suspectSS.isStaff()) {
            if (!GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                staffPlayer.sendMessage(SpigotChat.format(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
                return;
            }
        }

        if (suspectSS.isFrozen()) { //The suspect is already being controlled
            staffPlayer.sendMessage(SpigotChat.format(GlobalConfig.MESSAGES_ERROR_ALREADY_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }

        if (staffSS.getControlled()!=null) {
            if (staffSS.getControlled().equals(suspectSS)) { //The staffer is already controlling this player
                staffPlayer.sendMessage(SpigotChat.format(GlobalConfig.MESSAGES_ERROR_ALREADY_SS_PLAYER.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            } else {
                staffPlayer.sendMessage(SpigotChat.format(GlobalConfig.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            }
            return;
        }


        if (SpigotConfig.TELEPORT_ON_START.getBoolean()) {
            staffPlayer.teleport(suspectPlayer);
        }

        if (SpigotConfig.SPAWN_ENABLED.getBoolean()) {

            String[] staffSpawn = SpigotConfig.SPAWN_STAFF.getString().split(",");
            String[] suspectSpawn = SpigotConfig.SPAWN_SUSPECT.getString().split(",");

            Location staffSpawnLocation = new Location(Bukkit.getWorld(staffSpawn[5]), Double.parseDouble(staffSpawn[0]), Double.parseDouble(staffSpawn[1]), Double.parseDouble(staffSpawn[2]), Float.parseFloat(staffSpawn[3]), Float.parseFloat(staffSpawn[4]));
            Location suspectSpawnLocation = new Location(Bukkit.getWorld(suspectSpawn[5]), Double.parseDouble(suspectSpawn[0]), Double.parseDouble(suspectSpawn[1]), Double.parseDouble(suspectSpawn[2]), Float.parseFloat(suspectSpawn[3]), Float.parseFloat(suspectSpawn[4]));

            staffPlayer.teleport(staffSpawnLocation);
            suspectPlayer.teleport(suspectSpawnLocation);

        }

        suspectSS.setFrozen(true);
        suspectSS.setStaffer(staffSS);
        staffSS.setControlled(suspect);

        if (GlobalConfig.START_TITLE_SEND.getBoolean()) { //Send the title to the suspect
            suspectPlayer.sendTitle(
                    SpigotChat.format(GlobalConfig.START_TITLE_TITLE.getMessage(), "%STAFF%", staffPlayer.getName()),
                    SpigotChat.format(GlobalConfig.START_TITLE_SUBTITLE.getMessage(), "%STAFF%", staffPlayer.getName()),
                    GlobalConfig.START_TITLE_FADEIN.getInt(),
                    GlobalConfig.START_TITLE_STAY.getInt(),
                    GlobalConfig.START_TITLE_FADEOUT.getInt()
            );
        }

        if (GlobalConfig.START_CLEAR_CHAT.getBoolean()) { //Clears the chat
            for (int i = 0; i < 300; i++) {
                suspectPlayer.sendMessage("");
            }
        }

        suspectPlayer.sendMessage(SpigotChat.format(GlobalConfig.START_SS_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName()));

        if (GlobalConfig.START_ANYDESK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(SpigotChat.format(GlobalConfig.START_ANYDESK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }
        if (GlobalConfig.START_TEAMSPEAK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(SpigotChat.format(GlobalConfig.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }
        if (GlobalConfig.START_DISCORD_SEND.getBoolean()) {
            suspectPlayer.sendMessage(SpigotChat.format(GlobalConfig.START_DISCORD_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }



        //BUTTONS
        if (GlobalConfig.START_BUTTONS.getSection().getKeys(false).size() > 0) {
            List<TextComponent> buttons = new ArrayList<>();
            ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
            for (String button : GlobalConfig.START_BUTTONS_ELEMENTS.getSection().getKeys(false)) {
                String type = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonType(button).toLowerCase();
                if (premadeButtons.containsKey(type)) { //Checks if the button is a premade button
                    TextComponent component = premadeButtons.get(type);
                    String command;
                    if (type.equalsIgnoreCase("clean")) {
                        command = "/clean " + suspectPlayer.getName();
                    } else {
                        command = GlobalConfig.valueOf("BAN_COMMAND_" + type.toUpperCase()).getString().replace("%SUSPECT%", suspectPlayer.getName());
                    }
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(command)}));
                    component.setClickEvent(new ClickEvent(action, command));
                    buttons.add(component);

                } else {
                    String text = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonText(button);
                    String command = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonCommand(button).replace("%SUSPECT%", suspectPlayer.getName());
                    TextComponent component = new TextComponent(SpigotChat.format(text));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(command)}));
                    component.setClickEvent(new ClickEvent(action, command));
                    buttons.add(component);
                }
            }
            if (buttons.size() > 0) {
                if (GlobalConfig.START_BUTTONS_IN_LINE.getBoolean()) {
                    ComponentBuilder builder = new ComponentBuilder("");
                    for (TextComponent component : buttons) {
                        builder.append(component);
                    }
                    staffPlayer.spigot().sendMessage(builder.create());
                } else {
                    for (TextComponent component : buttons) {
                        staffPlayer.spigot().sendMessage(component);
                    }
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (api.getPlayersManager().getPlayer(player.getUniqueId()).isStaff() && api.getPlayersManager().getPlayer(player.getUniqueId()).isAlerts()) {
                player.sendMessage(SpigotChat.format(GlobalConfig.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName(), "%SUSPECT%", suspectPlayer.getName()));
            }
        }

        suspectPlayer.setWalkSpeed(0);

        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            suspectPlayer.getActivePotionEffects().forEach(effect -> suspectPlayer.removePotionEffect(effect.getType()));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {
            suspectSS.setLastMode((byte) suspectPlayer.getGameMode().getValue());
            suspectPlayer.setGameMode(GameMode.ADVENTURE);
        }


        Bukkit.getPluginManager().callEvent(new SsStartEvent(staffPlayer, suspectPlayer));
        api.getPlugin().getLog().info(staffPlayer.getName() + " is now controlling " + suspectPlayer.getName());

    }

    @Override
    public void clearPlayer(UUID target) {

    }

    @Override
    public void clearPlayer(UUID staff, UUID suspect) {

    }
}
