/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.listener;

import com.mongodb.client.result.UpdateResult;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.finn.friendgui.FriendGUI;
import de.finn.friendgui.enums.FriendEnum;
import de.finn.friendgui.objects.ItemBuilder;
import java.util.ArrayList;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Finn
 */
public class FriendRequestListener implements Listener {
    
    private final FriendGUI plugin;

    public FriendRequestListener(FriendGUI plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
        
        Inventory inventory;
        Document document;
        ArrayList<UUID> request;

        if (event.getClickedInventory().getName().equals(FriendEnum.FRIEND_GUI.getInventoryName()) && event.getCurrentItem().getType() == Material.EMERALD) {
            event.getWhoClicked().closeInventory();
            
            inventory = this.plugin.getServer().createInventory(event.getWhoClicked(), FriendEnum.REQUEST_GUI.getInventorySize(), FriendEnum.REQUEST_GUI.getInventoryName());
            document = (Document)this.plugin.getMetadata((Player) event.getWhoClicked(), "friendgui");
            request = (ArrayList)document.get("REQUEST_LIST", (Class)ArrayList.class);
            
            if (!request.isEmpty()) {
                request.forEach((uuids) -> {
                    OfflinePlayer player = CloudAPI.getInstance().getOfflinePlayer((UUID)uuids);
                    if (player != null) {
                        inventory.addItem(new ItemStack[]{(new ItemBuilder(Material.SKULL_ITEM)).setDisplayName("§r" + player.getName()).getItemStack()});
                    }
                });
            }

            for(int i = 45; i <= 53; i++) {
                inventory.setItem(i, (new ItemBuilder(Material.STAINED_GLASS_PANE)).setShort((short) 7).setDisplayName(" ").getItemStack());
            }
            
            inventory.setItem(53, (new ItemBuilder(Material.REDSTONE_BLOCK)).setDisplayName("§cAlle Anfragen löschen").getItemStack());
            event.getWhoClicked().openInventory(inventory);
            return;
        }
        
        if (event.getClickedInventory().getName().equals(FriendEnum.REQUEST_GUI.getInventoryName())) {
            switch(event.getCurrentItem().getType()) {
                case SKULL_ITEM:
                    event.getWhoClicked().closeInventory();
                    inventory = this.plugin.getServer().createInventory((InventoryHolder)null, FriendEnum.PLAYER_GUI.getInventorySize(), FriendEnum.PLAYER_GUI.getInventoryName());
                    inventory.setItem(10, event.getCurrentItem());
                    inventory.setItem(15, (new ItemBuilder(Material.getMaterial(159))).setShort(Short.valueOf((short)5)).setDisplayName("§aAnfrage Annehmen").getItemStack());
                    inventory.setItem(16, (new ItemBuilder(Material.getMaterial(159))).setShort(Short.valueOf((short)14)).setDisplayName("§cAnfrage Ablehnen").getItemStack());
                    event.getWhoClicked().openInventory(inventory);
                    break;
                case REDSTONE_BLOCK:
                    document = (Document)this.plugin.getMetadata((Player) event.getWhoClicked(), "friendgui");
                    if (((ArrayList)document.get("REQUEST_LIST", (Class)ArrayList.class)).isEmpty()) {
                        event.getWhoClicked().sendMessage(this.plugin.getPREFIX() + "§7Deine Anfragenliste ist leer.");
                        return;
                    }
                    ((ArrayList)document.get("REQUEST_LIST", (Class)ArrayList.class)).clear();
                    request = (ArrayList)document.get("REQUEST_LIST", (Class)ArrayList.class);
                    UpdateResult result = this.plugin.getMongoPlayer().setUpdate("REQUEST_LIST", request, document);
                    if (result != null) {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().sendMessage(this.plugin.getPREFIX() + "§7Deine Anfragenliste wurde geleert.");
                    }
                    break;
            }
        }

        if (event.getClickedInventory().getName().equals(FriendEnum.PLAYER_GUI.getInventoryName())) {
            if (event.getCurrentItem().getType() == Material.DIAMOND) return;
            
            String name = ChatColor.stripColor(event.getInventory().getItem(10).getItemMeta().getDisplayName());
            Document playerDocument = (Document)this.plugin.getMetadata((Player) event.getWhoClicked(), "friendgui");
            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
            
            byte clickedItem = -1;
            switch(itemName) {
            case "§cAnfrage Ablehnen":
                clickedItem = 1;
                break;
            case "§aAnfrage Annehmen":
                clickedItem = 0;
            }

            switch(clickedItem) {
                case 0:
                    document = this.plugin.getMongoPlayer().searchDocument(name);
                    if (document != null) {
                        if (((ArrayList)document.get("FRIEND_LIST", (Class)ArrayList.class)).size() >= 35) {
                            event.getWhoClicked().sendMessage(this.plugin.getPREFIX() + "§7Die Freundesliste von §b" + name + " §7ist voll.");
                            return;
                        }

                        ((ArrayList)document.get("FRIEND_LIST", (Class)ArrayList.class)).add(event.getWhoClicked().getUniqueId());
                        if (playerDocument != null) {
                            if (((ArrayList)playerDocument.get("FRIEND_LIST", (Class)ArrayList.class)).size() >= 35) {
                                event.getWhoClicked().sendMessage(this.plugin.getPREFIX() + "§7Deine Freundeslist ist voll.");
                                return;
                            }

                            ((ArrayList)playerDocument.get("REQUEST_LIST", (Class)ArrayList.class)).remove(document.get("UUID", (Class)UUID.class));
                            ((ArrayList)playerDocument.get("FRIEND_LIST", (Class)ArrayList.class)).add(document.get("UUID", (Class)UUID.class));
                            this.plugin.getMongoPlayer().setUpdate("FRIEND_LIST", playerDocument.get("FRIEND_LIST", (Class)ArrayList.class), playerDocument);
                            this.plugin.getMongoPlayer().setUpdate("REQUEST_LIST", playerDocument.get("REQUEST_LIST", (Class)ArrayList.class), playerDocument);
                            this.plugin.getMongoPlayer().setUpdate("FRIEND_LIST", document.get("FRIEND_LIST", (Class)ArrayList.class), document);
                            event.getWhoClicked().sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + name + " §7befreundet.");
                            event.getWhoClicked().closeInventory();
                        }
                    }
                    break;
                case 1:
                    OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);
                    if (offlinePlayer != null) {
                        ((ArrayList)playerDocument.get("REQUEST_LIST", (Class)ArrayList.class)).remove(offlinePlayer.getUniqueId());
                        this.plugin.getMongoPlayer().setUpdate("REQUEST_LIST", playerDocument.get("REQUEST_LIST", (Class)ArrayList.class), playerDocument);
                        event.getWhoClicked().sendMessage(this.plugin.getPREFIX() + "§7Du hast die Anfrage von §b" + name + " §7abgelehnt.");
                        event.getWhoClicked().closeInventory();
                    }
           }
        }
        
    }
}
