/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.objects;

import java.util.Arrays;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author Finn
 */
public class ItemBuilder {
    
    private final ItemStack itemStack;
    private ItemMeta itemMeta;
    private SkullMeta skullMeta;
    private LeatherArmorMeta leatherArmorMeta;

    public ItemBuilder( Material material ) {
        this.itemStack = new ItemStack( material );
    }
    
    public ItemBuilder setAmount( int amount ) {
        this.itemStack.setAmount( amount );
        return this;
    }
    
    public ItemBuilder setDisplayName( String name ) {
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName( name );
        this.itemStack.setItemMeta( this.itemMeta );
        return this;
    }
    public ItemBuilder setLore(String ... lore) {
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setLore( Arrays.asList(lore) );
        this.itemStack.setItemMeta( this.itemMeta );
        return this;
    }
    public ItemBuilder setSkullOwner( String owner ) {
        this.skullMeta = ( SkullMeta ) this.itemStack.getItemMeta();
        this.skullMeta.setOwner( owner );
        this.itemStack.setItemMeta( this.skullMeta );
        return this;
    }
    public ItemBuilder setShort( Short s ) {
        this.itemStack.setDurability( s );
        return this;
    }
    public ItemBuilder addEnchantment(Enchantment enchantment, int value ){
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.addEnchant( enchantment, value, true );
        this.itemStack.setItemMeta( this.itemMeta );
        return this;
    }
    public ItemBuilder setLeatherColor( Color color ) {
        this.leatherArmorMeta = ( LeatherArmorMeta ) this.itemStack.getItemMeta();
        this.leatherArmorMeta.setColor( color );
        this.itemStack.setItemMeta( this.leatherArmorMeta );
        return this;
    }
    public ItemStack getItemStack () {
        return itemStack;
    }
}
