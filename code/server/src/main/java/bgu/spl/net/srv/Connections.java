package bgu.spl.net.srv;

import java.io.IOException;

import bgu.spl.net.impl.stomp.StompConnectionHandler;

public interface Connections<T> {

    /*
     * sends a message T to client represented by the given connectionId.
     */
    void send(int connectionId, T msg);

    /*
     * sends a message T to all the clients subscribed to the given channel.
     */
    void send(String channel, T msg, int connectionId);

    /*
     * Removes an active client connectionId from the map
     */
    void disconnect(int connectionId, int receiptId);


    /**
     * 
     * @param channel
     * @param connectionId
     * 
     * create a new channel if the channel does not exist
     * 
     * add a subscription to a channel 
     * do nothing if the subscription is already subscribed to the channel
     * 
     */
    String addSubscription(String channel, Integer connectionId, int subId, int receiptId);

    /**
     * 
     * @param channel
     * @param connectionId
     * 
     * remove a subscription from all channels
     * 
     */
    void unsubscribeAllGames(int connectionId, int receiptId);

    String unsubscribe(int connectionId, int receiptId, int subId);




    String verifyConnection(String string, String string2, int id);


    /**
     * 
     * @param connectionId
     * @param connectionHandler
     */
    void addConnection(int connectionId, StompConnectionHandler<String> connectionHandler);

    public void addConnection(int connectionId, BlockingConnectionHandler<String> connectionHandler);


}
