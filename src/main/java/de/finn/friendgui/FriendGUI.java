/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui;

import de.finn.friendgui.database.MongoManager;
import de.finn.friendgui.database.MongoPlayer;
import de.finn.friendgui.listener.FriendInventoryListener;
import de.finn.friendgui.listener.FriendInventorySettings;
import de.finn.friendgui.listener.FriendRequestListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Finn
 */
public class FriendGUI extends JavaPlugin {
    
    private final String PREFIX = "§7✦ §3Freunde §8» ";
    
    private MongoManager mongoManager;
    private MongoPlayer mongoPlayer;
    
    @Override
    public void onEnable() {
        this.init();
        
        new FriendInventoryListener(this);
        new FriendInventorySettings(this);
        new FriendRequestListener(this);
    }
    
    @Override
    public void onDisable() {
        this.mongoManager.disconnectDatabase();
    }
    
    //<editor-fold defaultstate="collapsed" desc="init">
    private void init() {
        this.mongoManager = new MongoManager("localhost", 27017);
        this.mongoManager.connect();
        this.mongoPlayer = new MongoPlayer(this);
    }
    //</editor-fold>
    
    /**
     * @param player the player from which the metadata is
     * @param key the key to get the metadata
     * @return the object in the metadata
     */
    //<editor-fold defaultstate="collapsed" desc="getMetadata">
    public Object getMetadata(Player player, String key) {
        return ((MetadataValue)player.getMetadata(key).get(0)).value();
    }
    //</editor-fold>
    
    /**
     * 
     * @param player the player from which the metadata is
     * @param key the key to remove the metadata
     */
    //<editor-fold defaultstate="collapsed" desc="removeMetedata">
    public void removeMetedata(Player player, String key) {
        if (player.hasMetadata(key)) {
            player.removeMetadata(key, this);
        }
    }
    //</editor-fold>
    
    /**
     * 
     * @param player the player from which the metadata is
     * @param key the key to set the metadata
     * @param value the value for the metadata
     */
    //<editor-fold defaultstate="collapsed" desc="setMetadata">
    public void setMetadata(Player player, String key, Object value) {
        if (player.hasMetadata(key)) {
            player.removeMetadata(key, this);
        }
        player.setMetadata(key, new FixedMetadataValue(this, value));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPREFIX">
    public String getPREFIX() {
        return PREFIX;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getMongoManager">
    public MongoManager getMongoManager() {
        return mongoManager;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getMongoPlayer">
    public MongoPlayer getMongoPlayer() {
        return mongoPlayer;
    }
    //</editor-fold>
    
}
