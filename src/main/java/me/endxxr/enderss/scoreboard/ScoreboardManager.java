package me.endxxr.enderss.scoreboard;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

import java.util.*;

public class ScoreboardManager {

    private final HashMap<ProxiedPlayer, SsScoreboard> scoreboards;
    private final ScoreboardObjective deleteSb;
    private final ScoreboardDisplay deleteSbDisplay;
    private final Random random;
    private final EnderSS plugin;



    public ScoreboardManager(EnderSS plugin) {
        this.scoreboards = new HashMap<>();
        this.plugin = plugin;
        this.random = new Random();

        this.deleteSb = new ScoreboardObjective();
        this.deleteSbDisplay = new ScoreboardDisplay();


        deleteSb.setName("delete");
        deleteSb.setAction((byte) 1);
        deleteSb.setValue("delete");
        deleteSb.setType(ScoreboardObjective.HealthDisplay.INTEGER);
        deleteSbDisplay.setName("delete");
        deleteSbDisplay.setPosition((byte) 1);
    }

    public void createScoreboards(ProxiedPlayer staff, ProxiedPlayer suspect) {
        scoreboards.put(staff, new SsScoreboard(staff, suspect, plugin, random));
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
        scoreboards.get(staffer).stop();
        scoreboards.remove(staffer);
    }

    public void sendIdlingScoreboard(ProxiedPlayer target) {

        if (target==null || plugin.getPlayersManager().getPlayer(target)==null) return;


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
                String line = lines.get(i);
                if (line.isBlank()) { // We have to do if we want all blanks lines to work
                    StringBuilder sb1 = new StringBuilder();
                    for (int j = 0; j < i; j++) {
                        sb1.append(" ");
                    }
                    line = sb1.toString();
                } else {
                    String alerts = plugin.getPlayersManager().getPlayer(target).isAlerts() ? Config.SCOREBOARD_YES.getString() : Config.SCOREBOARD_NO.getString();
                    line = ChatColor.translateAlternateColorCodes('&', line
                            .replace("%STAFFER%", target.getName())
                            .replace("%SUSPECT%", Config.SCOREBOARD_NOT_CONTROLLING_PLACEHOLDER.getString())
                            .replace("%ALERTS%", alerts));
                }
                score.setItemName(line);
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


}
