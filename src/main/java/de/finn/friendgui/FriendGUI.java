/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui;

import de.finn.friendgui.database.MongoManager;
import de.finn.friendgui.database.MongoPlayer;
import de.finn.friendgui.listener.FriendInvListener;
import de.finn.friendgui.listener.FriendInvSettings;
import de.finn.friendgui.listener.FriendRequestListener;
import org.bukkit.entity.Entity;
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
        this.mongoPlayer = new MongoPlayer(this);
        this.mongoPlayer = new MongoPlayer(this);
        new FriendInvListener(this);
        new FriendInvSettings(this);
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
    }
    //</editor-fold>
    
    public Object getMetadata(Entity entity, String key) {
        return ((MetadataValue)entity.getMetadata(key).get(0)).value();
    }

    public void removeMetedata(Entity entity, String key) {
        if (entity.hasMetadata(key)) {
            entity.removeMetadata(key, this);
        }
    }

    public void setMetadata(Entity entity, String key, Object value) {
        if (entity.hasMetadata(key)) {
            entity.removeMetadata(key, this);
        }
        entity.setMetadata(key, new FixedMetadataValue(this, value));
    }

    public String getPREFIX() {
        return PREFIX;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public MongoPlayer getMongoPlayer() {
        return mongoPlayer;
    }
    
}
