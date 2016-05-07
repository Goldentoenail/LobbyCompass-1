package de.Dreieck52.LobbyCompass;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyCompass
extends JavaPlugin implements Listener{
    public static String prefix = "\u00a7f[\u00a7eCompass\u00a7f] ";
    public static String use_Permission = "lobbycompass.use";
    public static String get_Permission = "lobbycompass.get";
    public static String admin_Permission = "lobbycompass.admin";

    public void onEnable() {
        this.getCommand("lobbycompass").setExecutor((CommandExecutor)new LobbyCompassCommand((Plugin)this));
        Bukkit.getPluginManager().registerEvents((Listener)new LobbyCompassCommand((Plugin)this), (Plugin)this);
        
        this.getConfig().options().header("#############################################\n# - LobbyCompass Updated By FrostedGC & Goldentoenail - #\n#############################################\n# Option 'executedByPlayer' means:\n#    > true = Command is executed as the player typed it\n#    > false = Command is executed by console (%n% is player name)\n# There can be only at most 6 lines in one inventory!\n# If get-compass-on-join-slot is -1 it will add the compass in a free slot\n# LobbyCompass handles inventories starting at 0-8 not 1-9!\n# ############################################\n");
        this.getConfig().addDefault("compass-name", (Object)"&f&lClick this to open warp menu!");
        this.getConfig().addDefault("compass-item", (Object)"COMPASS");
        this.getConfig().addDefault("compass-inventory-name", (Object)"&a&lAWESOME Warp-Compass Oo");
        this.getConfig().addDefault("get-compass-on-join", (Object)true);
        this.getConfig().addDefault("get-compass-on-join-slot", (Object)4);
        this.getConfig().addDefault("get-compass-worlds", (Object)new String[]{"world", "world_nether", "world_the_end"});
        this.getConfig().addDefault("only-allow-command-lc_get-in-worlds", (Object)true);
        this.getConfig().addDefault("can-drop-compass", (Object)false);
        this.getConfig().addDefault("inventory-lines-amount", (Object)4);
        this.getConfig().addDefault("sound-effect", (Object)true);
        this.getConfig().addDefault("options", (Object)new String[]{"spawn", "pvp"});
        this.getConfig().addDefault("data.spawn.name", (Object)"&e&lSpawn / Lobby");
        this.getConfig().addDefault("data.spawn.lore", (Object)new String[]{"&7Click this Item to execute command", "&8&l/warp spawn", "&7another line :D"});
        this.getConfig().addDefault("data.spawn.item", (Object)"GRASS");
        this.getConfig().addDefault("data.spawn.cmd", (Object)"warp spawn");
        this.getConfig().addDefault("data.spawn.executedByPlayer", (Object)true);
        this.getConfig().addDefault("data.spawn.position-in-inventory", (Object)10);
        this.getConfig().addDefault("data.pvp.name", (Object)"&c&lPvP Zone");
        this.getConfig().addDefault("data.pvp.lore", (Object)new String[]{"&cD&ei&as&bc&do", "&8&l/pvp <player> join"});
        this.getConfig().addDefault("data.pvp.item", (Object)"IRON_SWORD");
        this.getConfig().addDefault("data.pvp.cmd", (Object)"pvp %n% join");
        this.getConfig().addDefault("data.pvp.executedByPlayer", (Object)false);
        this.getConfig().addDefault("data.pvp.position-in-inventory", (Object)19);
        this.getConfig().options().copyHeader(true);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();
        this.getLogger().info("LobbyCompass Has Been Enabled!");
    }

    public void onDisable() {
    	this.getLogger().info("LobbyCompass Has Been Disabled!");
    }
}