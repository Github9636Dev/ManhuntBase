package com.hcfpetdev.baseManhunt.commands;

import com.hcfpetdev.baseManhunt.Main;
import com.hcfpetdev.baseManhunt.Message;
import com.hcfpetdev.baseManhunt.listeners.CompassListener;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Manhunt {

    private Main instance;

    public static String command;

    public Manhunt(Main instance) {

        this.instance = instance;

        String path = "command-aliases.manhunt";
        List<String> aliases = instance.getConfig().getStringList(path);

        boolean requiresOp = instance.getConfig().getBoolean("command-permissions.require-op");

        Logger logger = Bukkit.getLogger();

        if (aliases.isEmpty()) {
            aliases.add("/manhunt");
            logger.warning(path + " in config.yml is not set, so it is defaulted to  '/manhunt'");
        }

        for (int i = 0;i<aliases.size();i++) {
            if (aliases.get(i).startsWith("/")) aliases.set(i, aliases.get(i).substring(1));
        }

        command = aliases.remove(0);

        setPvp(instance.getServer(), false);

        CommandHandler handler = new CommandHandler(instance, command, 1) {

            @Override
            public boolean onCommand(CommandSender sender, String[] args) {

                if (requiresOp && !sender.isOp()) {
                    Message.sendPlayerError(sender, "You do not have permission to use this command");
                    return true;
                }

                String arg = args[0].toLowerCase();

                switch (arg) {
                    case "start":
                        start(sender);
                        break;
                    case "stop":
                        stop(sender);
                        break;
                    case "reset":
                        reset(sender);
                        break;
                    case "time":
                        time(sender);
                        break;
                    default:
                        return false;
                }

                return true;
            }

            @Override
            public String getUsage() {
                return "/" + command + " <start|stop|reset>";
            }

            @Override
            public String getDescription() {
                return "The command used to manipulate the manhunt";
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

                return Arrays.asList("start", "stop", "reset", "time");

            }

            @Override
            public List<String> getAliases() {
                return aliases;
            }
        };
    }

    private void start(CommandSender sender) {
        if (Main.manhuntInProgress()) {
            Message.sendPlayerError(sender, "There is a Manhunt in progress, cancel with the argument: stop");
            return;
        }

        int runners = Main.getOnlineRunners().size();
        int hunters = Main.getOnlineHunters().size();

        if (runners < 1) {
            Message.sendPlayerError(sender, "At least one runner is required to start a manhunt");
            return;
        }

        if (runners > 1 && !Main.allowMultipleRunners()) {
            Message.sendPlayerError(sender,
                    "More than 1 hunter is not allowed, change 'allow-multiple-runners' to true in the config.yml file");
            return;
        }

        if (hunters < 1) {
            Message.sendPlayerError(sender, "At least one hunter is required to start a manhunt");
            return;
        }

        setPvp(instance.getServer(),true);
        Message.broadcast("&cThe Manhunt has started by " + sender.getName());
        Main.setManhuntInProgress(true);

        for (Player p : Main.getOnlineHunters()) p.getInventory().addItem(CompassListener.trackerCompass);
    }

    private void stop(CommandSender sender) {
        if (!Main.manhuntInProgress()) {
            Message.sendPlayerError(sender, "There is no Manhunt in progress, start one with the argument: start");
            return;
        }

        setPvp(instance.getServer(),false);
        Message.broadcast("&cThe Manhunt has been ended by " + sender.getName());

        time(sender);

        Main.setManhuntInProgress(false);

//        for (Player p : Main.getOnlineHunters()) p.getInventory().remove(CompassListener.trackerCompass);
    }

    private void reset(CommandSender sender) {
        Main.clearHunters();
        Main.clearRunners();

        Main.clearOnlineHunters();
        Main.clearOnlineRunners();

        Message.broadcast("&cThe runners and hunters have been reset");

        stop(sender);
    }

    private void time(CommandSender sender) {

        if (!Main.manhuntInProgress()) {
            Message.sendPlayerError(sender, "There is no Manhunt in progress, start one with the argument: start");
            return;
        }

        long timeElapsed = System.currentTimeMillis() - Main.getStartTime();

        Message.sendPlayerMessage(sender, "&aThe current manhunt has lasted: &e" + Main.timeToString(timeElapsed));
    }

    private void setPvp(Server server, boolean pvp) {
        for (World w : server.getWorlds()) w.setPVP(pvp);
    }
}
