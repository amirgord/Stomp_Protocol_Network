package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

public class StompParser {
    
    public static Map<String, String> parseHeaders(String message) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.contains(":")) {
                int index = line.indexOf(":");
                String key = line.substring(0, index).trim();
                String value = line.substring(index + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    public static String parseBody(String message) {
        int bodyIndex = message.indexOf("\n\n");
        if (bodyIndex > 0) {
            return message.substring(bodyIndex + 2);
        }
        return "";
    }

    public static Map<String, Object> parseMessage(String message) {
        // Clear the message till the first letter:
        int firstLetterIndex = 0;
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) > 'A' & message.charAt(i) < 'Z') {
                firstLetterIndex = i;
                break;
            }
        }
        message = message.substring(firstLetterIndex);

        // Parse the message:
        Map<String, Object> parsedMessage = new HashMap<>();
        Map<String, String> headers = parseHeaders(message);
        String body = parseBody(message);
        parsedMessage.put("command", message.substring(0, message.indexOf("\n")));
        parsedMessage.put("headers", headers);
        parsedMessage.put("body", body);
        return parsedMessage;
    }

    public static boolean isValidMessage(String message) {
        if (!message.startsWith("CONNECT") && !message.startsWith("SEND") && !message.startsWith("SUBSCRIBE") && !message.startsWith("UNSUBSCRIBE") && !message.startsWith("DISCONNECT") && !message.startsWith("MESSAGE") && !message.startsWith("RECEIPT")){
        // Check that the message starts with a command (CONNECT, SEND, etc.)
            return false;
        }

        // Check that the message ends with a null byte
        if (!message.endsWith("\n")) {
            return false;
        }

        // Check that there is at least one header line
        if (!message.contains("\n")) {
            return false;
        }

        // Check that there is a blank line between the headers and the body
        int bodyIndex = message.indexOf("\n\n");
        if (bodyIndex < 0) {
            return false;
        }

        return true;
    }

}
