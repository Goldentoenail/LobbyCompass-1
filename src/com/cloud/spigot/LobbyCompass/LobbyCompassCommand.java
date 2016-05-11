package com.cloud.spigot.LobbyCompass;

import com.cloud.spigot.LobbyCompass.LobbyCompass;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class LobbyCompassCommand
implements CommandExecutor,
Listener {
    public Plugin pl;
	public String prefix = this.textToColour("&f[&eCompass&f] ");
	
    public LobbyCompassCommand(Plugin plugin) throws IOException {
        this.pl = plugin;
    }

    public String textToColour(String ttc) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)ttc);
    }
    
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
    	if(this.pl.getConfig().getString("lc-prefix") != null){
    		String lcprefix = this.textToColour(this.pl.getConfig().getString("lc-prefix"));
    		prefix = lcprefix;
    	}
        if (cs instanceof Player) {
            Player p = (Player)cs;
            if (args.length == 0 && p.hasPermission(LobbyCompass.use_Permission)) {
                this.openLobbyCompassOnPlayer(p);
            } else if (args[0].equals("help") || args[0].equals("info")) {
                p.sendMessage(prefix + "\u00a7e---------- LobbyCompass ----------");
                p.sendMessage(prefix + "\u00a7e> Plugin Updated By FrostedGC \u00a7<");
                p.sendMessage(prefix + "\u00a7e----------------------------------");
            } else if (args[0].equals("get") && (p.hasPermission(LobbyCompass.get_Permission) || p.hasPermission(LobbyCompass.admin_Permission))) {
                if (this.pl.getConfig().getBoolean("only-allow-command-lc_get-in-worlds") && !this.pl.getConfig().getStringList("get-compass-worlds").contains(p.getWorld().getName())) {
                    p.sendMessage(prefix + "\u00a7eThe Lobby-Compass is disabled for this world!");
                    return true;
                }
                if (args[0].equals("get") && p.getInventory().contains(this.configuratedCompass()) || args[0].equals("get") && p.getInventory().getItemInOffHand().equals((Object)this.configuratedCompass())) {
                    p.sendMessage(prefix + "\u00a7eYou already have a Lobby-Compass!");
                    return true;
                }
                p.sendMessage(prefix + "\u00a7eGiving you your Lobby-Compass!");
                p.getInventory().addItem(new ItemStack[]{this.configuratedCompass()});
            } else if (args[0].equals("reload") && p.hasPermission(LobbyCompass.admin_Permission)) {
                this.pl.reloadConfig();
                p.sendMessage(prefix + "\u00a7aConfig reloaded!");
            } else if (args[0].equals("version") && p.hasPermission(LobbyCompass.admin_Permission)) {
                p.sendMessage(prefix + "\u00a7aRunning Version 2.2!");
            } else {
                cs.sendMessage(prefix + "\u00a7cNot valid arguments or to few permissions.");
            }
        } else {
            cs.sendMessage(prefix + "\u00a7cYou're not a valid player.");
        }
        return true;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player p = event.getPlayer();
            ItemStack compass = this.configuratedCompass();
            try {
                if (p.getInventory().getItemInMainHand().getType() == Material.getMaterial((String)this.pl.getConfig().getString("compass-item")) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-name"))) && p.hasPermission(LobbyCompass.use_Permission)) {
                    event.setCancelled(true);
                    this.openLobbyCompassOnPlayer(p);
                } else {
                    event.setCancelled(true);
                }
            }
            catch (Exception var3_3) {
                System.out.println("[LobbyCompass] A open-compass error has been thrown. Please check your config or contact a developer if the problem persist.");
            }
        }
    }

    public void openLobbyCompassOnPlayer(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, (int)(this.pl.getConfig().getInt("inventory-lines-amount") * 9), (String)ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-inventory-name")));
        List options = this.pl.getConfig().getStringList("options");
        for (Object option : options) {
            ItemStack istack = new ItemStack(Material.getMaterial((String)this.pl.getConfig().getString("data." + option + ".item")));
            ItemMeta imeta = istack.getItemMeta();
            imeta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("data." + option + ".name")));
            List lores = this.pl.getConfig().getStringList("data." + option + ".lore");
            ArrayList<String> newLores = new ArrayList<String>();
            for (Object lore : lores) {
                newLores.add(ChatColor.translateAlternateColorCodes((char)'&', (String)((String)lore)));
            }
            imeta.setLore(newLores);
            istack.setItemMeta(imeta);
            inv.setItem(this.pl.getConfig().getInt("data." + option + ".position-in-inventory"), istack);
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void onClickOnItem(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player)event.getWhoClicked();
            if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-inventory-name"))) && event.getSlot() == event.getRawSlot()) {
                event.setCancelled(true);
                try {
                    if (event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().hasItemMeta()) {
                        ItemStack invstack = event.getCurrentItem();
                        List options = this.pl.getConfig().getStringList("options");
                        for (Object option : options) {
                            if (invstack.getType() != Material.getMaterial((String)this.pl.getConfig().getString("data." + option + ".item")) || !invstack.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("data." + option + ".name")))) continue;
                            String cmd = this.pl.getConfig().getString("data." + option + ".cmd");
                            if (this.pl.getConfig().getBoolean("sound-effect")) {
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 7.0f, 1.0f);
                            }
                            if (this.pl.getConfig().getBoolean("data." + option + ".executedByPlayer")) {
                                Bukkit.getServer().dispatchCommand((CommandSender)p, cmd);
                                break;
                            }
                            System.out.println("[LobbyCompass] Next command executed by LobbyCompassPlugin of Player " + p.getName());
                            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)cmd.replace("%n%", p.getName()));
                            break;
                        }
                        p.closeInventory();
                    }
                }
                catch (Exception invstack) {
                    System.out.println("[LobbyCompass] A click-item error has been thrown. Please check your config or contact a developer if the problem persist.");
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack compass = event.getItemDrop().getItemStack();
        Player p = event.getPlayer();
        if (compass.isSimilar(compass) && compass.hasItemMeta() && compass.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-name"))) && !this.pl.getConfig().getBoolean("can-drop-compass")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (this.pl.getConfig().getBoolean("get-compass-on-join") && this.pl.getConfig().getStringList("get-compass-worlds").contains(p.getWorld().getName())) {
            ItemStack compass = this.configuratedCompass();
            if (!p.getInventory().contains(compass) && this.pl.getConfig().getInt("get-compass-on-join-slot") >= 0) {
                p.getInventory().setItem(this.pl.getConfig().getInt("get-compass-on-join-slot"), compass);
                System.out.println("[LobbyCompass] " + p.getName() + " does not have a compass. Giving it now.");
            }
        } else {
            System.out.println("[LobbyCompass] A player join error has been thrown. Please check your config or contact a developer if the problem persist.");
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        ItemStack compass = this.configuratedCompass();
        ItemMeta compassMeta = compass.getItemMeta();
        if (this.pl.getConfig().getStringList("get-compass-worlds").contains(p.getWorld().getName()) && !p.getInventory().contains(this.configuratedCompass()) && !p.getInventory().getItemInOffHand().isSimilar(compass)) {
            p.getInventory().addItem(new ItemStack[]{this.configuratedCompass()});
            System.out.println("[LobbyCompass] " + p.getName() + " does not have a compass. Giving it now.");
        }
        if (!this.pl.getConfig().getStringList("get-compass-worlds").contains(p.getWorld().getName()) && (p.getInventory().contains(this.configuratedCompass()) || p.getInventory().getItemInOffHand().isSimilar(compass))) {
            p.getInventory().removeItem(new ItemStack[]{this.configuratedCompass()});
            System.out.println("[LobbyCompass] " + p.getName() + " has entered a non-compass world. Removing compass or making it invalid now.");
        }
    }

    public ItemStack configuratedCompass() {
        ItemStack compass = new ItemStack(Material.getMaterial((String)this.pl.getConfig().getString("compass-item")));
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-name")));
        compassMeta.addEnchant(Enchantment.DURABILITY, 0, false);
        compassMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        compass.setItemMeta(compassMeta);
        return compass;
    }
}

