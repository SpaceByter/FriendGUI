/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.finn.friendgui.FriendGUI;
import de.finn.friendgui.enums.FriendEnum;
import de.finn.friendgui.objects.ItemBuilder;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/**
 *
 * @author Finn
 */
public class FriendInventoryListener implements Listener {
    
    private final FriendGUI plugin;

    public FriendInventoryListener(FriendGUI plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handleInteractEvent(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getPlayer().getItemInHand().getType() == Material.SKULL_ITEM && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§bFreunde")) {
            Document document = this.plugin.getMongoPlayer().searchDocument(event.getPlayer().getName());
            
            if (document != null) {
                Inventory inventory = this.plugin.getServer().createInventory(event.getPlayer(), FriendEnum.FRIEND_GUI.getInventorySize(), FriendEnum.FRIEND_GUI.getInventoryName());
                
                ArrayList<UUID> friends = new ArrayList();
                ArrayList<UUID> friendList = (ArrayList)document.get("FRIEND_LIST", (Class)ArrayList.class);
                this.plugin.setMetadata(event.getPlayer(), "friendgui", document);
                
                friendList.forEach((uuids) -> {
                    CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uuids);
                    if (cloudPlayer != null) {
                        inventory.addItem(new ItemStack[]{(new ItemBuilder(Material.SKULL_ITEM)).setDisplayName("§a" + cloudPlayer.getName()).setShort(Short.valueOf((short)3)).setSkullOwner(cloudPlayer.getName()).setLore("§7Server: §3" + cloudPlayer.getServer()).getItemStack()});
                        friends.remove(cloudPlayer.getUniqueId());
                    } else {
                        friends.add(uuids);
                    }

                });
                
                if (!friends.isEmpty()) {
                    friends.forEach((uuids) -> {
                        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuids);
                        if (offlinePlayer != null) {
                            inventory.addItem(new ItemStack[]{(new ItemBuilder(Material.SKULL_ITEM)).setDisplayName("§7" + offlinePlayer.getName()).setLore("§cOffline").getItemStack()});
                        }
                    });
                }
                
                inventory.setItem(45, (new ItemBuilder(Material.EMERALD)).setDisplayName("§aFreundesanfragen").getItemStack());
                inventory.setItem(46, (new ItemBuilder(Material.COMMAND_MINECART)).setDisplayName("§3Einstellungen").getItemStack());
                inventory.setItem(45, (new ItemBuilder(Material.EMERALD)).setDisplayName("§aFreundesanfragen").getItemStack());
                inventory.setItem(46, (new ItemBuilder(Material.COMMAND_MINECART)).setDisplayName("§3Einstellungen").getItemStack());
                
                event.getPlayer().openInventory(inventory);
            }
      }
   }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
        
        if (event.getClickedInventory().getName().equals(FriendEnum.FRIEND_GUI.getInventoryName())) {
            if (event.getCurrentItem().getType() != Material.COMMAND_MINECART && event.getCurrentItem().getType() != Material.EMERALD) {
                Inventory inventory = this.plugin.getServer().createInventory(event.getWhoClicked(), FriendEnum.FRIEND_GUI_PLAYER.getInventorySize(), FriendEnum.FRIEND_GUI_PLAYER.getInventoryName());
                
                inventory.setItem(10, event.getCurrentItem());
                if (event.getCurrentItem().getDurability() != 0) {
                    inventory.setItem(15, (new ItemBuilder(Material.ENDER_PEARL)).setDisplayName("§7Freund nachspringen").getItemStack());
                }

                inventory.setItem(16, (new ItemBuilder(Material.BARRIER)).setShort(Short.valueOf((short)14)).setDisplayName("§cFreund entfernen").getItemStack());
                event.getWhoClicked().openInventory(inventory);
            }
            return;
        }
        
        if (event.getClickedInventory().getName().equals(FriendEnum.FRIEND_GUI_PLAYER.getInventoryName())) {
            String name = ChatColor.stripColor(event.getClickedInventory().getItem(10).getItemMeta().getDisplayName());
            Player player = (Player)event.getWhoClicked();
            byte clickedItem = -1;
            switch(event.getCurrentItem().getItemMeta().getDisplayName()) {
                case "§7Freund nachspringen":
                    clickedItem = 0;
                    break;
                case "§cFreund entfernen":
                    clickedItem = 1;
                    break;
            }
            
            switch(clickedItem) {
                case 0:
                    Document found = this.plugin.getMongoPlayer().searchDocument(name);
                    if (found != null) {
                        if (!found.getBoolean("ALLOW_JUMP")) {
                            player.sendMessage(this.plugin.getPREFIX() + "§b" + name + " §7kannst du nicht nachspringen.");
                            return;
                        }
                        
                        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
                        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer((UUID)found.get("UUID", (Class)UUID.class));
                        if (cloudPlayer != null) {
                            if (CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId()).getServer().equals(cloudPlayer.getServer())) {
                                player.sendMessage(this.plugin.getPREFIX() + "§7Du bist breits auf den Server von §b" + name + ".");
                                player.closeInventory();
                                return;
                            } 
                            
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            DataOutputStream outputStream = new DataOutputStream(stream);
                            try {
                                outputStream.writeUTF("Connect");
                                outputStream.writeUTF(cloudPlayer.getServer());
                            } catch (IOException exception) {
                                this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, "BungeeCord");
                                exception.printStackTrace();
                            } finally {
                                player.sendPluginMessage(this.plugin, "BungeeCord", stream.toByteArray());
                                this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, "BungeeCord");
                            }
                        }
                    }
                    break;
                case 1:
                    Document document = this.plugin.getMongoPlayer().searchDocument(name);
                    if (document != null) {
                        ((ArrayList)document.get("FRIEND_LIST", (Class)ArrayList.class)).remove(player.getUniqueId());
                        this.plugin.getMongoPlayer().setUpdate("FRIEND_LIST", document.get("FRIEND_LIST", (Class)ArrayList.class), document);
                        
                        Document playerDocument = (Document)this.plugin.getMetadata(player, "friendgui");
                        ((ArrayList)playerDocument.get("FRIEND_LIST", (Class)ArrayList.class)).remove(document.get("UUID", (Class)UUID.class));
                        this.plugin.getMongoPlayer().setUpdate("FRIEND_LIST", playerDocument.get("FRIEND_LIST", (Class)ArrayList.class), playerDocument);
                        
                        player.closeInventory();
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du bist nicht mehr mit §b" + name + " §7befreundet.");
                    }
            }
        }
        
    }
}
