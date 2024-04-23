package me.xorgon.volleyball.schedulers;

import me.xorgon.volleyball.VManager;
import me.xorgon.volleyball.effects.BallTrailEffect;
import me.xorgon.volleyball.events.BallLandEvent;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BallChecker implements Runnable {

    private VManager manager;

    public BallChecker(VManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Court court : manager.getCourts().values()) {
            if (court.getBall() != null) {
                if (court.getBall().isOnGround()
                        || court.getBall().getLocation().getY() < court.getY()
                        || court.hasLanded()
                        || court.getBall().getLocation().getBlock().isLiquid()
                        || court.getBall().getLocation().distance(court.getCenter()) > 10 * court.getCenter(Court.Team.RED).distance(court.getCenter(Court.Team.BLUE))) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), new BallRemovalScheduler(court), 2L);
                }
            }
        }
    }

    public class BallRemovalScheduler implements Runnable {

        private Court court;

        public BallRemovalScheduler(Court court) {
            this.court = court;
        }

        @Override
        public void run() {
            if (court.getBall().isOnGround()
                    || court.getBall().getLocation().getY() < court.getY()
                    || court.hasLanded()
                    || court.getBall().getLocation().getBlock().isLiquid()
                    || court.getBall().getLocation().distance(court.getCenter()) > 10 * court.getCenter(Court.Team.RED).distance(court.getCenter(Court.Team.BLUE))) {
                // Rimuovi la palla dal mondo o imposta la sua visibilità a false

                court.removeBall();
                for (Player player : court.getPlayers()) {
                    player.sendTitle(ChatColor.YELLOW +"Attendi la decisione dell'albitro...", "", 10, 70, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }

                Bukkit.getPluginManager().callEvent(new BallLandEvent(court));
            }
        }
    }
}

