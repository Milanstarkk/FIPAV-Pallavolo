package me.xorgon.volleyball;

import me.xorgon.volleyball.events.BallLandEvent;
import me.xorgon.volleyball.objects.Court;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class VListener implements Listener {

    VolleyballPlugin plugin = VolleyballPlugin.getInstance();
    VManager manager = plugin.getManager();

    @EventHandler
    public void onSlimeHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && manager.isVolleyball(event.getEntity())) {
            event.setCancelled(true);
            Slime ball = (Slime) event.getEntity();
            Player player = (Player) event.getDamager();
            Court court = manager.getCourt(ball);
            if (court.getTeam(player) != Court.Team.NONE) {
                if (court.canHit()) {
                    Court.Team ballSide = court.getSide(ball.getLocation());
                    if (ballSide != court.getTeam(player) && ballSide != Court.Team.NONE) {
                        String wrongSideMessage = manager.messages.getWrongSideMessage();
                        if (!wrongSideMessage.isEmpty()) {
                            player.sendMessage(wrongSideMessage);
                        }
                        return;
                    }
                    if (court.getLastHitBy() == court.getTeam(player)) {
                        if (court.getHitCount() >= Court.MAX_HITS) {
                            String tooManyHitsMessage = manager.messages.getTooManyHitsMessage();
                            if (!tooManyHitsMessage.isEmpty()) {
                                player.sendMessage(tooManyHitsMessage);
                            }
                            return;
                        }
                    } else {
                        court.resetHitCount();
                    }
                    Vector dir = player.getLocation().getDirection();
                    if (!ball.hasGravity()) {
                        ball.setGravity(true);
                    }
                    double factor = 0.5;
                    if (player.isSprinting()) {
                        factor += 0.5;
                    }
                    if (!player.isOnGround()) {
                        factor += 0.5;
                    }
                    factor *= court.getPower();
                    ball.setAI(true);
                    ball.setVelocity(dir.clone().multiply(2.7).setY(dir.getY() + 1.1).multiply(factor));
                    court.setLastHitBy(court.getTeam(player));
                    court.incHitCount();
                    court.resetLandedMS();
                }
            }
        }
    }

    @EventHandler
    public void onSlimeDamage(EntityDamageEvent event) {
        if (manager.isVolleyball(event.getEntity())) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Court court = manager.getCourt((Slime) event.getEntity());
                court.setLandedMS();
                ((Slime) event.getEntity()).setAI(false);
            }
        }
    }

    @EventHandler
    public void onBallLand(BallLandEvent event) {
        event.getCourt().score(event.getScoringTeam());
    }


    private List<Player> teamFullSent = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (manager.isInCourt(player)) {
            Court court = manager.getCourt(player);
            if (court.isStarted() && court.getTeam(player) == Court.Team.NONE
                    || !player.hasPermission("vb.user")) {
                Vector centerToPlayer = player.getLocation().toVector().subtract(court.getCenter().toVector()).clone();
                player.setVelocity(centerToPlayer.setY(0).normalize().setY(1));

                if (!manager.isBouncedPlayer(player)) {
                    if (!player.hasPermission("vb.user")) {
                        String noPermissionsMessage = manager.messages.getNoPermissionsMessage();
                        if (!noPermissionsMessage.isEmpty()) player.sendMessage(noPermissionsMessage);
                    } else {
                        String matchStartedMessage = manager.messages.getMatchStartedMessage();
                        if (!matchStartedMessage.isEmpty()) player.sendMessage(matchStartedMessage);
                    }

                    manager.addBouncedPlayer(player);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> manager.removeBouncedPlayer(player), 100);
                }
                return;
            }
            if (court.isStarted()) {
                return;
            }

            // Not started
            String help = manager.messages.getClickForHelpMessage();
            BaseComponent[] helpMsg = new ComponentBuilder(help)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vb help"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(help)))
                    .create();
            if (court.getSide(player.getLocation()) == Court.Team.RED) {
                if (court.getRedPlayers().size() < court.getMaxTeamSize()) {
                    if (!court.getRedPlayers().contains(player)) {
                        if (court.getBluePlayers().contains(player)) {
                            court.removePlayer(player);
                        }
                        court.addPlayer(player, Court.Team.RED);
                        String joinedTeamMessage = manager.messages.getJoinedTeamMessage(Court.Team.RED);
                        TextComponent msg = new TextComponent(joinedTeamMessage);
                        if (!joinedTeamMessage.isEmpty()) player.spigot().sendMessage(msg);
                        if (!help.isEmpty()) player.spigot().sendMessage(helpMsg);
                    }
                } else {
                    if (!teamFullSent.contains(player)) {
                        String fullTeamMessage = manager.messages.getFullTeamMessage(Court.Team.RED);
                        if (!fullTeamMessage.isEmpty()) player.sendMessage(fullTeamMessage);
                        teamFullSent.add(player);
                    }
                }
            } else {
                if (court.getBluePlayers().size() < court.getMaxTeamSize()) {
                    if (!court.getBluePlayers().contains(player)) {
                        if (court.getRedPlayers().contains(player)) {
                            court.removePlayer(player);
                        }
                        court.addPlayer(player, Court.Team.BLUE);
                        String joinedTeamMessage = manager.messages.getJoinedTeamMessage(Court.Team.BLUE);
                        TextComponent msg = new TextComponent(joinedTeamMessage);
                        if (!joinedTeamMessage.isEmpty()) player.spigot().sendMessage(msg);
                        if (!help.isEmpty()) player.spigot().sendMessage(helpMsg);
                    }
                } else {
                    if (!teamFullSent.contains(player)) {
                        String fullTeamMessage = manager.messages.getFullTeamMessage(Court.Team.BLUE);
                        if (!fullTeamMessage.isEmpty()) player.sendMessage(fullTeamMessage);
                        teamFullSent.add(player);
                    }
                }
            }

            // Send invites
            if (!court.isStarting()) {

                String alertMsg;
                if (court.getDisplayName() != null) {
                    alertMsg = manager.messages.getMatchStartingWithNameMessage(court);
                } else {
                    alertMsg = manager.messages.getMatchStartingWithoutNameMessage();
                }

                String clickToJoinMessage = manager.messages.getClickToJoinMessage();

            }
        } else if (manager.isPlaying(player)) {
            Court court = manager.getPlayingIn(player);
            if (!court.isStarted()) {
                String leftGameMessage = manager.messages.getLeftGameMessage();
                if (!leftGameMessage.isEmpty()) {
                    player.sendMessage(leftGameMessage);
                }
                court.removePlayer(player);
            }
        } else teamFullSent.remove(player);
    }

    @EventHandler
    public void onPlayerDamagedByBall(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Slime) {
            if (manager.isVolleyball(event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        // Cancel the event if player is playing.
        if (manager.isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        // Cancel the event if player is playing.
        if (manager.isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (manager.isPlaying(event.getPlayer())) {
            manager.getPlayingIn(event.getPlayer()).removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        manager.getCourts().values().stream()
                .filter(court -> court.getWorld() == event.getWorld())
                .forEach(Court::resetCourt);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (Court court : manager.getCourts().values()) {
            if (court.getWorldName() != null
                    && court.getWorld() == null
                    && event.getWorld().getName().equals(court.getWorldName())) {
                court.setWorld(event.getWorld());
            }
        }
    }
}
