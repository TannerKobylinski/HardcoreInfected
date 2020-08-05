package io.github.tannerkobylinski.hardcoreinfected;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Infected {
    private int TIME_TO_INFECT;
    private int FIRE_RESISTANCE_ON_RESPAWN;
    HardcoreInfected main;
    Team team = null;
    public Infected(HardcoreInfected instance) {
        this.main = instance;
        TIME_TO_INFECT = 15;
        setRespawnTime(TIME_TO_INFECT);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Set<Team> teams = board.getTeams();
        for(Team team : teams) {
        	if(team.getName().equals("Infected")) {
        		this.team = team;
        	}
        }
        if(team == null) {
            this.team = board.registerNewTeam("Infected");
            this.team.setColor(ChatColor.GREEN);
            this.team.setDisplayName("INFECTED");
            this.team.setCanSeeFriendlyInvisibles(true);
            this.team.setAllowFriendlyFire(false);
        }
    }

    public void applyInfected(Player p) {
        String playerName = p.getName();
        this.team.addEntry(playerName);
        p.setWalkSpeed((float)0.15);
        p.setFoodLevel(6);
        p.setCanPickupItems(false);
//        p.setDisplayName(ChatColor.GREEN + "[INFECTED] " + ChatColor.RESET + playerName);
//        p.setPlayerListName(ChatColor.GREEN + "[INFECTED] " + ChatColor.RESET + playerName);
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[4]);
        inv.setHelmet(new ItemStack(Material.ZOMBIE_HEAD));
    }

    public void cureInfected(Player p) {
        String playerName = p.getName();
        this.team.removeEntry(playerName);
        String key = Infected.getKey(playerName);
        FileConfiguration config = main.getConfig();
        config.set(key, false);
        main.saveConfig();

        p.setWalkSpeed((float)0.20);
        p.setCanPickupItems(true);
//        p.setDisplayName(playerName);
//        p.setPlayerListName(playerName);
        PlayerInventory inv = p.getInventory();
        inv.setArmorContents(new ItemStack[4]);
    }

    public void respawnInfected(Player p) {
        String playerName = p.getName();
        Location deathLocation = p.getLocation();
        Boolean voidDeath = p.getLastDamageCause().getCause() == DamageCause.VOID;
        if(!voidDeath) {
            Bukkit.broadcastMessage("[HardcoreInfected] Player " + playerName + " will revive as INFECTED in " + TIME_TO_INFECT + " seconds!");
            p.setHealth(20);
            p.setGameMode(GameMode.SPECTATOR);
            Zombie z = p.getWorld().spawn(deathLocation, Zombie.class);
            z.setCustomName("[RESPAWNING] " + playerName);
            z.setCustomNameVisible(true);
            z.setInvulnerable(true);
            z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 127));

            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {
                    p.setGameMode(GameMode.SURVIVAL);
                    applyInfected(p);
                    z.remove();
                    p.teleport(deathLocation);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, FIRE_RESISTANCE_ON_RESPAWN, 127));
                }
            }, 20L * TIME_TO_INFECT);
        }
        else {
            Bukkit.broadcastMessage("[HardcoreInfected] Player " + playerName + " fell into the VOID!");
            applyInfected(p);
        }
    }

    public void setRespawnTime(int time) {
        FileConfiguration config = main.getConfig();
        config.set("infectedRespawnTime", time);
        main.saveConfig();
        TIME_TO_INFECT = time;
    }

    public int getRespawnTime() {
        FileConfiguration config = main.getConfig();
        int time = config.getInt("infectedRespawnTime");
        TIME_TO_INFECT = time;
        return time;
    }

    public Boolean isInfected(Player p) {
        String playerName = p.getName();
        String key = getKey(playerName);
        FileConfiguration config = main.getConfig();
        return !!config.getBoolean(key);
    }

    public static String getKey(String playerName) {
        return "players."+playerName+".infected";
    }
    
    
}
