package bgu.spl.net.impl.echoClients;

import java.net.*;
import java.util.stream.Stream;

import bgu.spl.net.impl.stomp.StompEncoderDecoder;

import java.io.*;

public class EchoClientA {

    public static void main(String[] args) throws IOException {

        String msg1 = "CONNECT\naccept-version:1.2\nhost:stomp.cs.bgu.ac.il\nlogin:eran\npasscode:films\n\n";
        String msg2 = "SUBSCRIBE\ndestination:/germany_spain\nid:17\nreceipt:171\n\n";
        // String msg3 = "UNSUBSCRIBE\nid:17\nreceipt:120\n\n";
        String msg4 = "SEND\ndestination:/germany_spain\n\nHello World!\n\n";
        String msg5 = "DISCONNECT\nreceipt:109\n\n";


        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket("localhost", Integer.valueOf("7777"));) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String line = "";


            // ------------- 1 -------------

            System.out.println(">>> sending message connect to server");
            out.write(msg1 + '\0');
            out.flush();

            System.out.println(">>> awaiting response");

            String msg = "";
            StompEncoderDecoder encdec = new StompEncoderDecoder();
            int read;
            while ((read = in.read()) >= 0) {
                msg = encdec.decodeNextByte((byte) read);
                if (msg != null) {
                    break;
                }
            }
            System.out.println(msg);
            
            
            // ------------- 2 -------------

            System.out.println(">>> sending message subscribe to server");
            out.write(msg2 + '\0');
            out.flush();

            System.out.println(">>> awaiting response");

            msg = "";
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while ((read = in.read()) >= 0) {
                msg = encdec.decodeNextByte((byte) read);
                if (msg != null) {
                    break;
                }
            }
            System.out.println(msg);


            // // ------------- 3 -------------
        
            System.out.println(">>> awaiting to msg from other client");

            msg = "";
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while ((read = in.read()) >= 0) {
                msg = encdec.decodeNextByte((byte) read);
                if (msg != null) {
                    break;
                }
            }
            System.out.println(msg);



            // ------------- 5 -------------

            System.out.println(">>> sending message disconnect to server");
            out.write(msg5 + '\0');
            out.flush();

            msg = "";
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while ((read = in.read()) >= 0) {
                msg = encdec.decodeNextByte((byte) read);
                if (msg != null) {
                    break;
                }
            }
            if(msg.equals(""))
                System.out.println("the message was empty");
            System.out.println(msg);

        }
    }
}