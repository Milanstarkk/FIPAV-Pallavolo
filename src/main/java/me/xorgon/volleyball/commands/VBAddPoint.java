package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
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

public class VBAddPoint {
    private static int blueScore = 0;
    private static int redScore = 0;

    @Command(aliases = {"addpoint"}, desc = "Aggiungi un punto alle squadre!", usage = "<court> <red or blue>", min = 2, max = 2)
    @CommandPermissions({"fipav.arbitro", "vb.admin"})
    public static void addPoint(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;
        VManager manager = VolleyballPlugin.getInstance().getManager();
        Court court = manager.getCourt(args.getString(0));
        Location location = player.getLocation();

        if (court != null) {
            String team = args.getString(1);

            if (team.equalsIgnoreCase("red")) {
                redScore++;
                player.sendMessage(ChatColor.YELLOW + "Un punto è stato aggiunto alla squadra rossa " + court.getName() + ".");
                player.playSound(location,Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 1F);
            } else if (team.equalsIgnoreCase("blue")) {
                blueScore++;
                player.sendMessage(ChatColor.YELLOW + "Un punto è stato aggiunto alla squadra blu " + court.getName() + ".");
                player.playSound(location,Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 1F);
            } else {
                player.sendMessage(ChatColor.RED + "Squadra non valida. Si prega di specificare 'red' o 'blue'.");
            }
        } else {
            sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
        }
        setRedScore(redScore);
        setBlueScore(blueScore);
    }
}
