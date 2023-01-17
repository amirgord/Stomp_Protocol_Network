package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

public class Tests {

    public static void prettyPrint(Map<String, Object> parsedMessage) {
        System.out.println(parsedMessage.get("command"));
        Map<String, String> headers = (Map<String, String>)parsedMessage.get("headers");
        for (String key : headers.keySet()) {
            System.out.println(key + ": " + headers.get(key));
        }

        String body = (String)parsedMessage.get("body");
        System.out.println(body);
    }

    public static Map<String,Object> testParseMessage_1() {
    
        String message = "CONNECT\n" +
                "accept-version:1.2\n" +
                "host:stomp.cs.bgu.ac.il\n" +
                "login:student\n" +
                "passcode:student\n" +
                "\n" +
                "\u0000";

        // parse the message
        Map<String, Object> parsedMessage = StompParser.parseMessage(message);
        // pretty print the parsed message
        prettyPrint(parsedMessage);

        return parsedMessage;
    }
    
    public static Map<String,Object> testParseMessage_2() {
    
        String message = "CONNECTED\n" +
                "version:1.2\n" +
                "\n" +
                "\u0000";
    
        // parse the message
        Map<String, Object> parsedMessage = StompParser.parseMessage(message);

        // pretty print the parsed message
        prettyPrint(parsedMessage);

        return parsedMessage;
    }
    
    public static Map<String,Object> testParseMessage_3() {
    
        String message = "SEND\n" +
                "destination:/topic/a\n" +
                "\n" +
                "Hello World!\n" +
                "\u0000";
    
        // parse the message
        Map<String, Object> parsedMessage = StompParser.parseMessage(message);

        // pretty print the parsed message
        prettyPrint(parsedMessage);

        return parsedMessage;
    }

    public static void testParseMessage() {

        Map<String,Object> parse_1 = testParseMessage_1(); 
        Map<String,Object> parse_2 = testParseMessage_2();
        Map<String,Object> parse_3 = testParseMessage_3();

        Map<String,Object> expected_1 = new HashMap<String,Object>();
        Map<String,Object> expected_2 = new HashMap<String,Object>();
        Map<String,Object> expected_3 = new HashMap<String,Object>();

        expected_1.put("command", "CONNECT");
        Map<String,String> headers_1 = new HashMap<String,String>();
        headers_1.put("accept-version", "1.2");
        headers_1.put("host", "stomp.cs.bgu.ac.il");
        headers_1.put("login", "student");
        headers_1.put("passcode", "student");
        expected_1.put("headers", headers_1);
        expected_1.put("body", "");

        expected_2.put("command", "CONNECTED");
        Map<String,String> headers_2 = new HashMap<String,String>();
        headers_2.put("version", "1.2");
        expected_2.put("headers", headers_2);
        expected_2.put("body", "");

        expected_3.put("command", "SEND");
        Map<String,String> headers_3 = new HashMap<String,String>();
        headers_3.put("destination", "/topic/a");
        expected_3.put("headers", headers_3);
        expected_3.put("body", "Hello World!");

        // boolean t1 = parse_1.equals(expected_1);
        // boolean t2 = parse_2.equals(expected_2);
        // boolean t3 = parse_3.equals(expected_3);

        // assert(parse_1.equals(expected_1));

        // System.out.println("TEST 1 PASSED: " + t1);
        // System.out.println("TEST 2 PASSED: " + t2);
        // System.out.println("TEST 3 PASSED: " + t3);
        

    }
    
    public static void main(String[] args) {
        
        testParseMessage();

    }
}
