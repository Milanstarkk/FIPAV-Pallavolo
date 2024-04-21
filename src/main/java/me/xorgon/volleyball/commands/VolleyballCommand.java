package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import de.slikey.effectlib.EffectManager;
import me.xorgon.volleyball.VManager;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.effects.RomanCandleEffect;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VolleyballCommand {

    public static class VolleyballRootCommand {

        @Command(aliases = {"volleyball", "vb"}, desc = "The root Volleyball command.")
        @NestedCommand(value = {VolleyballCommand.class, VBSetCommand.VBSetRootCommand.class, VBAddPoint.class, VBGiveBall.class})
        public static void volleyball(CommandContext args, CommandSender sender) {
        }
    }

    @Command(aliases = {"addcourt", "ac", "createcourt", "add"},
            desc = "Create a court to be set up.",
            usage = "<court name>",
            min = 1,
            max = 1)
    @CommandPermissions("vb.admin")
    public static void addCourt(CommandContext args, CommandSender sender) {
        VolleyballPlugin.getInstance().getManager().addCourt(args.getString(0).toLowerCase());
        sender.sendMessage(ChatColor.YELLOW + "Created court.");
    }

    @Command(aliases = {"removecourt", "remove"},
            desc = "Remove a court from the config.",
            usage = "<court name>",
            min = 1,
            max = 1)
    @CommandPermissions("vb.admin")
    public static void removeCourt(CommandContext args, CommandSender sender) {
        VolleyballPlugin.getInstance().getManager().removeCourt(args.getString(0).toLowerCase());
        sender.sendMessage(ChatColor.YELLOW + "Removed court.");
    }

    @Command(aliases = {"listcourts", "list"}, desc = "")
    @CommandPermissions("vb.admin")
    public static void listcourts(CommandContext args, CommandSender sender) {
        String message = ChatColor.YELLOW + "Courts: ";
        for (Court court : VolleyballPlugin.getInstance().getManager().getCourts().values()) {
            message = message.concat(court.getName() + ", ");
        }
        sender.sendMessage(message.substring(0, message.length() - 2));
    }

    @Command(aliases = {"help"}, desc = "Basic instructions on how to play volleyball.")
    @CommandPermissions("vb.user")
    public static void help(CommandContext args, CommandSender sender) {
        String helpMessage = VolleyballPlugin.getInstance().getManager().messages.getHelpMessage();
        if (!helpMessage.isEmpty()) {
            sender.sendMessage(helpMessage);
        }
    }

    @Command(aliases = {"join"}, desc = "Join the specified volleyball court.", usage = "<court name> ", min = 1, max = 1)
    @CommandPermissions("vb.tp")
    public static void join(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            VManager manager = VolleyballPlugin.getInstance().getManager();
            if (manager.getCourts().containsKey(args.getString(0))) {
                Court court = manager.getCourt(args.getString(0));
                if (court.getWorldName() != null && court.getWorld() == null) {
                    player.sendMessage(manager.messages.getWorldNotLoadedMessage());
                } else if (!court.isInitialized()) {
                    player.sendMessage(manager.messages.getCourtNotReadyMessage());
                } else {
                    int redSize = court.getRedPlayers().size();
                    int blueSize = court.getBluePlayers().size();
                    if (redSize < blueSize && redSize < court.getMaxTeamSize()) {
                        player.teleport(court.getCenter(Court.Team.RED));
                        if (player.isFlying()) player.setFlying(false);
                    } else if (blueSize < court.getMaxTeamSize()) {
                        player.teleport(court.getCenter(Court.Team.BLUE));
                        if (player.isFlying()) player.setFlying(false);
                    } else {
                        Vector redVec = court.getCenter(Court.Team.RED).toVector();
                        Vector blueVec = court.getCenter(Court.Team.BLUE).toVector();
                        Vector mid = redVec.midpoint(blueVec);
                        Vector across = redVec.clone().subtract(blueVec);
                        mid.add(new Vector(0, 1, 0).crossProduct(across.clone().multiply(1 / across.length())).multiply(across.length()));
                        String fullGameMessage = manager.messages.getFullGameMessage();
                        if (!fullGameMessage.isEmpty()) {
                            player.sendMessage(fullGameMessage);
                        }
                        player.teleport(mid.toLocation(court.getWorld()));
                        if (player.isFlying()) player.setFlying(false);
                    }
                }
            } else {
                player.sendMessage(manager.messages.getCourtDoesNotExistMessage());
            }
        }
    }

    @Command(aliases = {"start"},
            desc = "Start a match on specified court.",
            usage = "<court name>",
            max = 1)
    @CommandPermissions("vb.admin")
    public static void start(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            VManager manager = VolleyballPlugin.getInstance().getManager();
            Court court;
            if (args.argsLength() == 0) {
                court = manager.getCourt((Player) sender);
                if (court == null) {
                    sender.sendMessage(ChatColor.RED + "You are not within a court and you have not specified a court.");
                    return;
                }
            } else {
                court = manager.getCourt(args.getString(0));
                if (court == null) {
                    sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
                    return;
                }
            }
            if (court.isInitialized()) {
                court.startGame(true);
            } else if (court.getWorld() == null && court.getWorldName() != null) {
                sender.sendMessage(manager.messages.getWorldNotLoadedMessage());
            } else {
                sender.sendMessage(manager.messages.getCourtNotReadyMessage());
            }
        }
    }

    @Command(aliases = {"end"},
            desc = "End a match on specified court.",
            usage = "<court name>",
            max = 1)
    @CommandPermissions("vb.admin")
    public static void end(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            VManager manager = VolleyballPlugin.getInstance().getManager();
            if (args.argsLength() == 0) {
                Player player = (Player) sender;
                if (manager.isInCourt(player)) {
                    manager.getCourt(player).endGame();
                } else {
                    sender.sendMessage(manager.messages.getNotInCourtMessage());
                }
            } else {
                manager.getCourt(args.getString(0)).endGame();
            }

        }
    }

    @Command(aliases = {"spawn"}, desc = "Spawn a volleyball.")
    @CommandPermissions("vb.admin")
    public static void spawn(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            VManager manager = VolleyballPlugin.getInstance().getManager();
            Player player = (Player) sender;
            if (manager.getCourt(player) != null) {
                Location loc = player.getLocation().add(0, 1.25, 0);
                manager.getCourt(player).spawnBall(loc);
            } else {
                player.sendMessage(manager.messages.getNotInCourtMessage());
            }
        }
    }

    @Command(aliases = {"testbound"}, desc = "Tells you if you're inside a court.")
    @CommandPermissions("vb.admin")
    public static void testBound(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            VManager manager = VolleyballPlugin.getInstance().getManager();
            if (manager.isInCourt(player)) {
                player.sendMessage(ChatColor.YELLOW + "You are in court " + ChatColor.LIGHT_PURPLE + manager.getCourt(player).getName());
            } else {
                player.sendMessage(manager.messages.getNotInCourtMessage());
            }
        }
    }

    @Command(aliases = {"romancandle", "rc"}, desc = "Launch a roman candle.")
    @CommandPermissions("vb.admin")
    public static void romanCandle(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            EffectManager effectManager = VolleyballPlugin.getInstance().getEffectManager();
            VManager manager = VolleyballPlugin.getInstance().getManager();
            Player player = (Player) sender;
            Color color;
            if (manager.isInCourt(player)) {
                color = manager.getCourt(player).getSide(player.getLocation()) == Court.Team.RED ? Color.RED : Color.BLUE;
            } else {
                color = Color.PURPLE;
            }
            double height;
            if (args.argsLength() > 0) {
                height = args.getDouble(0);
            } else {
                height = 3.;
            }
            RomanCandleEffect effect = new RomanCandleEffect(effectManager, player.getLocation(), color, height);
            effect.start();
        }
    }
    @Command(aliases = {"clear"}, desc = "Remove all volleyballs.")
    public static void clear(CommandContext args, CommandSender sender) {
        VolleyballPlugin.getInstance().getManager().clearVolleyballs();
        sender.sendMessage(ChatColor.YELLOW + "Removed volleyballs.");
    }
}


