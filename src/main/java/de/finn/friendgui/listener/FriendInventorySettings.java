/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.listener;

import de.finn.friendgui.FriendGUI;
import de.finn.friendgui.enums.FriendEnum;
import de.finn.friendgui.objects.CustomHeads;
import de.finn.friendgui.objects.ItemBuilder;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;

/**
 *
 * @author Finn
 */
public class FriendInventorySettings implements Listener {
    
    private final FriendGUI plugin;

    public FriendInventorySettings(FriendGUI plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) return;
        Player player = (Player)event.getWhoClicked();

        if (event.getClickedInventory().getName().equals(FriendEnum.FRIEND_GUI.getInventoryName())) {
            if (event.getCurrentItem().getType() == Material.COMMAND_MINECART) {
                event.getWhoClicked().closeInventory();
                Document document = (Document)this.plugin.getMetadata((Player) event.getWhoClicked(), "friendgui");

                Inventory inventory = this.plugin.getServer().createInventory(event.getWhoClicked(), FriendEnum.SETTING_GUI.getInventorySize(), FriendEnum.SETTING_GUI.getInventoryName());
                
                inventory.setItem(10, document.getBoolean("ALLOW_FRIEND") ? new CustomHeads(CustomHeads.getSkinValue(player), "§bFreundesanfragen Erhalten").createCustomSkull("§7✦ §aAktiviert") : 
                        new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§bFreundesanfragen Erhalten").setLore("§7✦ §cDeaktiviert").getItemStack());
                
                inventory.setItem(13, (new ItemBuilder(Material.PAPER)).setDisplayName("§bFreundesnachrichten Erhalten").setLore(document.getBoolean("ALLOW_MESSAGE") ? "§7✦ §aAktiviert" : "§7✦ §cDeaktiviert").getItemStack());
                
                inventory.setItem(16, (new ItemBuilder(Material.ENDER_PEARL)).setDisplayName("§bNachspringen").setLore(document.getBoolean("ALLOW_JUMP") ? "§7✦ §aAktiviert" : "§7✦ §cDeaktiviert").getItemStack());
                
                event.getWhoClicked().openInventory(inventory);
            }
            return;
        }

        if (event.getClickedInventory().getName().equals(FriendEnum.SETTING_GUI.getInventoryName())) {
            switch(event.getCurrentItem().getType()) {
                case SKULL_ITEM:
                    if (event.getCurrentItem().getDurability() == 0) {
                        event.getClickedInventory().setItem(10, (new CustomHeads(CustomHeads.getSkinValue(player), "§bFreundesanfragen Erhalten")).createCustomSkull("§7✦ §aAktiviert"));
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du kannst nun wieder §bAnfragen §7erhalten.");
                        this.plugin.getMongoPlayer().setUpdate("ALLOW_FRIEND", true, (Document)((MetadataValue)player.getMetadata("friendgui").get(0)).value());
                    } else {
                        event.getClickedInventory().setItem(10, (new ItemBuilder(Material.SKULL_ITEM)).setDisplayName("§bFreundesanfragen Erhalten").setLore("§7✦ §cDeaktiviert").getItemStack());
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du kannst nun keine §bAnfragen §7mehr erhalten");
                        this.plugin.getMongoPlayer().setUpdate("ALLOW_FRIEND", false, (Document)((MetadataValue)player.getMetadata("friendgui").get(0)).value());
                    }
                    break;
                case PAPER:
                    if (event.getCurrentItem().getItemMeta().getLore().contains("§7✦ §aAktiviert")) {
                        event.getClickedInventory().setItem(13, (new ItemBuilder(Material.PAPER)).setDisplayName("§bFreundesnachrichten Erhalten").setLore("§7✦ §cDeaktiviert").getItemStack());
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du kannst nun keine §bNachrichten §7mehr erhalten.");
                        this.plugin.getMongoPlayer().setUpdate("ALLOW_MESSAGE", false, (Document)((MetadataValue)player.getMetadata("friendgui").get(0)).value());
                    } else {
                        event.getClickedInventory().setItem(13, (new ItemBuilder(Material.PAPER)).setDisplayName("§bFreundesnachrichten Erhalten").setLore("§7✦ §aAktiviert").getItemStack());
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du kannst nun wieder §bNachrichten §7erhalten.");
                        this.plugin.getMongoPlayer().setUpdate("ALLOW_MESSAGE", true, (Document)((MetadataValue)player.getMetadata("friendgui").get(0)).value());
                    }
                    break;
                case ENDER_PEARL:
                    if (event.getCurrentItem().getItemMeta().getLore().contains("§7✦ §aAktiviert")) {
                        event.getClickedInventory().setItem(16, (new ItemBuilder(Material.ENDER_PEARL)).setDisplayName("§bNach springen").setLore("§7✦ §cDeaktiviert").getItemStack());
                        player.sendMessage(this.plugin.getPREFIX() + "§7Dir kann nun keiner mehr §bnachspringen§7.");
                        this.plugin.getMongoPlayer().setUpdate("ALLOW_JUMP", false, (Document)((MetadataValue)player.getMetadata("friendgui").get(0)).value());
                    } else {
                        event.getClickedInventory().setItem(16, (new ItemBuilder(Material.ENDER_PEARL)).setDisplayName("§bNach springen").setLore("§7✦ §aAktiviert").getItemStack());
                        player.sendMessage(this.plugin.getPREFIX() + "§7Deine §3Freunde §7können nun wieder §bnachspringen§7.");
                        this.plugin.getMongoPlayer().setUpdate("ALLOW_JUMP", true, (Document)((MetadataValue)player.getMetadata("friendgui").get(0)).value());
                    }
               }
        }
    }
}
