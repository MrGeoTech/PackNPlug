package net.mrgeotech;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TestClient {

    public static void main(String[] args) {
        System.out.println("Test");
        try (SocketChannel channel = SocketChannel.open()) {
            System.out.println("1");
            System.out.println(getResponse(channel, "example", (byte) 0x00));
            System.out.println("10");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("2");
        }
    }

    private static String getResponse(SocketChannel channel, String request, byte sentId) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5 + request.length());
        buffer.put(sentId);
        byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(requestBytes.length);
        buffer.put(requestBytes);
        channel.connect(new InetSocketAddress("localhost", 8243));
        System.out.println("Connected!");
        channel.write(buffer.flip());
        buffer = ByteBuffer.allocate(1);
        channel.read(buffer);
        buffer.position(0);
        byte id = buffer.get();
        buffer = ByteBuffer.allocate(4);
        channel.read(buffer);
        buffer.position(0);
        int length = buffer.getInt();
        buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        buffer.position(0);
        channel.close();
        System.out.println("Done!");
        if (id == sentId + 1)
            return new String(buffer.array(), StandardCharsets.UTF_8).trim();
        else
            return null;
    }

}
