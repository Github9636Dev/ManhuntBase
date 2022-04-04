package com.hcfpetdev.baseManhunt.listeners;

import com.hcfpetdev.baseManhunt.Main;
import com.hcfpetdev.baseManhunt.Message;
import com.hcfpetdev.baseManhunt.commands.Manhunt;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WinListener implements Listener {
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (!Main.manhuntInProgress()) return;
        if (!Main.onlineRunnerContains(event.getEntity())) return;

        Player player = event.getEntity();

        if (Main.allowMultipleRunners() && Main.requireAllRunnersToDie()) {
            Main.removeOnlineRunner(player, true);
            Message.broadcast("&eThere are " + Main.getOnlineRunners().size() + " runners left");

            if (!Main.getOnlineRunners().isEmpty()) return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Manhunt.command + " stop");
        Message.broadcast("&cHunters won! All runners has died");
    }

    @EventHandler
    private void onEnderDragonDeath(EntityDeathEvent event) {
        if (!Main.manhuntInProgress()) return;
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Manhunt.command + " stop");
        Message.broadcast("&aRunners won! The Ender Dragon has died");
    }
}
