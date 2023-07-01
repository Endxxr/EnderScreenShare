package dev.endxxr.enderss.spigot.managers;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.events.spigot.SsStartEvent;
import dev.endxxr.enderss.api.objects.player.SpigotPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import dev.endxxr.enderss.spigot.utils.WorldUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class ScreenShareManager implements dev.endxxr.enderss.api.objects.managers.ScreenShareManager {

    private final EnderSS api = EnderSSProvider.getApi();


    // Freeze the player
    @Override
    public void startScreenShare(UUID staff, UUID suspect) {

        Player staffPlayer = Bukkit.getPlayer(staff);
        Player suspectPlayer = Bukkit.getPlayer(suspect);

        if (staffPlayer == null || suspectPlayer == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff or suspect is null!"), "Illegal use of API");
            return;
        }

        if (staff.equals(suspect)) {
            staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_YOURSELF.getMessage()));
            return;
        }

        if (!staffPlayer.hasPermission("enderss.staff") && !staffPlayer.hasPermission("enderss.ss")) {
            staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (suspectPlayer.hasPermission("enderss.exempt") || suspectPlayer.hasPermission("enderss.bypass")) {
            staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_EXEMPT.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }

        SpigotPlayer staffSS = (SpigotPlayer) api.getPlayersManager().getPlayer(staff);
        SpigotPlayer suspectSS = (SpigotPlayer) api.getPlayersManager().getPlayer(suspect);

        if (staffSS == null || suspectSS == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff or suspect is null!"), "Illegal use of API");
            return;
        }


        if (SpigotConfig.PROXY_MODE.getBoolean()) {
            startBackEndScreenShare(staffPlayer, suspectPlayer, staffSS, suspectSS);
            return;
        }

        if (suspectSS.isStaff()) {
            if (!GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CANNOT_SS_STAFF.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
                return;
            }
        }

        if (suspectSS.isFrozen()) { //The suspect is already being controlled
            staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_ALREADY_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }

        if (staffSS.getControlled()!=null) {
            if (staffSS.getControlled().equals(suspectSS)) { //The staffer is already controlling this player
                staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_ALREADY_SS_PLAYER.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            } else {
                staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_STAFF_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            }
            return;
        }

        if (SpigotConfig.TELEPORT_ON_START.getBoolean()) {
            staffPlayer.teleport(suspectPlayer);
        }

        if (SpigotConfig.START_ENABLED.getBoolean()) {
            staffPlayer.teleport(WorldUtils.getSpawnLocation(staffSS));
            suspectPlayer.teleport(WorldUtils.getSpawnLocation(suspectSS));
        }

        suspectSS.setFrozen(true);
        suspectSS.setStaffer(staffSS);
        staffSS.setControlled(suspect);

        if (GlobalConfig.START_TITLE_SEND.getBoolean()) { //Send the title to the suspect
            suspectPlayer.sendTitle(
                    ChatUtils.format(GlobalConfig.START_TITLE_TITLE.getString(), "%STAFF%", staffPlayer.getName()),
                    ChatUtils.format(GlobalConfig.START_TITLE_SUBTITLE.getString(), "%STAFF%", staffPlayer.getName()),
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

        suspectPlayer.sendMessage(ChatUtils.format(GlobalConfig.START_SS_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName()));

        if (GlobalConfig.START_ANYDESK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.format(GlobalConfig.START_ANYDESK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }
        if (GlobalConfig.START_TEAMSPEAK_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.format(GlobalConfig.START_TEAMSPEAK_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }
        if (GlobalConfig.START_DISCORD_SEND.getBoolean()) {
            suspectPlayer.sendMessage(ChatUtils.format(GlobalConfig.START_DISCORD_MESSAGE.getMessage()));
            suspectPlayer.sendMessage("");
        }


        //BUTTONS
        if (GlobalConfig.START_BUTTONS.getSection().getKeys(false).size() > 0) {
            sendScreenShareButtons(staffPlayer, suspectPlayer);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
            if (ssPlayer == null) {
                return;
            }

            if (ssPlayer.isStaff() && ssPlayer.hasAlerts()) {
                player.sendMessage(ChatUtils.format(GlobalConfig.START_STAFF_MESSAGE.getMessage(), "%STAFF%", staffPlayer.getName(), "%SUSPECT%", suspectPlayer.getName()));
            }
        }

        suspectPlayer.setWalkSpeed(0);

        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            suspectPlayer.getActivePotionEffects().forEach(effect -> suspectPlayer.removePotionEffect(effect.getType()));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {
            suspectSS.setLastGameMode((byte) suspectPlayer.getGameMode().getValue());
            suspectPlayer.setGameMode(GameMode.ADVENTURE);
        }


        Bukkit.getPluginManager().callEvent(new SsStartEvent(staffPlayer, suspectPlayer));
        api.getPlugin().getLog().info(staffPlayer.getName() + " is now controlling " + suspectPlayer.getName());

    }

    @Override
    public void clearPlayer(UUID target) {

        SpigotPlayer targetSS = (SpigotPlayer) api.getPlayersManager().getPlayer(target);
        Player targetPlayer = Bukkit.getPlayer(target);

        if (targetSS == null || targetPlayer == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Target or target player is null!"), "Illegal use of API");
            return;
        }


        targetSS.setStaffer(null);
        targetSS.setFrozen(false);

        if (targetSS.isStaff() && !targetSS.isFrozen() && targetSS.getControlled()!=null) {
            targetSS.setControlled(null);
            return;
        }


        if (!SpigotConfig.PROXY_MODE.getBoolean()) { // Prevents double message in proxy mode
            for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
                if (online.isStaff() && online.hasAlerts()) {
                    Bukkit.getPlayer(online.getUUID()).sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                            "%STAFF%", "Console",
                            "%SUSPECT%", targetPlayer.getName()));
                }
            }
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {
            targetPlayer.setGameMode(GameMode.getByValue(targetSS.getLastGameMode()));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            targetSS.getLastPotionEffects().forEach(targetPlayer::addPotionEffect);
        }

        if (SpigotConfig.FALLBACK_ENABLED.getBoolean()) {
            targetPlayer.teleport(WorldUtils.getFallbackLocation(targetSS));
        }

        targetPlayer.setWalkSpeed(0.2f);
        api.getPlugin().getLog().info("Player " + targetPlayer.getName() + " is now free");
        
    }

    @Override
    public void clearPlayer(UUID staff, UUID suspect) {

        Player staffPlayer = Bukkit.getPlayer(staff);
        Player suspectPlayer = Bukkit.getPlayer(suspect);
        SsPlayer ssStaff = api.getPlayersManager().getPlayer(staff);
        SpigotPlayer ssSuspect = (SpigotPlayer) api.getPlayersManager().getPlayer(suspect);

        if (staffPlayer == null || suspectPlayer == null || ssStaff == null || ssSuspect == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Staff, suspect, staff ss or suspect ss is null!"), "Illegal use of API");
            return;
        }



        if (SpigotConfig.PROXY_MODE.getBoolean()) {
            endBackEndControl(staffPlayer, suspectPlayer, ssStaff, ssSuspect);
            return;
        }

        if (!ssSuspect.isFrozen()) {
            staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_SUSPECT_NOT_IN_SS.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }

        // If the staff is trying to clear a player that is not controlled by him
        if (!staffPlayer.hasPermission("enderss.admin") && !ssStaff.getControlled().equals(ssSuspect)) {
            staffPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NOT_CONTROLLING.getMessage(), "%SUSPECT%", suspectPlayer.getName()));
            return;
        }
        ssStaff.setControlled(null);
        ssSuspect.setStaffer(null);
        ssSuspect.setFrozen(false);


        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {
            suspectPlayer.setGameMode(GameMode.getByValue(ssSuspect.getLastGameMode()));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            ssSuspect.getLastPotionEffects().forEach(suspectPlayer::addPotionEffect);
        }

        for (SsPlayer online : api.getPlayersManager().getRegisteredPlayers()) {
            if (online.isStaff() && online.hasAlerts()) {
                Bukkit.getPlayer(online.getUUID()).sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", staffPlayer.getName(),
                        "%SUSPECT%", suspectPlayer.getName()));
            }
        }

        if (SpigotConfig.FALLBACK_ENABLED.getBoolean()) {
            staffPlayer.teleport(WorldUtils.getFallbackLocation(ssStaff));
            suspectPlayer.teleport(WorldUtils.getFallbackLocation(ssSuspect));
        }

        suspectPlayer.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_INFO_CONTROL_ENDED.getMessage(),
                "%STAFF%", staffPlayer.getName(),
                "%SUSPECT%", suspectPlayer.getName()));

        suspectPlayer.setWalkSpeed(0.2f);
        api.getPlugin().getLog().info(staffPlayer.getName()+" has freed "+suspectPlayer.getName());
    }



    private void startBackEndScreenShare(Player staff, Player suspect, SpigotPlayer staffSS, SpigotPlayer suspectSS) {
        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            Collection<PotionEffect> effectsCollection = suspect.getActivePotionEffects();
            suspectSS.setLastPotionEffects(effectsCollection);
            effectsCollection.forEach((potionEffect ->  suspect.removePotionEffect(potionEffect.getType())));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {
            suspect.setGameMode(GameMode.ADVENTURE);
        }

        if (SpigotConfig.TELEPORT_ON_START.getBoolean()) {
            staff.teleport(suspect);
        }

        if (SpigotConfig.START_ENABLED.getBoolean()) {
            staff.teleport(WorldUtils.getSpawnLocation(staffSS));
            suspect.teleport(WorldUtils.getSpawnLocation(suspectSS));
        }

        suspectSS.setFrozen(true);
        suspectSS.setStaffer(staffSS);
        staffSS.setControlled(suspect.getUniqueId());

    }


    private void endBackEndControl(Player staffPlayer, Player suspectPlayer, SsPlayer staffSS, SpigotPlayer suspectSS) {

        if (!suspectSS.isFrozen()) {
            return;
        }

        // If the staff is trying to clear a player that is not controlled by him
        if (!staffPlayer.hasPermission("enderss.admin") && !staffSS.getControlled().equals(suspectSS)) {
            return;
        }
        staffSS.setControlled(null);
        suspectSS.setStaffer(null);
        suspectSS.setFrozen(false);


        if (SpigotConfig.PROTECTIONS_PLAYER_ADVENTURE_MODE.getBoolean()) {
            suspectPlayer.setGameMode(GameMode.getByValue(suspectSS.getLastGameMode()));
        }

        if (SpigotConfig.PROTECTIONS_PLAYER_REMOVE_EFFECTS.getBoolean()) {
            suspectSS.getLastPotionEffects().forEach(suspectPlayer::addPotionEffect);
        }

        if (SpigotConfig.FALLBACK_ENABLED.getBoolean()) {
            staffPlayer.teleport(WorldUtils.getFallbackLocation(staffSS));
            suspectPlayer.teleport(WorldUtils.getFallbackLocation(suspectSS));
        }


        suspectPlayer.setWalkSpeed(0.2f);
        api.getPlugin().getLog().info(staffPlayer.getName()+" has freed "+suspectPlayer.getName());

    }

    private void sendScreenShareButtons(Player staffPlayer, Player suspectPlayer) {
        // Text - Action
        HashMap<String, String> stringButtons = GlobalConfig.getScreenShareButtons(suspectPlayer.getDisplayName());
        List<TextComponent> buttons = new ArrayList<>();

        ClickEvent.Action action = GlobalConfig.BUTTONS_CONFIRM_BUTTONS.getBoolean() ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        for (String key : stringButtons.keySet()) {
            TextComponent button = new TextComponent(key);
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(stringButtons.get(key))}));
            button.setClickEvent(new ClickEvent(action, stringButtons.get(key)));
            buttons.add(button);
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


}
