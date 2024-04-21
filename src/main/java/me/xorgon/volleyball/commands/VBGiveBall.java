package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.entity.Player;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class VBGiveBall {
    @Command(aliases = {"give"}, desc = "Give a point to a team and decide to continue the game", usage = "<court> <red or blue>", min = 2, max = 2)
    @CommandPermissions({"fipav.arbitro", "vb.admin"})
    public static void givePoint(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }

        String courtName = args.getString(0);
        String teamName = args.getString(1);
        Court.Team team = Court.Team.valueOf(teamName.toUpperCase());

        Court court = VolleyballPlugin.getInstance().getManager().getCourt(courtName);
        if (court != null) {
            court.refereeDecision(courtName, team);
        } else {
            sender.sendMessage(ChatColor.RED + "Il campo specificato non esiste.");
        }
    }

}
