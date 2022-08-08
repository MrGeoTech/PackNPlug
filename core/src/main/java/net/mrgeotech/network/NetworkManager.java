package net.mrgeotech.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NetworkManager {

    private static final Map<String, List<String>> CACHE = new HashMap<>();

    public static CompletableFuture<String> getDownloadUrl(String pName) {
        return CompletableFuture.supplyAsync(() -> {
            try (SocketChannel channel = SocketChannel.open()) {
                return getResponse(channel, pName, (byte) 0x00);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<String> getDownloadUrl(String pName, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try (SocketChannel channel = SocketChannel.open()) {
                String url = pName + ":" + version;
                return getResponse(channel, url, (byte) 0x00);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<String> getDownloadUrl(String repo, String pName, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try (SocketChannel channel = SocketChannel.open()) {
                String url = repo + "/" + pName + ":" + version;
                return getResponse(channel, url, (byte) 0x00);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<List<String>> getNames(String regex) {
        return CompletableFuture.supplyAsync(() -> {
            for (Map.Entry<String, List<String>> entry : CACHE.entrySet()) {
                if (entry.getKey().startsWith(regex)) {
                    return List.of(
                            entry.getValue().stream().filter(s -> s.startsWith(regex))
                                    .toArray(String[]::new));
                }
            }

            try (SocketChannel channel = SocketChannel.open()) {
                String response = getResponse(channel, regex, (byte) 0x02);
                if (response == null || response.equalsIgnoreCase("[]")) return new ArrayList<>();
                List<String> names = List.of(
                        response.replaceAll("\\[", "")
                                .replaceAll("]", "")
                                .split(",")
                );
                CACHE.put(regex, names);
                return names;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }

    private static String getResponse(SocketChannel channel, String request, byte sentId) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5 + request.length());
        buffer.put(sentId);
        byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(requestBytes.length);
        buffer.put(requestBytes);
        channel.connect(new InetSocketAddress("mrgeotech.net", 8243));
        channel.write(buffer.flip());
        buffer = ByteBuffer.allocate(1);
        channel.read(buffer);
        byte id = buffer.get();
        buffer = ByteBuffer.allocate(4);
        channel.read(buffer);
        int length = buffer.getInt();
        buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        if (id == sentId + 1)
            return new String(buffer.array(), StandardCharsets.UTF_8).trim();
        else
            return null;
    }

}
