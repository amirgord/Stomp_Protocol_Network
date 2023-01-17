package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {

        Server.threadPerClient(
                7777, //port
                () -> new StompProtocol<String>(), //protocol factory
                StompEncoderDecoder::new //message encoder decoder factory
        ).serve();

        // Server.reactor(
        //         Runtime.getRuntime().availableProcessors(),
        //         7777, //port
        //         () ->  new StompProtocol<String>(), //protocol factory
        //         StompEncoderDecoder::new //message encoder decoder factory
        // ).serve();


    }
}
