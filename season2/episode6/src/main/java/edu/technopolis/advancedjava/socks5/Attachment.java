package edu.technopolis.advancedjava.socks5;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class Attachment {

    SelectionKey peer;

    ByteBuffer writeBuffer;
    ByteBuffer readBuffer;

    public Attachment() {
        writeBuffer = ByteBuffer.allocate(8*1024);
        readBuffer = ByteBuffer.allocate(8*1024);
    }

}
