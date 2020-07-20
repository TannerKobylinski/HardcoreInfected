package io.github.tannerkobylinski.hardcoreinfected;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardcoreInfected extends JavaPlugin {
    Infected infected;

    @Override
    public void onEnable() {
        getLogger().info("Hardcore Infected enabled!");
        infected = new Infected(this);
        getServer().getPluginManager().registerEvents(new EventsListener(this, infected), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Hardcore Infected disabled!");
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("cure")) {
            if (args.length != 1) {
                sender.sendMessage("Invalid # of arguments!");
                return false;
            }
            Player target = (Bukkit.getServer().getPlayer(args[0]));
            if (target == null) {
                sender.sendMessage(args[0] + " is not online!");
                return false;
            }
            infected.cureInfected(target);
            Bukkit.broadcastMessage("[HardcoreInfected] " + sender.getName() + " has CURED " + target.getName() + "!");
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("infect")) {
            if (args.length != 1) {
                sender.sendMessage("Invalid # of arguments!");
                return false;
            }
            Player target = (Bukkit.getServer().getPlayer(args[0]));
            if (target == null) {
                sender.sendMessage(args[0] + " is not online!");
                return false;
            }
            infected.applyInfected(target);
            Bukkit.broadcastMessage("[HardcoreInfected] " + sender.getName() + " has INFECTED " + target.getName() + "!");
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("setInfectedRespawnTime")) {
            if (args.length != 1) {
                sender.sendMessage("Invalid # of arguments!");
                return false;
            }
            int time = Integer.parseInt(args[0]);
            infected.setRespawnTime(time);
            Bukkit.broadcastMessage("[HardcoreInfected] Infected respawn time has been set to " + time + " seconds!");
            return true;
        }
        return false;
    }
}