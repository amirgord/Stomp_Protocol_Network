package bgu.spl.net.impl.stomp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;

public class User {
    
    private String userName;
    private String password;
    private ConnectionHandler<String> connectionHandler;
    private int connectionId;
    private HashMap<String,Integer> games;
    private boolean isConnected;

    
    //cont for Reactor:
    public User(int connectionId, ConnectionHandler<String> connectionHandler) {

        this("NONAME", "", connectionHandler, connectionId);
    }

    public User(String userName, String password, ConnectionHandler<String> connectionHandler, int connectionId) {
        this.userName = userName;
        this.password = password;
        this.connectionId = connectionId;
        games = new HashMap<String,Integer>();
        isConnected = true;
        if(connectionHandler instanceof StompConnectionHandler)
            this.connectionHandler = (StompConnectionHandler<String>)connectionHandler;
        else
            this.connectionHandler = (BlockingConnectionHandler<String>)connectionHandler;

    }

    // //cont for TPC:
    // public User(int connectionId, BlockingConnectionHandler<String> connectionHandler) {

    //     this("NONAME", "", connectionHandler, connectionId);
    // }

    // public User(String userName, String password, BlockingConnectionHandler<String> connectionHandler, int connectionId) {
    //     this.userName = userName;
    //     this.password = password;
    //     this.connectionHandler = (BlockingConnectionHandler<String>)connectionHandler;
    //     this.connectionId = connectionId;
    //     games = new HashMap<String,Integer>();
    //     isConnected = true;
    // }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionHandler<String> getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(StompConnectionHandler<String> connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public void addGame(String game, int subId) {
        games.put(game, subId);
    }

    public Map<String, Integer> getGames() {
        return games;
    }

    public void removeGame(String game) {
        games.remove(game);
    }

    public void removeGame(int subId) {
        for(String game : games.keySet()) {
            if(games.get(game) == subId) {
                games.remove(game);
                break;
            }
        }
    }

    public void removeAllGames() {
        games = new HashMap<String,Integer>();
    }

    public boolean isSubscribed(String game) {
        return games.containsKey(game);
    }

    public void send(String msg) {
        // System.out.println("Send msg to User: " + connectionId);
        connectionHandler.send(msg);
    }

    public void disconnect() {
        try {
            this.connectionHandler.close();
        } 
        catch (IOException exception) {
            exception.printStackTrace();
        }
        this.connectionHandler = null;
        this.connectionId = -1;
        this.isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public String getGame(int subId) {
        for (String game : games.keySet()) {
            if (games.get(game) == subId) {
                return game;
            }
        }
            
        return null;
    }

	public int getSubId(String game) {
        if(games.containsKey(game)) {
            return games.get(game);
        }
		return -1;
	}

}
