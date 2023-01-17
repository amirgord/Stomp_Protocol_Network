/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.net.srv;

import java.io.Closeable;

/**
 * The ConnectionHandler interface for Message of type T
 */
public interface ConnectionHandler<T> extends Closeable {

    /**
     * Comment the following lines (both send methods) for the existing implentations to work.
     * 
     * 
     *
     */

     /*
      * sends msg T to the client. Should be used by the send commands in
      * the Connections implementation
      *
      * @param msg the message to send to the client
      */
    void send(T msg);

}
