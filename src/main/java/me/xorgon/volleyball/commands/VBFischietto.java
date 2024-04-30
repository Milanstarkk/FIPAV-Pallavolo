package me.xorgon.volleyball.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import me.xorgon.volleyball.VManager;
import me.xorgon.volleyball.VolleyballPlugin;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.xorgon.volleyball.objects.Court.setBlueScore;
import static me.xorgon.volleyball.objects.Court.setRedScore;

public class VBFischietto {

    public static class FischiettoRootCommand {

        @Command(aliases = {"fischietto", "fischio"}, desc = "Il comando per gli arbitri!")
        @NestedCommand(value = {FFoul.class, FGiveball.class})
        public static void fischietto(CommandContext args, CommandSender sender) {
        }
    }

    private static Court.Team turn;

    private static int blueScore = 0;
    private static int redScore = 0;

    static VManager manager = VolleyballPlugin.getInstance().getManager();

    public static class FFoul {
        @Command(aliases = {"fallo"}, desc = "Assegna fallo a una squadra", usage = "<court> <red or blue> <foul type>")
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

            if (teamName.equalsIgnoreCase("red")){
                if (court != null){
                    if (foultype != null) {
                        court.sendAllPlayersMessage("§b§lARBITRO §f§l• §e§lFALLO");
                        court.sendAllPlayersMessage("");
                        court.sendAllPlayersMessage("§7L'arbitro ha rilevato un fallo!");
                        court.sendAllPlayersMessage("§b§l• §fSquadra fallosa: §cRossa");
                        court.sendAllPlayersMessage("§b§l• §fTipo di fallo: §f" + args.getString(2));
                        court.spawnBall(court.getCenter(Court.Team.BLUE));
                        court.setLastHitBy(Court.Team.BLUE);

                        boolean waitingForRefereeDecision = false;
                        player.sendMessage(ChatColor.YELLOW + "Il servizio è stato concesso alla squadra " + ChatColor.BLUE + "Blu");
                    } else {
                        player.sendMessage("§cSeleziona un tipo di fallo per continuare");
                    }
                } else {
                    player.sendMessage("§cSeleziona un campo valido per poter continuare.");
                }
            } else if (teamName.equalsIgnoreCase("blue")){
                if (court != null){
                    if (foultype != null) {
                        court.sendAllPlayersMessage("§b§lARBITRO §f§l• §e§lFALLO");
                        court.sendAllPlayersMessage("");
                        court.sendAllPlayersMessage("§7L'arbitro ha rilevato un fallo!");
                        court.sendAllPlayersMessage("§b§l• §fSquadra fallosa: §9Blu");
                        court.sendAllPlayersMessage("§b§l• §fTipo di fallo: §f" + args.getString(2));
                        player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 1F);
                        court.spawnBall(court.getCenter(Court.Team.RED));
                        court.setLastHitBy(Court.Team.RED);

                        boolean waitingForRefereeDecision = false;
                        player.sendMessage(ChatColor.YELLOW + "Il servizio è stato concesso alla squadra " + ChatColor.RED + "Rosso");
                    } else {
                        player.sendMessage("§cSeleziona un tipo di fallo per continuare");
                    }
                } else {
                    player.sendMessage("§cSeleziona un campo valido per poter continuare.");
                }

            }

        }
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

    public static class FSanction{
        @Command(aliases = {"sanction"}, desc = "Give a point to a team and decide to continue the game", usage = "<court> <player> <ammonizione|espulsione> <motivo>")
        @CommandPermissions({"fipav.arbitro", "vb.admin"})
        public static void givePoint(CommandContext args, CommandSender sender) {
            if (!(sender instanceof Player)) {
                return;
            }

            String courtName = args.getString(0);
            String playerName = args.getString(1);
            String sanctionType = args.getString(2);
            String sanctionReason = args.getString(3);
            Player p = (Player) sender;
            Location location = p.getLocation();

            Court court = manager.getCourt(courtName);
            Player player = Bukkit.getPlayer(playerName);
            if (player == null || !player.isOnline()) {
                sender.sendMessage("§cIl giocatore specificato non è online.");
                return;
            }
            if (sanctionType.equalsIgnoreCase("ammonizione")){
                if (court != null){
                    if(playerName != null){
                        if (sanctionReason != null){
                            court.sendAllPlayersMessage("§b§lARBITRO §f§l• §e§lAMMONIZIONE");
                            court.sendAllPlayersMessage("");
                            court.sendAllPlayersMessage("§7L'arbitro ha ammonito un giocatore!");
                            court.sendAllPlayersMessage("§b§l• §fGiocatore ammonito: §e" + args.getString(1));
                            court.sendAllPlayersMessage("§b§l• §fMotivo: §f" + args.getString(3));
                            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 1F);
                            player.sendTitle(ChatColor.YELLOW +"Sei stato ammonito dall'arbitro!", "", 10, 70, 20);
                        } else {
                            p.sendMessage(ChatColor.RED + "Specifica un motivo dell'ammonizione");
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Campo di gioco non trovato");
                }
            } else if (sanctionType.equalsIgnoreCase("espulsione")) {
                if (court != null){
                    if(playerName != null){
                        if (sanctionReason != null){
                            court.sendAllPlayersMessage("§b§lARBITRO §f§l• §c§lESPULSIONE");
                            court.sendAllPlayersMessage("");
                            court.sendAllPlayersMessage("§7L'arbitro ha espulso un giocatore!");
                            court.sendAllPlayersMessage("§b§l• §fGiocatore espulso: §e" + args.getString(1));
                            court.sendAllPlayersMessage("§b§l• §fMotivo: §f" + args.getString(3));
                            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 1F);
                            player.sendTitle(ChatColor.RED +"Sei stato espulso dall'arbitro!", "", 10, 70, 20);
                            court.removePlayer(player);
                        } else {
                            p.sendMessage(ChatColor.RED + "Specifica un motivo dell'ammonizione");
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Campo di gioco non trovato");
                }

            }
        }
    }
}

