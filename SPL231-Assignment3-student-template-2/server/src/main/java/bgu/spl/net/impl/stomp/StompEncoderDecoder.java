package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;

public class StompEncoderDecoder<T> implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public String decodeNextByte(byte nextByte) {
        if (nextByte == '\0') {
            return popString();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(String message) {
        return (message + "\0").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = copyArray(bytes, 2 * len);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len);
        len = 0;
        return result;
    }

    private byte[] copyArray(byte[] old, int newLen) {
        byte[] copy = new byte[newLen];
        System.arraycopy(old, 0, copy, 0, Math.min(old.length, newLen));
        return copy;
    }

}
    

