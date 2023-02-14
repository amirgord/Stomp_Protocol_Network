package bgu.spl.net.impl.echoClients;

import java.net.*;
import java.util.stream.Stream;
import java.io.*;

public class EchoClientC {

    public static void main(String[] args) throws IOException {

        String msg1 = "CONNECT\naccept-version:1.2\nhost:stomp.cs.bgu.ac.il\nlogin:lilah\npasscode:flowers\n\n";

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket("localhost", Integer.valueOf("7777"));
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println(">>> sending message to server");
            out.write(msg1 + '\0');
            out.flush();

            System.out.println(">>> awaiting response");
            String line = in.readLine();
            System.out.println("message from server:\n"+ line);
            while(!(line = in.readLine()).endsWith("\0"))  // DOESN'T ENDS
                System.out.println(line);
        }
    }
}