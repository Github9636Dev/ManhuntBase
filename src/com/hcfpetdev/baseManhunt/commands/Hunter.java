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

public class Hunter {
    public Hunter(Main instance) {
        String path = "command-aliases.hunter";
        List<String> aliases = instance.getConfig().getStringList(path);

        boolean requiresOp = instance.getConfig().getBoolean("command-permissions.require-op");

        Logger logger = Bukkit.getLogger();

        if (aliases.isEmpty()) {
            aliases.add("/hunter");
            logger.warning(path + " in config.yml is not set, so it is defaulted to  '/hunter'");
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
                    if (Main.getOnlineHunters().size() == 1) {
                        Message.sendPlayerError(sender, "at least one hunter is needed for a manhunt");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("join")) {
                    if (!(sender instanceof Player)) return false;

                    Player player = (Player) sender;

                    if (Main.onlineRunnerContains(player)) {
                        Message.sendPlayerError(player,
                                "You are already a runner, first leave before joining another team");
                        return true;
                    }

                    Main.addHunter(player.getName());
                    Main.addOnlineHunter(player, true);
                }

                else if (args[0].equalsIgnoreCase("leave")) {
                    if (!(sender instanceof Player)) return false;

                    Player player = (Player) sender;

                    if (!Main.onlineHuntersContains(player)) {
                        Message.sendPlayerError(player, "you are not part of the 'Hunters' team");
                    }

                    Main.removeHunter(player.getName());
                    Main.removeOnlineHunter(player,true);
                }

                else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length < 2) {
                        Message.sendPlayerError(sender, "please specify a player");
                        return true;
                    }


                    Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) Message.sendPlayerError(sender, "that player is not online");
                    else {

                        if (Main.onlineRunnerContains(player)) {
                            Message.sendPlayerError(sender,
                                    player.getName() +
                                            " is already a runner, first leave before joining another team");
                            return true;
                        }

                        Main.addHunter(player.getName());
                        Main.addOnlineHunter(player, true);
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
                        if (!Main.onlineHuntersContains(player)) {
                            Message.sendPlayerError(sender, player.getName() + " is not part of the 'Hunters' team");
                        }
                        Main.removeHunter(player.getName());
                        Main.removeOnlineHunter(player, true);
                    }
                }

                else if (args[0].equalsIgnoreCase("list")) {
                    StringBuilder hunters = new StringBuilder();

                    for (Player p : Main.getOnlineHunters()) hunters.append(p.getName()).append("&b, ");

                    if (hunters.length() < 2) hunters.append("No hunters  ");

                    Message.sendPlayerMessage(sender, "&aHunters: &e" +
                            hunters.substring(0,hunters.length()-2));
                }

                else if (args[0].equalsIgnoreCase("clear")) {
                    Main.clearHunters();
                    Main.clearOnlineHunters();

                    Message.broadcast("&c" + sender.getName() + " has &l&ncleared &r&call hunters");
                }

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
                return "The command used to manipulate the hunters of a manhunt";
            }
        };
    }
}
