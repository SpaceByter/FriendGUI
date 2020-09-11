/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.enums;

/**
 *
 * @author Finn
 */
public enum FriendEnum {
    
    FRIEND_GUI("§7✦ §3Deine Freunde §7✦", 54),
    REQUEST_GUI("§7✦ §3Deine Anfragen §7✦", 54),
    PLAYER_GUI("§7✦ §3Anfrage Bearbeiten §7✦", 27),
    SETTING_GUI("§7✦ §3Deine Einstellungen §7✦", 27),
    FRIEND_GUI_PLAYER("§7§l✦ §3Freund Übersicht", 27);

    private final String inventoryName;
    private final Integer inventorySize;

    private FriendEnum(String inventoryName, Integer inventorySize) {
        this.inventoryName = inventoryName;
        this.inventorySize = inventorySize;
    }

    public String getInventoryName() {
        return this.inventoryName;
    }

    public Integer getInventorySize() {
        return this.inventorySize;
    }
    
}
