/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendgui.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 *
 * @author Finn
 */
public class MongoManager {
    
    private final String hostname;
    private final int port;

    private MongoClient client;
    private MongoDatabase database;
    
    private MongoCollection<Document> players;
    
    
    public MongoManager(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    public void connect(){
        this.client = new MongoClient(this.hostname, this.port);
        
        this.database = this.client.getDatabase("FriendSystem");
        this.players = this.database.getCollection("players");
    }
    
    public void disconnectDatabase(){
        if(this.client != null) this.client.close();
    }

    public MongoCollection<Document> getPlayers() {
        return players;
    }
    
}
