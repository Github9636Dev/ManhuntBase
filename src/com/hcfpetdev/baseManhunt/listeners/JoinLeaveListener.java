package com.hcfpetdev.baseManhunt.listeners;

import com.hcfpetdev.baseManhunt.Main;
import com.hcfpetdev.baseManhunt.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class JoinLeaveListener implements Listener {

    private final List<String> runners,hunters;
    private final String defaultRunner, defaultHunter, messageToPlayer, messageBroadcasted;
    private final boolean saveOnReload, broadcast;
    private final int defaultTeam;

    public JoinLeaveListener(List<String> runners,List<String> hunters, boolean saveOnReload, int defaultTeam,
                             String defaultRunner, String defaultHunter, boolean broadcast, String messageToPlayer,
                             String messageBroadcasted) {
        this.runners = runners;
        this.hunters = hunters;
        this.saveOnReload = saveOnReload;
        this.defaultTeam = defaultTeam;
        this.defaultRunner = defaultRunner;
        this.defaultHunter = defaultHunter;
        this.broadcast = broadcast;
        this.messageToPlayer = messageToPlayer;
        this.messageBroadcasted = messageBroadcasted;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (defaultTeam == 1) Main.addOnlineRunner(player, false);
        if (defaultTeam == 2) Main.addOnlineHunter(player, false);

        if (defaultRunner.equals(player.getName())) Main.addOnlineRunner(player,false);
        else if (defaultHunter.equals(player.getName())) Main.addOnlineHunter(player, false);

        if (runners.contains(player.getName())) Main.addOnlineRunner(player, false);
        else if (hunters.contains(player.getName())) Main.addOnlineHunter(player, false);
        else if (saveOnReload) {
            if (Main.onlineRunnerContains(player)) Main.addRunner(player.getName());
            if (Main.onlineHuntersContains(player)) Main.addHunter(player.getName());
        }

        if (Main.onlineRunnerContains(player)) {
            Message.sendPlayerMessage(player,  Message.format(messageToPlayer, "Runners"));

            if (broadcast) Message.broadcast(
                    Message.format(messageBroadcasted, player.getName(), "Runners"));
        }

        else if (Main.onlineHuntersContains(player)) {
            Message.sendPlayerMessage(player,  Message.format(messageToPlayer, "Hunters"));

            if (broadcast) Message.broadcast(
                    Message.format(messageBroadcasted, player.getName(), "Hunters"));
        }

        Main.updateConfig();

    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (runners.contains(player.getName())) Main.removeOnlineRunner(player,false);
        else if (hunters.contains(player.getName())) Main.removeOnlineHunter(player, false);
    }
}
