package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.objects.Court;
import me.xorgon.volleyball.VManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class VBGiveBall {

    private static Court.Team turn;

    static VManager manager = VolleyballPlugin.getInstance().getManager();

    @Command(aliases = {"give"}, desc = "Give a point to a team and decide to continue the game", usage = "<court> <red or blue>", min = 2, max = 2)
    @CommandPermissions({"vb.admin"})
    public static void givePoint(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }

        String courtName = args.getString(0);
        String teamName = args.getString(1);
        Player player = (Player) sender;

        Court court = manager.getCourt(courtName);
        if (teamName.equalsIgnoreCase("red")) {
            if (court != null) {
                court.spawnBall(court.getCenter(Court.Team.RED));
                court.setLastHitBy(Court.Team.RED);

                boolean waitingForRefereeDecision = false;
                player.sendMessage(ChatColor.YELLOW + "Il servizio è stato concesso alla squadra " + ChatColor.RED + "Rosso");
            } else {
                player.sendMessage(ChatColor.RED + "Campo di gioco non trovato");
            }
        } else if (teamName.equalsIgnoreCase("blue")) {
            if (court != null) {
                court.spawnBall(court.getCenter(Court.Team.BLUE));
                court.setLastHitBy(Court.Team.BLUE);

                boolean waitingForRefereeDecision = false;
                player.sendMessage(ChatColor.YELLOW + "Il servizio è stato concesso alla squadra " + ChatColor.BLUE + "Blu");
            } else {
                player.sendMessage(ChatColor.RED + "Campo di gioco non trovato");
            }
        }

        }

    }
