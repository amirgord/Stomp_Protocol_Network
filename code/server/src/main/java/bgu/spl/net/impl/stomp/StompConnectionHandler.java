package bgu.spl.net.impl.stomp;


import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.NonBlockingConnectionHandler;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Reactor;

public class StompConnectionHandler<T> extends NonBlockingConnectionHandler<T> {

    // protected String userName;   ?
    // protected String subscriptionId;   ?
    protected int connectionId;
    protected boolean isConnected;
    protected List<channel> channelsList;

    public final StompProtocol<T> stompProtocol;

    public StompConnectionHandler(
            MessageEncoderDecoder<T> reader,
            StompProtocol<T> protocol,
            SocketChannel chan,
            Reactor reactor,
            int id) {
        super(reader, null, chan, reactor);
        this.stompProtocol = protocol;
        this.connectionId = id;
        this.isConnected = false;
        // is it bidirectional?
        this.channelsList = null;
    }

    
    @Override
    public Runnable continueRead() {
        ByteBuffer buf = super.leaseBuffer();
        
        boolean success = false;
        try {
            success = chan.read(buf) != -1;
        } catch (IOException ex) {
            if(!(ex instanceof ClosedChannelException))
                ex.printStackTrace();
        }
        
        if (success) {
            buf.flip();
            return () -> {
                try {
                    while (buf.hasRemaining()) {
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) 
                        {
                            stompProtocol.process((String)nextMessage);
                        }
                    }
                } finally {
                    releaseBuffer(buf);
                }
            };
        } else {
            releaseBuffer(buf);
            close();
            return null;
        }
        
    }

    @Override
    public void continueWrite() {
        while (!writeQueue.isEmpty()) {
            try {
                ByteBuffer top = writeQueue.peek();
                chan.write(top);
                if (top.hasRemaining()) {
                    return;
                } else {
                    writeQueue.remove();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
            }
        }

        if (writeQueue.isEmpty()) {
            try {
                if (stompProtocol.shouldTerminate()) close();
                else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);
            } catch(CancelledKeyException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void send(T msg) {

        // add the message to the write queue
        writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
        reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);    
    }
    
}
