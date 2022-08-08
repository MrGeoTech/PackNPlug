package net.mrgeotech.networking;

import net.mrgeotech.PackageServer;
import net.mrgeotech.storage.StorageManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NetworkManager implements Runnable {

    private ServerSocketChannel serverChannel;
    private Selector selector;

    public boolean startServer(int port) {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.socket().setReuseAddress(true);

            selector = SelectorProvider.provider().openSelector();

            serverChannel.register(selector, serverChannel.validOps());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        while (PackageServer.RUNNING) {
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        SocketChannel channel = serverChannel.accept();

                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    }

                    if (key.isReadable()) {
                        handle((SocketChannel) key.channel());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Successfully stopped server!");
    }

    public void handle(SocketChannel channel) throws IOException {
        if (channel != null && channel.isOpen()) {
            // Processing incoming packet from bytes
            ByteBuffer buffer = ByteBuffer.allocate(1);
            channel.read(buffer);
            buffer.position(0);
            byte id = buffer.get();
            buffer = ByteBuffer.allocate(4);
            channel.read(buffer);
            buffer.position(0);
            buffer = ByteBuffer.allocate(buffer.getInt());
            channel.read(buffer);
            buffer.position(0);
            String[] request = new String(buffer.array(), StandardCharsets.UTF_8).trim().split(":");

            System.out.println(id + request[0]);

            String name = request[0];
            String version = "latest";
            if (request.length > 1)
                version = request[1];

            // Processing incoming request
            if (id == 0x00) {
                String info = StorageManager.getDownloadUrl(name, version);
                sendData(channel, (byte) 0x01, info);
            } else if (id == 0x02) {
                String info = StorageManager.getInfo(name, version);
                sendData(channel, (byte) 0x03, info);
            } else if (id == 0x04) {
                String names = StorageManager.getFunding(name, version);
                sendData(channel, (byte) 0x05, names);
            }
        }
    }

    private void sendData(SocketChannel channel, byte id, String info) throws IOException {
        ByteBuffer buffer;
        buffer = ByteBuffer.allocate(5 + info.length());
        buffer.put(id);
        byte[] namesBytes = info.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(namesBytes.length);
        buffer.put(namesBytes);
        channel.write(buffer.flip());
    }

}
