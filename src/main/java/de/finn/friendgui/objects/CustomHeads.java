/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.objects;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author Finn
 */
public class CustomHeads {
    
    private final String url;
    private final String name;

    public CustomHeads(String url, String name) {
        this.url = url;
        this.name = name;
    }
    
    /**
     * 
     * @param lore is the lore of the item
     * @return the skull itemstack
     */
    //<editor-fold defaultstate="collapsed" desc="createCustomSkull">
    public ItemStack createCustomSkull(String... lore) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        if (this.url.isEmpty())
            return head;
        
        SkullMeta headMeta = (SkullMeta)head.getItemMeta();
        headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
        headMeta.setLore(Arrays.asList(lore));
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", this.url));
        
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException|NoSuchFieldException|SecurityException|IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta((ItemMeta)headMeta);
        return head;
    }
    //</editor-fold>
    
    /**
     * 
     * @param player is the player you want the texture from
     * @return texture value
     */
    //<editor-fold defaultstate="collapsed" desc="getSkinValue">
    public static String getSkinValue(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer)player;
        GameProfile gameProfile = craftPlayer.getProfile();
        Iterator<Property> iterator = gameProfile.getProperties().get("textures").iterator();
        Property prop = iterator.next();
        return prop.getValue();
    }
    //</editor-fold>
}
