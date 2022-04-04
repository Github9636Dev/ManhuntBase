package com.hcfpetdev.baseManhunt;

import com.hcfpetdev.baseManhunt.commands.Hunter;
import com.hcfpetdev.baseManhunt.commands.Manhunt;
import com.hcfpetdev.baseManhunt.commands.Runner;
import com.hcfpetdev.baseManhunt.listeners.CompassListener;
import com.hcfpetdev.baseManhunt.listeners.JoinLeaveListener;
import com.hcfpetdev.baseManhunt.listeners.WinListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private static String name, version, messageToJoinedPlayer, messageToRemovedPlayer, messageBroadcastedOnJoin,
    messageBroadcastedOnRemove;

    private static List<Player> onlineRunners, onlineHunters;
    private static List<String> runners, hunters;

    private static boolean manhuntInProgress,allowMultipleRunners, broadcastJoinTeam, requireAllRunnersToDie;

    private static long startTime;

    @Override
    public void onEnable() {
        super.onEnable();

        name = getDescription().getName();
        version = getDescription().getVersion();

        manhuntInProgress = false;

        PluginManager pluginManager = Bukkit.getPluginManager();
        FileConfiguration config = getConfig();

        if (!new File(config.getCurrentPath()).exists()) saveDefaultConfig();

        onlineRunners = new ArrayList<>();
        onlineHunters = new ArrayList<>();

        loadConfig();

        String defaultRunner = config.getString("defaults.runner");
        String defaultHunter = config.getString("defaults.hunter");


        boolean saveOnReload = config.getBoolean("save-on-reload");

        if (defaultRunner == null) defaultRunner = "";
        if (defaultHunter == null) defaultHunter = "";

        int defaultTeam = 0;

        if (defaultRunner.equalsIgnoreCase("all")) defaultTeam = 1;
        if (defaultHunter.equalsIgnoreCase("all")) defaultTeam = 2;

        //Commands
        Manhunt manhunt = new Manhunt(this);
        Runner runner = new Runner(this);
        Hunter hunter = new Hunter(this);

        //Listeners
        JoinLeaveListener joinLeaveListener = new JoinLeaveListener
                (runners,hunters,saveOnReload, defaultTeam, defaultRunner, defaultHunter, broadcastJoinTeam,
                        messageToJoinedPlayer, messageBroadcastedOnJoin);

        CompassListener compassListener = new CompassListener();
        WinListener winListener = new WinListener();

        pluginManager.registerEvents(joinLeaveListener, this);
        pluginManager.registerEvents(compassListener,this);
        pluginManager.registerEvents(winListener, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        FileConfiguration config = getConfig();

        config.set("values.runners", runners);
        config.set("values.hunters", hunters);

    }

    private void loadConfig() {

        FileConfiguration config = getConfig();

        broadcastJoinTeam = config.getBoolean("message.broadcast-join-team");

        runners = config.getStringList("values.runners");
        hunters = config.getStringList("values.hunters");

        if (runners.size() > 1 && !allowMultipleRunners) {
            getLogger().info("allow-multiple-runners is set to true because there are more than 1 runner saved");
            allowMultipleRunners = true;
            config.set("allow-multiple-runners", true);
        }

        messageToJoinedPlayer = config.getString("message.message-to-joined-player");
        messageToRemovedPlayer = config.getString("message.message-to-removed-player");
        messageBroadcastedOnJoin = config.getString("message.broadcast-message-join");
        messageBroadcastedOnRemove = config.getString("message.broadcast-message-remove");

        if (messageToJoinedPlayer == null) messageToJoinedPlayer = "§aYou have joined the %s team";
        if (messageToRemovedPlayer == null) messageToRemovedPlayer = "&cYou have been removed from %s";
        if (messageBroadcastedOnJoin == null) messageBroadcastedOnJoin = "&a%s has joined %s";
        if (messageBroadcastedOnRemove == null) messageBroadcastedOnRemove = "&c%s has been removed from %s";

        messageToJoinedPlayer = ChatColor.translateAlternateColorCodes('&', messageToJoinedPlayer);
        messageToRemovedPlayer = ChatColor.translateAlternateColorCodes('&', messageToRemovedPlayer);
        messageBroadcastedOnJoin = ChatColor.translateAlternateColorCodes('&', messageBroadcastedOnJoin);
        messageBroadcastedOnRemove = ChatColor.translateAlternateColorCodes('&',messageBroadcastedOnRemove);

        requireAllRunnersToDie = config.getBoolean("require-all-runners-to-die");
    }

    public static String getPluginName() {
        return name;
    }

    public static void addOnlineRunner(Player player, boolean showMessage) {
        onlineRunners.add(player);

        onlineHunters.remove(player);

        if (!showMessage) return;
        Message.sendPlayerMessage(player, Message.format(messageToJoinedPlayer, "Runners"));

        if (broadcastJoinTeam) Message.broadcast(
                Message.format(messageBroadcastedOnJoin, player.getName(), "Runners"));
        
    }

    public static void addOnlineHunter(Player player, boolean showMessage) {
        onlineHunters.add(player);

        onlineRunners.remove(player);
        
        if (!showMessage) return;

        Message.sendPlayerMessage(player, Message.format(messageToJoinedPlayer, "Hunters"));

        if (broadcastJoinTeam) Message.broadcast(
                Message.format(messageBroadcastedOnJoin, player.getName(), "Hunters"));
    }

    public static void removeOnlineRunner(Player player, boolean showMessage) {
        onlineRunners.remove(player);

        if (!showMessage) return;

        Message.sendPlayerMessage(player, Message.format(messageToRemovedPlayer, "Runners"));

        if (broadcastJoinTeam) Message.broadcast(
                Message.format(messageBroadcastedOnRemove, player.getName(), "Runners"));
    }

    public static void removeOnlineHunter(Player player, boolean showMessage) {
        onlineHunters.remove(player);

        if (!showMessage) return;

        Message.sendPlayerMessage(player,  Message.format(messageToRemovedPlayer, "Hunters"));

        if (broadcastJoinTeam) Message.broadcast(
                Message.format(messageBroadcastedOnRemove, player.getName(), "Hunters"));
    }

    public static boolean onlineRunnerContains(Player player) {
        return onlineRunners.contains(player);
    }

    public static boolean onlineHuntersContains(Player player) {
        return onlineHunters.contains(player);
    }

    public static void clearOnlineRunners() {
        onlineRunners.clear();
    }

    public static void clearOnlineHunters() {
        onlineHunters.clear();
    }

    public static void addRunner(String name) {
        runners.add(name);
    }

    public static void addHunter(String name) {
        hunters.add(name);
    }

    public static void removeRunner(String name) {
        runners.remove(name);
    }

    public static void removeHunter(String name) {
        hunters.remove(name);
    }

    public static void clearRunners() {
        runners.clear();
    }

    public static void clearHunters() {
        hunters.clear();
    }

    public static boolean manhuntInProgress() {
        return manhuntInProgress;
    }

    public static void setManhuntInProgress(boolean manhuntInProgress) {
        if (manhuntInProgress) startTime = System.currentTimeMillis();
        Main.manhuntInProgress = manhuntInProgress;
    }

    public static List<Player> getOnlineHunters() {
        return onlineHunters;
    }

    public static List<Player> getOnlineRunners() {
        return onlineRunners;
    }

    public static long getStartTime() {
        return startTime;
    }

    public static String timeToString(long time) {
        long seconds = time  / 1000;
        long minutes = 0;
        long hours = 0;
        long days = 0;

        if (seconds / 60 != 0) {
            minutes = seconds / 60;
            seconds %= 60;
        }

        if (minutes / 60 != 0) {
            hours = minutes / 60;
            minutes %= 60;
        }

        if (hours / 24 != 0) {
            days = hours / 24;
            hours %= 24;
        }

        String output = "";

        if (days == 1) output += "1 day, ";
        else if (days > 1) output += days + " days, ";

        if (hours == 1) output += "1 hour, ";
        else if (hours > 1) output += hours + " hours, ";

        if (minutes == 1) output += "1 minute, ";
        else if (minutes > 1) output += minutes + " minutes, ";

        if (seconds == 1) output += "1 second, ";
        else if (seconds > 1) output += seconds + " seconds, ";

        return output.substring(0,output.length()-2);
    }

    public static boolean requireAllRunnersToDie() {
        return requireAllRunnersToDie;
    }

    public static boolean allowMultipleRunners() {
        return allowMultipleRunners;
    }
}
