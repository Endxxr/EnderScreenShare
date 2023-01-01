package me.endxxr.enderss.scoreboard;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SsScoreboard {

    private final EnderSS plugin;
    private final Random random;
    private final UUID staff;
    private final UUID suspect;
    private final AtomicInteger time;
    private int taskID;

    public SsScoreboard(ProxiedPlayer staff, ProxiedPlayer suspect, EnderSS plugin, Random random) {

        this.plugin = plugin;

        this.staff = staff.getUniqueId();
        this.suspect = suspect.getUniqueId();

        this.random = new Random();
        this.time = new AtomicInteger();


        createScoreboards(staff, suspect);
    }


    public void createScoreboards(ProxiedPlayer staffer, ProxiedPlayer suspect) {
        sendStaff(staffer);
        sendSuspect(suspect);
        update(staffer);
    }

    private void sendStaff(ProxiedPlayer staffer) {

        String sbName = staffer.getName() + random.nextInt(999999);
        ScoreboardObjective sb = new ScoreboardObjective();
        ScoreboardDisplay sbDisplay = new ScoreboardDisplay();
        sb.setName(sbName);
        sb.setAction((byte) 0);
        sb.setValue(ChatColor.translateAlternateColorCodes('&', Config.SCOREBOARD_STAFF_TITLE.getString()));
        sb.setType(ScoreboardObjective.HealthDisplay.INTEGER);
        sbDisplay.setName(sbName);
        sbDisplay.setPosition((byte) 1);
        List<String> lines = Config.SCOREBOARD_STAFF_LINES.getStringList();
        Collections.reverse(lines);
        sendScoreboard(staffer, sb, sbDisplay, sbName, lines);
    }

    private void sendSuspect(ProxiedPlayer suspect) {
            String sbName = suspect.getName() + random.nextInt(999999);
            ScoreboardObjective sb = new ScoreboardObjective();
            ScoreboardDisplay sbDisplay = new ScoreboardDisplay();
            sb.setName(sbName);
            sb.setAction((byte) 0);
            sb.setValue(ChatColor.translateAlternateColorCodes('&', Config.SCOREBOARD_SUSPECT_TITLE.getString()));
            sb.setType(ScoreboardObjective.HealthDisplay.INTEGER);
            sbDisplay.setName(sbName);
            sbDisplay.setPosition((byte) 1);
            List<String> lines = Config.SCOREBOARD_SUSPECT_LINES.getStringList();
            Collections.reverse(lines);
            sendScoreboard(suspect, sb, sbDisplay, sbName, lines);
    }

    private void sendScoreboard(ProxiedPlayer target, ScoreboardObjective sb, ScoreboardDisplay sbDisplay, String sbName, List<String> lines) {
        target.unsafe().sendPacket(sb);
        target.unsafe().sendPacket(sbDisplay);
        for (int i = 0; i < lines.size(); i++) {
            ScoreboardScore score = new ScoreboardScore();
            String line = lines.get(i);
            if (line.isBlank()) { // We have to do if we want all blanks lines to work
                StringBuilder sb1 = new StringBuilder();
                for (int j = 0; j < i; j++) {
                    sb1.append(" ");
                }
                line = sb1.toString();
            } else {
                line = ChatColor.translateAlternateColorCodes('&', parsePlaceholders(line));
            }
            score.setItemName(line);
            score.setValue(i);
            score.setAction((byte) 0);
            score.setScoreName(sbName);
            target.unsafe().sendPacket(score);
        }
    }

    private void update(ProxiedPlayer staffer) {
        SsPlayer ssPlayer = plugin.getPlayersManager().getPlayer(staffer);
        ProxiedPlayer controlled = ssPlayer.getControlled();
        ScheduledTask task = plugin.getProxy().getScheduler().schedule(EnderSS.getInstance(), () -> {
            time.incrementAndGet();
            sendStaff(staffer);
            sendSuspect(controlled);
            }, 1, 1, TimeUnit.SECONDS);
        this.taskID = task.getId();
    }


    public void stop() {
        plugin.getProxy().getScheduler().cancel(taskID);
    }


    private String parsePlaceholders(String s) {
        ProxiedPlayer staffer = ProxyServer.getInstance().getPlayer(this.staff);
        ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(this.suspect);
        String alerts = plugin.getPlayersManager().getPlayer(staffer).isAlerts() ? Config.SCOREBOARD_YES.getString() : Config.SCOREBOARD_NO.getString();
        return s.replaceAll("%STAFFER%", staffer.getName())
                .replaceAll("%SUSPECT%", suspect.getName())
                .replaceAll("%TIME%", parseTime(time.get()))
                .replaceAll("%ALERTS%", alerts);
    }

    private String parseTime(int seconds) {
        int minutes = seconds / 60;
        int remainder = seconds % 60;
        String finalString;
        if (minutes >= 1) {
            finalString =  minutes+"m "+remainder+"s";
        } else {
            finalString = seconds+"s";
        }

        return finalString;
    }
    
}
