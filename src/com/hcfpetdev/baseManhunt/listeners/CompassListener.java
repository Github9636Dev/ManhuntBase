package com.hcfpetdev.baseManhunt.listeners;

import com.hcfpetdev.baseManhunt.Main;
import com.hcfpetdev.baseManhunt.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CompassListener implements Listener {

    private Map<Player, Integer> compassTargets;

    public static ItemStack trackerCompass;

    public CompassListener() {

        compassTargets = new HashMap<>();

        trackerCompass = new ItemStack(Material.COMPASS);

        CompassMeta meta = (CompassMeta) Bukkit.getItemFactory().getItemMeta(Material.COMPASS);

        assert meta != null;

        meta.setLodestoneTracked(false);
        Bukkit.getWorlds().get(0).getBlockAt(0,-64,0).setType(Material.LODESTONE);
        meta.setLodestone(new Location(Bukkit.getWorlds().get(0),0,-64,0));
        Bukkit.getWorlds().get(0).getBlockAt(0,-64,0).setType(Material.BEDROCK);
        meta.setDisplayName("§eTracker Compass");
        meta.setLore(Arrays.asList("§7Tracks a runner's Position", "§7Right click to change / retrack target"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        trackerCompass.setItemMeta(meta);
    }

    @EventHandler
    private void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;


        Player player = event.getPlayer();

        if (!Main.onlineHuntersContains(player)) return;

        List<Player> runners = Main.getOnlineRunners();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getItemMeta() == null) return;

        if (item.getItemMeta().getDisplayName().equals("§eTracker Compass")) {

            if (!Main.manhuntInProgress()) {
                Message.sendPlayerError(player, "Tracker compasses are only enabled when the manhunt starts");
                return;
            }

            int target;
            if (compassTargets.containsKey(player)) {
                target = compassTargets.remove(player);
                if (++target >= runners.size()) target = 0;
            }
            else {
                compassTargets.put(player, 0);
                target = 0;
            }

            Player targetedPlayer = runners.get(target);
            Location l = targetedPlayer.getLocation();

            if (!targetedPlayer.getWorld().equals(player.getWorld())) {
                Message.sendPlayerMessage(player,
                        "&c" + targetedPlayer.getName() + " is not found (in the current world)");
                return;
            }

            CompassMeta meta = (CompassMeta) item.getItemMeta();

            assert meta != null;
            meta.setLodestoneTracked(false);

            player.getWorld().getBlockAt(l.getBlockX(), -64, l.getBlockZ()).setType(Material.LODESTONE);
            meta.setLodestone(l);

            if (player.getWorld().getEnvironment() == World.Environment.THE_END ||
                    player.getWorld().getEnvironment() == World.Environment.CUSTOM)
                player.getWorld().getBlockAt(l.getBlockX(), -64, l.getBlockZ()).setType(Material.AIR);
            else player.getWorld().getBlockAt(l.getBlockX(), -64, l.getBlockZ()).setType(Material.BEDROCK);



            item.setItemMeta(meta);

            player.getInventory().setItemInMainHand(item);

            Message.sendPlayerMessage(player, "&aTracking " + targetedPlayer.getName());
        }
    }


    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta() == null) return;
        if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§eTracker Compass") && Main.manhuntInProgress()) {
            event.setCancelled(true);
        }
    }
}
