package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import me.xorgon.volleyball.VManager;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.xorgon.volleyball.objects.Court.setBlueScore;
import static me.xorgon.volleyball.objects.Court.setRedScore;

public class VBFischietto {

    public static class VolleyballRootCommand {

        @Command(aliases = {"fischietto", "fischio"}, desc = "Il comando per gli arbitri!")
        @NestedCommand(value = {FGiveball.class})
        public static void fischietto(CommandContext args, CommandSender sender) {
        }
    }

    private static Court.Team turn;

    private static int blueScore = 0;
    private static int redScore = 0;

    static VManager manager = VolleyballPlugin.getInstance().getManager();

    public static  class FFoul {
        @Command(aliases = {"fallo"}, desc = "Assegna fallo a una squadra", usage = "<court> <red or blue>", min = 2, max = 2)
        @CommandPermissions({"fipav.arbitro", "vb.admin"})
        public static void Foul(CommandContext args, CommandSender sender) {
            if (!(sender instanceof Player)) {
                return;
            }

            String courtName = args.getString(0);
            String teamName = args.getString(1);
            String foultype = args.getString(2);
            Player player = (Player) sender;
            Location location = player.getLocation();
            Court court = manager.getCourt(courtName);

    }

    public static class FGiveball {
        @Command(aliases = {"giveball"}, desc = "Give a point to a team and decide to continue the game", usage = "<court> <red or blue>", min = 2, max = 2)
        @CommandPermissions({"fipav.arbitro", "vb.admin"})
        public static void givePoint(CommandContext args, CommandSender sender) {
            if (!(sender instanceof Player)) {
                return;
            }

            String courtName = args.getString(0);
            String teamName = args.getString(1);
            Player player = (Player) sender;
            Location location = player.getLocation();

            Court court = manager.getCourt(courtName);
            if (teamName.equalsIgnoreCase("red")) {
                if (court != null) {
                    redScore++;
                    player.sendMessage(ChatColor.YELLOW + "Un punto è stato aggiunto alla squadra rossa " + court.getName() + ".");
                    player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 1F);
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
            setRedScore(redScore);
            setBlueScore(blueScore);
        }

    }
}
