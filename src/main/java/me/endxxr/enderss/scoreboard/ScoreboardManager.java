package me.endxxr.enderss.scoreboard;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScoreboardManager {

    final EnderSS plugin;
    final Random random;
    final ScoreboardObjective deleteSb;
    final ScoreboardDisplay deleteSbDisplay;
    final ConcurrentHashMap<SsPlayer, Integer> scores;

    public ScoreboardManager(EnderSS plugin) {

        this.plugin = plugin;
        this.random = new Random();

        this.deleteSb = new ScoreboardObjective();
        this.deleteSbDisplay = new ScoreboardDisplay();
        this.scores = new ConcurrentHashMap<>();

        deleteSb.setName("delete");
        deleteSb.setAction((byte) 1);
        deleteSb.setValue("delete");
        deleteSb.setType(ScoreboardObjective.HealthDisplay.INTEGER);
        deleteSbDisplay.setName("delete");
        deleteSbDisplay.setPosition((byte) 1);

    }


    public void createScoreboards(ProxiedPlayer staffer, ProxiedPlayer suspect) {

        updateStaffScoreboard(staffer, suspect);
        updateSuspectScoreboard(suspect, staffer);
        scores.put(plugin.getPlayersManager().getPlayer(staffer), 0);
        stopWatch(staffer);

    }

    private void updateSuspectScoreboard(ProxiedPlayer suspect, ProxiedPlayer staffer) {

            String sbName = suspect.getName() + random.nextInt(999999);
            deleteSb.setName(sbName);
            deleteSb.setAction((byte) 0);
            deleteSb.setValue(ChatColor.translateAlternateColorCodes('&', Config.SCOREBOARD_SUSPECT_TITLE.getString()));
            deleteSb.setType(ScoreboardObjective.HealthDisplay.INTEGER);
            deleteSbDisplay.setName(sbName);

            List<String> lines = Config.SCOREBOARD_SUSPECT_LINES.getStringList();
            Collections.reverse(lines);
            suspect.unsafe().sendPacket(deleteSb);
            suspect.unsafe().sendPacket(deleteSbDisplay);

            for (int i = 0; i < lines.size(); i++) {
                ScoreboardScore score = new ScoreboardScore();
                score.setItemName(ChatColor.translateAlternateColorCodes('&', parsePlaceholders(lines.get(i), staffer, suspect)));
                score.setValue(i);
                score.setAction((byte) 0);
                score.setScoreName(sbName);
                suspect.unsafe().sendPacket(score);
            }

    }

    public void updateStaffScoreboard(ProxiedPlayer staffer, ProxiedPlayer suspect) {

        String sbName = staffer.getName() + random.nextInt(999999);
        deleteSb.setName(sbName);
        deleteSb.setAction((byte) 0);
        deleteSb.setValue(ChatColor.translateAlternateColorCodes('&', Config.SCOREBOARD_STAFF_TITLE.getString()));
        deleteSb.setType(ScoreboardObjective.HealthDisplay.INTEGER);
        deleteSbDisplay.setName(sbName);

        List<String> lines = Config.SCOREBOARD_STAFF_LINES.getStringList();
        Collections.reverse(lines);
        staffer.unsafe().sendPacket(deleteSb);
        staffer.unsafe().sendPacket(deleteSbDisplay);

        for (int i = 0; i < lines.size(); i++) {
            ScoreboardScore score = new ScoreboardScore();
            score.setItemName(ChatColor.translateAlternateColorCodes('&', parsePlaceholders(lines.get(i), staffer, suspect)));
            score.setValue(i);
            score.setAction((byte) 0);
            score.setScoreName(sbName);
            staffer.unsafe().sendPacket(score);
        }
    }


    /**
     *
     * Sends the idling scoreboard to the specified player, asynchronously.
     *
     * @param target The player to send the scoreboard to.
     */


    public void sendIdlingScoreboard(ProxiedPlayer target) {

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            final ScoreboardObjective scoreboard = new ScoreboardObjective();
            final String sbName = "idling" + random.nextInt(999999);
            scoreboard.setName(sbName);
            scoreboard.setAction((byte) 0);
            scoreboard.setValue(ChatColor.translateAlternateColorCodes('&', Config.SCOREBOARD_IDLING_TITLE.getString()));
            scoreboard.setType(ScoreboardObjective.HealthDisplay.INTEGER);

            final ScoreboardDisplay sbDisplay = new ScoreboardDisplay();
            sbDisplay.setPosition((byte) 1);
            sbDisplay.setName(sbName);

            target.unsafe().sendPacket(scoreboard);
            target.unsafe().sendPacket(sbDisplay);

            List<String> lines = Config.SCOREBOARD_IDLING_LINES.getStringList();
            Collections.reverse(lines);

            for (int i = 0; i < lines.size(); i++) {
                final ScoreboardScore score = new ScoreboardScore();
                score.setItemName(ChatColor.translateAlternateColorCodes('&', lines.get(i)
                        .replace("%STAFFER%", target.getDisplayName())
                        .replace("%SUSPECT%", Config.SCOREBOARD_NOT_CONTROLLING_PLACEHOLDER.getString())));
                score.setValue(i);
                score.setAction((byte) 0);
                score.setScoreName(sbName);
                target.unsafe().sendPacket(score);
            }

        });
    }

    /**
     *
     * Destroy the current scoreboard of the specified player
     *
     * @param target the target of this mehtod
     */

    private void destroyScoreboard(ProxiedPlayer target) {
        target.unsafe().sendPacket(this.deleteSb);
        target.unsafe().sendPacket(this.deleteSbDisplay);
    }

    /**
     *
     * Destroy all the scoreboards of the specified players
     * and set the idling scoreboard to the specified players
     *
     * @param staffer the staff player
     * @param suspect the suspect player
     */

    public void endScoreboard(ProxiedPlayer staffer, ProxiedPlayer suspect) {
        destroyScoreboard(staffer);
        destroyScoreboard(suspect);
        if (staffer.getServer().getInfo().getName().equalsIgnoreCase(Config.CONFIG_SSSERVER.getString())) sendIdlingScoreboard(staffer);
        if (suspect.getServer().getInfo().getName().equalsIgnoreCase(Config.CONFIG_SSSERVER.getString())) sendIdlingScoreboard(suspect);
    }

    /**
     *
     * Destroy the scoreboard of the specified player
     * and set the idling scoreboard
     *
     * @param player the player
     */

    public void endScoreboard(ProxiedPlayer player) {
        destroyScoreboard(player);
        if (player.getServer().getInfo().getName().equalsIgnoreCase(Config.CONFIG_SSSERVER.getString())) sendIdlingScoreboard(player);
    }


    private void stopWatch(ProxiedPlayer staffer) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        SsPlayer ssPlayer = plugin.getPlayersManager().getPlayer(staffer);
        ProxiedPlayer controlled = ssPlayer.getControlled(); 
        executor.scheduleAtFixedRate(() -> {
            if (plugin.getPlayersManager().getPlayer(staffer).getControlled()!=null) { //self-destruct if the player is no longer controlling
                executor.shutdownNow();
                return;
            }
            scores.replace(ssPlayer, scores.get(ssPlayer) + 1);
            updateStaffScoreboard(staffer, ssPlayer.getControlled());
            updateSuspectScoreboard(controlled, staffer);
        }, 1, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    private String parsePlaceholders(String s, ProxiedPlayer staffer, ProxiedPlayer suspect) {
        return s.replaceAll("%STAFFER%", staffer.getName())
                .replaceAll("%SUSPECT%", suspect.getName())
                .replaceAll("%TIME%", parseTime(scores.get(plugin.getPlayersManager().getPlayer(staffer))));
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
