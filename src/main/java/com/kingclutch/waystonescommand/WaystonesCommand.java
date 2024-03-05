package com.kingclutch.waystonescommand;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;

import thito.fancywaystones.event.WaystoneActivateEvent;
import thito.fancywaystones.event.WaystoneDestroyEvent;
import thito.fancywaystones.event.WaystonePlaceEvent;

public class WaystonesCommand extends JavaPlugin implements Listener {

    //private static final int SAVE_INTERVAL = 60; // 60 ticks, adjust as needed
    private static final int RELOAD_INTERVAL = 80;

    @Override
    public void onEnable() {
    // Register the event listener
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        saveDefaultConfig();

        // Schedule the configuration save task to run every 60 ticks (3 seconds)
        //Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveConfigTask, 0L, SAVE_INTERVAL);

        // Schedule the configuration reload task to run every RELOAD_INTERVAL ticks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::reloadConfigTask, 0L, RELOAD_INTERVAL);
    }

    private void saveConfigTask() {
        // Save the configuration
        saveConfig();
    }

    private void reloadConfigTask() {
        // Reload the configuration
        reloadConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @EventHandler
    public void onWaystoneActivate(WaystoneActivateEvent event) {
        // Retrieve the UUID of the activated waystone
        String waystoneUUID = event.getWaystoneData().getUUID().toString();

        // Check if the UUID is present in the configuration
        if (getConfig().contains("waystones." + waystoneUUID)) {
            // Retrieve the command associated with the UUID
            String commandToExecute = getConfig().getString("waystones." + waystoneUUID + ".command");

            // Check if the command is not null
            if (commandToExecute != null && !commandToExecute.isEmpty() && !commandToExecute.equals("default_command_here")) {
                // Execute the command
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
            }
        }
    }

    @EventHandler
    public void onWaystonePlace(WaystonePlaceEvent event) {
        // Generate a new UUID for the waystone
        String waystoneUUID = event.getWaystoneData().getUUID().toString();

        // Add the UUID and a default command to the config
        ConfigurationSection waystoneSection = getConfig().createSection("waystones." + waystoneUUID);
        waystoneSection.set("command", "default_command_here");

        // Add comments with waystone name and coordinates
        waystoneSection.set("name", event.getWaystoneData().getName());
        waystoneSection.set("coordinates", "X: " + event.getLocation().getBlockX() +
                ", Y: " + event.getLocation().getBlockY() +
                ", Z: " + event.getLocation().getBlockZ());

        saveConfig();
    }

    @EventHandler
    public void onWaystoneDestroy(WaystoneDestroyEvent event) {
        // Get the UUID of the destroyed waystone
        String waystoneUUID = event.getWaystoneData().getUUID().toString();

        // Remove the UUID and its associated command from the config
        getConfig().set("waystones." + waystoneUUID, "OLD"+waystoneUUID);
        saveConfig();
    }
}
