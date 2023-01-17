package bgu.spl.net.impl.stomp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

/*
 * This class should map a unique ID for each active client connected
 * to the server. The implementation of Connections is part of the server
 * pattern and not part of the protocol. It has 3 functions that you must
 * implement (You may add more if needed)
 */
public class ConnectionsImpl<T> implements Connections<T> {

    protected ConcurrentHashMap<String,User> nameToUser;
    protected ConcurrentHashMap<Integer,User> connectionIdToUser;
    protected ConcurrentHashMap<String, LinkedList<User>> gameToUsers;
    private int messageIdMaker = 0;

    public ConnectionsImpl() {

        nameToUser = new ConcurrentHashMap<String,User>();
        connectionIdToUser = new ConcurrentHashMap<Integer,User>();
        gameToUsers = new ConcurrentHashMap<String, LinkedList<User>>();
        
    }

    @Override
    public void send(int connectionId, T msg) {
        connectionIdToUser.get(connectionId).send((String)msg);
        System.out.println(">>> Server response:");
        System.out.println((String)msg);
    }

    @Override
    public void send(String game, T msg, int connectionId) {

        boolean joinedToGame = connectionIdToUser.get(connectionId).isSubscribed(game);

        // if subscription is not subscribed to the channel send Error message
        if (!joinedToGame) {
            String error = "ERROR\nmessage:You are not subscribed to this channel\n\n" + '\u0000';
            send(connectionId, (T)error);
            return;
        }

        int fromSubId = connectionIdToUser.get(connectionId).getSubId(game);

        for (User user : gameToUsers.get(game)) {
            
            // make sure not send a message to yourself??
            // if (user.getConnectionId() == connectionId) {
            //     continue;
            // }

            String response = "MESSAGE\nsubscription:" + fromSubId + "\nmessage-id:" + messageIdMaker++ + "\ndestination:" + game + "\n\n" + (String)msg + "\n" + '\u0000';
            // user.send("MESSAGE\nsubscription:" + fromSubId + "\nmessage-id:" + messageIdMaker++ + "\ndestination:" + game + "\n\n" + "dummy" + '\u0000');
            user.send(response);
            
        }
    }

    @Override
    public void disconnect(int connectionId, int receiptId) {

        User userToDisconnect = connectionIdToUser.get(connectionId);

        // send receipt to user:
        String receipt = "RECEIPT\nreceipt-id:" + receiptId + "\n\n" + '\u0000';
        send(connectionId, (T)receipt);

        //unsubscribe from all games:
        unsubscribeAllGames(connectionId, -1);

        //remove from connection id to User:
        connectionIdToUser.remove(connectionId);

        //change user's fields:
        userToDisconnect.disconnect();
        
    }

    public void unsubscribeAllGames(int connectionId, int receiptId) {
        User user = connectionIdToUser.get(connectionId);

        for(String game : user.getGames().keySet()){
            gameToUsers.get(game).remove(connectionIdToUser.get(connectionId));
        }
        user.removeAllGames();
    }

    public String unsubscribe(int connectionId, int receiptId, int subId) {

        User user = connectionIdToUser.get(connectionId);
        
        // remove user from game's users list
        String game = user.getGame(subId);
        if(game == null) {
            return "ERROR\nmessage:You are not subscribed to this channel\n\n" + '\u0000';
        }
        gameToUsers.get(game).remove(user);

        // remove game from user's games list
        user.removeGame(subId);


        return "RECEIPT\nreceipt-id:" + receiptId + "\n\n" + '\u0000';
    }

    public void addGame(String game) {
        gameToUsers.put(game, new LinkedList<User>());
    }


    public String addSubscription(String game, Integer connectionId, int subId, int receiptId) {
        
        // if game doesn't exist create it
        if (!gameToUsers.containsKey(game)) {
            addGame(game);
        }



        // if user is'nt subscribed to game add him
        if (!connectionIdToUser.get(connectionId).isSubscribed(game)) {
            gameToUsers.get(game).add(connectionIdToUser.get(connectionId));
            connectionIdToUser.get(connectionId).addGame(game, subId);
        
            // send SUBSCRIBED message to the client
            String subscribed = "RECEIPT\nreceipt-id:" + receiptId + "\n\n" + '\u0000';
            return subscribed;
        }
        else { // user is already subscribed to game
            return "ERROR\nmessage:You are already subscribed to this channel\n\n" + '\u0000';
        }

    }

    private boolean isConnected(int connectionId) { // throws Exception?
        
        return connectionIdToUser.contains(connectionId);
    }

    public String verifyConnection(String userName, String password, int id) {
        // if user is new
        if(!nameToUser.containsKey(userName)) {
            User newUser = connectionIdToUser.get(id);
            newUser.setUserName(userName);
            newUser.setPassword(password);
            newUser.setConnected(true);
            nameToUser.put(userName, newUser);
        }
        else{ // user exists

                // password is wrong
                if(!nameToUser.get(userName).getPassword().equals(password)) {
                    return "ERROR\nmessage:Wrong password\n\n" + '\u0000';
                }
                else if (nameToUser.get(userName).isConnected()) { // user is already connected
                    return "ERROR\nmessage:User is already connected\n\n" + '\u0000';
                }
                else { // user is not connected
                    User user = new User(userName, password, connectionIdToUser.get(id).getConnectionHandler(),id);
                    // override dummy user with real user
                    connectionIdToUser.put(id, user);
                    nameToUser.put(userName, user);
                }

            }
        return "CONNECTED\nversion:1.2\n\n\u0000";

        
    }

    @Override
    public void addConnection(int connectionId, StompConnectionHandler<String> connectionHandler) {
        connectionIdToUser.put(connectionId, new User(connectionId ,connectionHandler));
    }

    @Override
    public void addConnection(int connectionId, BlockingConnectionHandler<String> connectionHandler) {
        connectionIdToUser.put(connectionId, new User(connectionId ,connectionHandler));
    }





    
}
