/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.database;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import de.finn.friendgui.FriendGUI;
import java.util.UUID;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author Finn
 */
public class MongoPlayer {
    
    private final FriendGUI plugin;

    public MongoPlayer(FriendGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * @param username is the name of the person from who the document is searched
     * @return the document of the searched player
     */
    //<editor-fold defaultstate="collapsed" desc="searchDocument">
    public Document searchDocument(String username) {
        Document document = (Document)this.plugin.getMongoManager().getPlayers().find(Filters.eq("NAME", username)).first();
        return document;
    }
    //</editor-fold>
    
    /**
     * @param key the key to update the document
     * @param object the value to update
     * @param document the document to update
     * @return the update result
     */
    //<editor-fold defaultstate="collapsed" desc="setUpdate">
    public UpdateResult setUpdate(String key, Object object, Document document) {
        Bson updatedvalue = new Document(key, object);
        Bson updateoperation = new Document("$set", updatedvalue);
        return this.plugin.getMongoManager().getPlayers().updateOne(Filters.eq("UUID", (UUID)document.get("UUID", (Class)UUID.class)), (Bson)updateoperation);
    }
    //</editor-fold>
    
}
