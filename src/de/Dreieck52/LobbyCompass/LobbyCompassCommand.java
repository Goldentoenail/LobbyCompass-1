package de.Dreieck52.LobbyCompass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class LobbyCompassCommand implements CommandExecutor, Listener {
    private Plugin pl;

    public LobbyCompassCommand(Plugin plugin) {
        this.pl = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player)cs;
            if (args.length == 0 && p.hasPermission(LobbyCompass.use_Permission)) {
                this.openLobbyCompassOnPlayer(p);
            } else if (args[0].equals("help") || args[0].equals("info")) {
                p.sendMessage(LobbyCompass.prefix + "\u00a7e---------- LobbyCompass ----------");
                p.sendMessage(LobbyCompass.prefix + "\u00a7e> Plugin Updated By FrostedGC \u00a7<");
                p.sendMessage(LobbyCompass.prefix + "\u00a7e----------------------------------");
            } else if (args[0].equals("get") && (p.hasPermission(LobbyCompass.get_Permission) || p.hasPermission(LobbyCompass.admin_Permission))) {
                if (this.pl.getConfig().getBoolean("only-allow-command-lc_get-in-worlds") && !this.pl.getConfig().getStringList("get-compass-worlds").contains(p.getWorld().getName())) {
                    p.sendMessage(LobbyCompass.prefix + "\u00a7eThe Lobby-Compass is disabled for this world!");
                    return true;
                }
                p.sendMessage(LobbyCompass.prefix + "\u00a7eGiving you your Lobby-Compass!");
                p.getInventory().addItem(new ItemStack[]{this.configuratedCompass()});
            } else if (args[0].equals("reload") && p.hasPermission(LobbyCompass.admin_Permission)) {
                p.sendMessage(LobbyCompass.prefix + "\u00a7aConfig reloaded!");
                this.pl.reloadConfig();
            } else if (args[0].equals("version") && p.hasPermission(LobbyCompass.admin_Permission)) {
                p.sendMessage(LobbyCompass.prefix + "\u00a7aRunning Version 2.1!");
            }
            else {
                cs.sendMessage(LobbyCompass.prefix + "\u00a7cNot valid arguments or to few permissions.");
            }
        } else {
            cs.sendMessage(LobbyCompass.prefix + "\u00a7cYou're not a valid player.");
        }
        return true;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player p = event.getPlayer();
            try {
            	if (p.getInventory().getItemInMainHand().getType() == Material.getMaterial(this.pl.getConfig().getString("compass-item"))&& p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-name")))&& p.hasPermission(LobbyCompass.use_Permission)) {
                    event.setCancelled(true);
                    this.openLobbyCompassOnPlayer(p);
                }
            }
            catch (Exception var3_3) {
            }
        }
    }

    public void openLobbyCompassOnPlayer(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, (int)(this.pl.getConfig().getInt("inventory-lines-amount") * 9), (String)ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-inventory-name")));
        List<String> options = this.pl.getConfig().getStringList("options");
        for (String option : options) {
        	ItemStack istack = new ItemStack(Material.getMaterial(this.pl.getConfig().getString("data." + option + ".item")));
            ItemMeta imeta = istack.getItemMeta();
            imeta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("data." + option + ".name")));
            List<String> lores = this.pl.getConfig().getStringList("data." + option + ".lore");
            ArrayList<String> newLores = new ArrayList<String>();
            for (String lore : lores) {
                newLores.add(ChatColor.translateAlternateColorCodes((char)'&', (String)lore));
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
                        List<String> options = this.pl.getConfig().getStringList("options");
                        for (String option : options) {
                        	if (invstack.getType() != Material.getMaterial(this.pl.getConfig().getString("data." + option + ".item")) || !invstack.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("data." + option + ".name")))) continue;
                        	String cmd = this.pl.getConfig().getString("data." + option + ".cmd");
                            if (this.pl.getConfig().getBoolean("sound-effect")) {
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 7.0f, 1.0f);
                            }
                            if (this.pl.getConfig().getBoolean("data." + option + ".executedByPlayer")) {
                                Bukkit.getServer().dispatchCommand((CommandSender)p, cmd);
                                break;
                            }
                            System.out.println(LobbyCompass.prefix + "Next command executed by LobbyCompassPlugin of Player " + p.getName());
                            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)cmd.replace("%n%", p.getName()));
                            break;
                        }
                        p.closeInventory();
                    }
                }
                catch (Exception invstack) {
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
            if (!p.getInventory().contains(compass)) {
                if (this.pl.getConfig().getInt("get-compass-on-join-slot") >= 0) {
                    p.getInventory().setItem(this.pl.getConfig().getInt("get-compass-on-join-slot"), compass);
                }
            }
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        ItemStack compass = this.configuratedCompass();
        if (this.pl.getConfig().getStringList("get-compass-worlds").contains(p.getWorld().getName())) {
            if (!p.getInventory().contains(this.configuratedCompass())) {
                p.getInventory().addItem(new ItemStack[]{this.configuratedCompass()});
            }
        }
    }

    public ItemStack configuratedCompass() {
        ItemStack compass = new ItemStack(Material.getMaterial(this.pl.getConfig().getString("compass-item")));
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)this.pl.getConfig().getString("compass-name")));
        compassMeta.addEnchant(Enchantment.DURABILITY,0, false);
        compassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        compass.setItemMeta(compassMeta);
        return compass;
    }
}
