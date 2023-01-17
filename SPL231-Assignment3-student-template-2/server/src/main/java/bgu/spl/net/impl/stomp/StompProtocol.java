package bgu.spl.net.impl.stomp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// import javax.lang.model.util.ElementScanner14;

import java.util.HashMap;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.Reactor;
import java.util.concurrent.ConcurrentHashMap;



public class StompProtocol<T> implements StompMessagingProtocol<T>{

    private boolean shouldTerminate = false;
    private Connections connections;
    private boolean isConnected;
    private int connectionId;

    // private List<Integer> connectionIds;


    public StompProtocol() {
        // connectionIds = new ArrayList<Integer>();
        this.connectionId = -1;
        // System.out.println("StompProtocol created, connectionId: " + connectionId);
        isConnected = false;
    }

    @Override
    public void start(int connectionId,Connections<T> connections) {
        this.connections = connections;
        this.connectionId = connectionId;
        
    }

    @Override
    public void process(String message) {
        System.out.println(">>> Server process msg:");
        System.out.println(message);
        String response = "";

        // check if message is valid
        // if (!StompParser.isValidMessage(message)){
        //     // send error message
        //     return;
        // }

        //parse message
        Map<String, Object> parsedMessage = StompParser.parseMessage(message);
        HashMap<String,String> headers = (HashMap<String,String>)parsedMessage.get("headers");

        // handle message according to the frame type
        String c = (String)parsedMessage.get("command");
                switch ((String)parsedMessage.get("command")) {
            case "CONNECT":

                String userName = headers.get("login");
                String password = headers.get("passcode");
                User user = ((ConnectionsImpl<?>)connections).connectionIdToUser.get(connectionId);
                

                //verify:
                if(isConnected) //the user already logged in:
                    response = "ERROR\nmessage:The client is already logged in, log out before trying again\n\n" + '\u0000';
                else
                    response = connections.verifyConnection(userName, password, connectionId);
                
                
                if(response.charAt(0) == 'C')
                    isConnected = true;

                // connect to the server
                // make a connection handler for the client
                // add the connection handler to the connections list
                // send connected message
                break;

            case "SEND":

                // check if the subscription subscribed to the channel
                // if not, send error message
                if(!isConnected){
                    response = "ERROR\nmessage:You are not logged in\n\n" + '\u0000';
                    break;
                }

                // if yes, send the message to all the subscribers
                connections.send(headers.get("destination"), parsedMessage.get("body"), connectionId);

                break;

            case "SUBSCRIBE":
                // add the subscription to the channel (create the channel if not exists)
                // send subscribed message

                response = connections.addSubscription(headers.get("destination"), connectionId, Integer.parseInt(headers.get("id")), Integer.parseInt(headers.get("receipt")));                

                break;

            case "UNSUBSCRIBE":

                // remove the subscription from games

                response = connections.unsubscribe( connectionId, Integer.parseInt(headers.get("receipt")), Integer.parseInt(headers.get("id")));

                break;

            case "DISCONNECT":
                // remove the connection from the connections list
                // send disconnected message
                // terminate the connection
                
                connections.disconnect(connectionId, Integer.parseInt(headers.get("receipt")));

                break;
            
            default:
                response = "ERROR\nmessage:Wrong command\n\n" + '\u0000';
                // send error message
                break;
        }
        if(!response.equals("")) {
            connections.send(connectionId, response);
        }
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
    
}
