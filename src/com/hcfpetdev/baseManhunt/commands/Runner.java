package com.hcfpetdev.baseManhunt.commands;

import com.hcfpetdev.baseManhunt.Main;
import com.hcfpetdev.baseManhunt.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Runner {
    public Runner(Main instance) {
        String path = "command-aliases.runner";
        List<String> aliases = instance.getConfig().getStringList(path);

        boolean requiresOp = instance.getConfig().getBoolean("command-permissions.require-op");

        Logger logger = Bukkit.getLogger();

        if (aliases.isEmpty()) {
            aliases.add("/runner");
            logger.warning(path + " in config.yml is not set, so it is defaulted to  '/runner'");
        }

        for (int i = 0;i<aliases.size();i++) {
            if (aliases.get(i).startsWith("/")) aliases.set(i, aliases.get(i).substring(1));
        }

        String command = aliases.remove(0);

        new CommandHandler(instance, command) {

            @Override
            public boolean onCommand(CommandSender sender, String[] args) {

                if (requiresOp && !sender.isOp()) {
                    Message.sendPlayerError(sender, "you do not have permission to use this permission");
                    return true;
                }

                if (args.length == 0) return false;
                if (Main.manhuntInProgress() && (args[0].equalsIgnoreCase("leave")
                        || args[0].equalsIgnoreCase("remove"))) {
                    Message.sendPlayerError(sender, "Runners cannot be altered during a manhunt");
                    return true;
                }

                if (!Main.allowMultipleRunners() && Main.getOnlineRunners().size() >= 1 && (args[0].equalsIgnoreCase("join") ||
                    args[0].equalsIgnoreCase("add"))) {
                    Message.sendPlayerError(sender,
                            "enable the 'allow-multiple-runners' setting in config.yml to allow multiple runners");
                    return true;
                }

                if (args[0].equalsIgnoreCase("join")) {
                    if (!(sender instanceof Player)) return false;

                    Player player = (Player) sender;

                    if (Main.onlineHuntersContains(player)) {
                        Message.sendPlayerError(player,
                                "You are already a hunter, first leave before joining another team");
                        return true;
                    }

                    if (Main.onlineRunnerContains(player)) {
                        Message.sendPlayerError(sender,
                                "You are already a runner");
                        return true;
                    }

                    Main.addRunner(player.getName());
                    Main.addOnlineRunner(player, true);

                    Main.updateConfig();
                }

                else if (args[0].equalsIgnoreCase("leave")) {
                    if (!(sender instanceof Player)) return false;

                    Player player = (Player) sender;

                    if (!Main.onlineRunnerContains(player)) {
                        Message.sendPlayerError(player, "you are not part of the 'Runners' team");
                        return true;
                    }

                    Main.removeRunner(player.getName());
                    Main.removeOnlineRunner(player,true);

                    Main.updateConfig();
                }

                else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length < 2) {
                        Message.sendPlayerError(sender, "please specify a player");
                        return true;
                    }


                    Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) Message.sendPlayerError(sender, "that player is not online");
                    else {

                        if (Main.onlineHuntersContains(player)) {
                            Message.sendPlayerError(sender,
                                    player.getName() +
                                            " is already a hunter, first leave before joining another team");
                            return true;
                        }

                        if (Main.onlineRunnerContains(player)) {
                            Message.sendPlayerError(sender, player.getName() +
                                    "is already a runner");
                            return true;
                        }

                        Main.addRunner(player.getName());
                        Main.addOnlineRunner(player, true);

                        Main.updateConfig();
                    }
                }

                else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length < 2) {
                        Message.sendPlayerError(sender, "please specify a player");
                        return true;
                    }

                    Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) Message.sendPlayerError(sender, "that player is not online");
                    else {
                        if (!Main.onlineRunnerContains(player)) {
                            Message.sendPlayerError(sender, player.getName() + " is not part of the 'Runners' team");
                            return true;
                        }

                        Main.removeRunner(player.getName());
                        Main.removeOnlineRunner(player, true);

                        Main.updateConfig();
                    }
                }

                else if (args[0].equalsIgnoreCase("list")) {
                    StringBuilder runners = new StringBuilder();

                    for (Player p : Main.getOnlineRunners()) runners.append(p.getName()).append("&b, ");

                    if (runners.length() < 2) runners.append("No runners  ");

                    Message.sendPlayerMessage(sender, "&aRunners: &e" +
                            runners.substring(0,runners.length()-2));
                }

                else if (args[0].equalsIgnoreCase("clear")) {
                    Main.clearRunners();
                    Main.clearOnlineRunners();

                    Message.broadcast("&c" + sender.getName() + " has &l&ncleared&r &call runners");
                }

                instance.saveConfig();

                return true;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) {


                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                    List<String> output = new ArrayList<>();
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) output.add(p.getName());
                    return output;
                }
                else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("leave")
                        || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("clear"))
                    return Collections.singletonList("");

                return Arrays.asList("join", "add", "list", "leave", "remove", "clear");
            }

            @Override
            public List<String> getAliases() {
                if (aliases.isEmpty()) return super.getAliases();
                return aliases;
            }

            @Override
            public String getUsage() {
                return "/" + command + " <join|add|list|leave|remove|clear> <player>";
            }

            @Override
            public String getDescription() {
                return "The command used to manipulate the runners of a manhunt";
            }
        };
    }
}
