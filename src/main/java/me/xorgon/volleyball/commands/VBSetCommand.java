package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import me.xorgon.volleyball.VManager;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VBSetCommand {

            public static class VBSetRootCommand {
                @Command(aliases = {"set"}, desc = "Define a setting for a court.")
                @CommandPermissions("vb.admin")
                @NestedCommand(value = {VBSetCommand.class})
                public static void set(CommandContext args, CommandSender sender) {
                }
            }

            @Command(aliases = {"red"}, desc = "Set the red side of the court.", usage = "<court name>", min = 1)
            public static void red(CommandContext args, CommandSender sender) {
                if (!(sender instanceof Player)) {
                    return;
                }
                Player player = (Player) sender;
                VManager manager = VolleyballPlugin.getInstance().getManager();
                Court court = manager.getCourt(args.getString(0));
                if (court != null) {
                    WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
                    LocalSession playerSession = worldedit.getSession(player);
                    if (playerSession != null) {
                        Region selection = null;
                        try {
                            selection = playerSession.getSelection(playerSession.getSelectionWorld());
                        } catch (IncompleteRegionException e) {
                            player.sendMessage(ChatColor.RED + "Your selection is incomplete.");
                        }
                        if (selection != null) {
                            court.setRed(selection.getMinimumPoint(), selection.getMaximumPoint().add(1, 0, 1));
                            court.setWorld(player.getWorld());
                            player.sendMessage(ChatColor.YELLOW + "Red side set.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "WorldEdit could not find your session.");
                    }
                } else {
                    player.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"blue"}, desc = "Set the blue side of the court.", usage = "<court name>", min = 1)
            public static void blue(CommandContext args, CommandSender sender) {
                if (!(sender instanceof Player)) {
                    return;
                }
                Player player = (Player) sender;
                VManager manager = VolleyballPlugin.getInstance().getManager();
                Court court = manager.getCourt(args.getString(0));
                if (court != null) {
                    WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
                    LocalSession playerSession = worldedit.getSession(player);
                    if (playerSession != null) {
                        Region selection = null;
                        try {
                            selection = playerSession.getSelection(playerSession.getSelectionWorld());
                        } catch (IncompleteRegionException e) {
                            player.sendMessage(ChatColor.RED + "Your selection is incomplete.");
                        }
                        if (selection != null) {
                            court.setBlue(selection.getMinimumPoint(), selection.getMaximumPoint().add(1, 0, 1));
                            court.setWorld(player.getWorld());
                            player.sendMessage(ChatColor.YELLOW + "Blue side set.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "WorldEdit could not find your session.");
                    }
                } else {
                    player.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"displayname", "dn"},
                    desc = "Set the display name of the court,",
                    usage = "<court name> <display name>",
                    min = 1)
            public static void displayName(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setDisplayName(args.getJoinedStrings(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set court display name.");
                    } else {
                        manager.getCourt(args.getString(0)).setDisplayName(null);
                        sender.sendMessage(ChatColor.YELLOW + "Unset court display name.");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"ballsize"},
                    desc = "Set the ball size for the court,",
                    usage = "<court name> <ball size>",
                    min = 1)
            public static void ballSize(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setBallSize(args.getInteger(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set ball size.");
                    } else {
                        manager.getCourt(args.getString(0)).setBallSize(3);
                        sender.sendMessage(ChatColor.YELLOW + "Reset ball size to default (3).");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"ymax"},
                    desc = "Set the maximum y position for the court,",
                    usage = "<court name> <y max>",
                    min = 1)
            public static void yMax(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setYMax(args.getDouble(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set maximum y position.");
                    } else {
                        manager.getCourt(args.getString(0)).setYMax(320);
                        sender.sendMessage(ChatColor.YELLOW + "Reset maximum y position to 320");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"power"},
                    desc = "Set the power multiplier for the court,",
                    usage = "<court name> <power multiplier>",
                    min = 1)
            public static void power(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setPowerFactor(args.getDouble(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set power multiplier.");
                    } else {
                        manager.getCourt(args.getString(0)).setPowerFactor(1);
                        sender.sendMessage(ChatColor.YELLOW + "Reset power multiplier to 1.");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"minteamsize"},
                    desc = "Set the minimum team size for the court,",
                    usage = "<court name> <team size>",
                    min = 1)
            public static void minTeamSize(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setMinTeamSize(args.getInteger(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set minimum team size.");
                    } else {
                        manager.getCourt(args.getString(0)).setMinTeamSize(1);
                        sender.sendMessage(ChatColor.YELLOW + "Reset minimum team size to default (1).");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"maxteamsize"},
                    desc = "Set the maximum team size for the court,",
                    usage = "<court name> <team size>",
                    min = 1)
            public static void maxTeamSize(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setMaxTeamSize(args.getInteger(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set maximum team size.");
                    } else {
                        manager.getCourt(args.getString(0)).setMaxTeamSize(6);
                        sender.sendMessage(ChatColor.YELLOW + "Reset maximum team size to default (6).");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

            @Command(aliases = {"inviterange"},
                    desc = "Set the range within which people are invited to play,",
                    usage = "<court name> <invite range>",
                    min = 1)
            public static void inviteRange(CommandContext args, CommandSender sender) {
                VManager manager = VolleyballPlugin.getInstance().getManager();
                if (manager.getCourt(args.getString(0)) != null) {
                    if (args.argsLength() > 1) {
                        manager.getCourt(args.getString(0)).setInviteRange(args.getInteger(1));
                        sender.sendMessage(ChatColor.YELLOW + "Set invite range.");
                    } else {
                        manager.getCourt(args.getString(0)).setInviteRange(-1);
                        sender.sendMessage(ChatColor.YELLOW + "Reset invite range to unlimited.");
                    }
                } else {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                }
            }

        }
