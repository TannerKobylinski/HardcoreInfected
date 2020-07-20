package io.github.tannerkobylinski.hardcoreinfected;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class EventsListener implements Listener {
    HardcoreInfected main;
    Infected infected;
    public EventsListener(HardcoreInfected instance, Infected inf) {
        this.main = instance;
        this.infected = inf;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        String playerName = player.getName();
        String key = Infected.getKey(playerName);
        FileConfiguration config = main.getConfig();

        if(!infected.isInfected(player)) {
            config.set(key, true);
            main.saveConfig();
        }
        infected.respawnInfected(player);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        main.getLogger().info("Player " + playerName + " has logged in!");

        if(infected.isInfected(player)) {
            infected.applyInfected(player);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        main.getLogger().info("Player " + playerName + " has respawned!");
        infected.applyInfected(player);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTarget(EntityTargetEvent event) {
        Entity targeted = event.getTarget();
        if(event.getTarget() instanceof Player) {
            Player player = (Player) targeted;
            if(infected.isInfected(player)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSprint(FoodLevelChangeEvent event) {
        Player player = ((Player) event.getEntity()).getPlayer();
        if(infected.isInfected(player)) {
            event.setCancelled(true);
        }
    }
}
