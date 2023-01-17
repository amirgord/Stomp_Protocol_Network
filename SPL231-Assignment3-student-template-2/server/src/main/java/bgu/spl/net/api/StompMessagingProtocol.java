package bgu.spl.net.api;

import bgu.spl.net.srv.Connections;

public interface StompMessagingProtocol<T>  {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     * saves the owner client’s connection id.
	**/

    void start(int connectionId, Connections<T> connections);
    

    /**
     * Used to process a received message T
     * 
     * As in MessagingProtocol, processes a given message. Unlike MessagingProtocol,
     * responses are sent via the connections object send functions (ifneeded)
     * 
     * @param message - the message to process
     */
    void process(String message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
