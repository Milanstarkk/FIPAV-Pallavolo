package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import me.xorgon.volleyball.VManager;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VBAddPoint {

    @Command(aliases = {"addpoint"}, desc = "Aggiungi un punto alle squadre!", usage = "<court> <red or blue>", min = 2, max = 2)
    @CommandPermissions({"fipav.arbitro", "vb.admin"})
    public static void addPoint(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;
        VManager manager = VolleyballPlugin.getInstance().getManager();
        Court court = manager.getCourt(args.getString(0));

        if (court != null) {
            String team = args.getString(1);

            if (team.equalsIgnoreCase("red")) {
                court.setRedScore(court.getRedScore() + 1);
                player.sendMessage(ChatColor.YELLOW + "Un punto è stato aggiunto alla squadra rossa nel campo " + court.getName() + ".");
            } else if (team.equalsIgnoreCase("blue")) {
                court.setBlueScore(court.getBlueScore() + 1);
                player.sendMessage(ChatColor.YELLOW + "Un punto è stato aggiunto alla squadra blu nel campo " + court.getName() + ".");
            } else {
                player.sendMessage(ChatColor.RED + "Squadra non valida. Si prega di specificare 'red' o 'blue'.");
            }
        } else {
            sender.sendMessage(manager.messages.getCourtDoesNotExistMessage());
        }
    }
}
