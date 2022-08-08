package net.mrgeotech;

import net.mrgeotech.networking.NetworkManager;
import net.mrgeotech.storage.StorageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PackageServer {

    public static boolean RUNNING = true;
    public static NetworkManager networkManager;

    public static void main(String[] args) {
        StorageManager.DATABASE_URL = "192.168.254.99";
        StorageManager.DATABASE_USERNAME = "u1_cw2jeaNyDk";
        StorageManager.DATABASE_PASSWORD = "Db2!qz3=UJBGZEgg7RVek!i+";

        startServer(8243);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (reader.readLine().equalsIgnoreCase("stop")) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stop();
    }

    public static void startServer(int port) {
        NetworkManager networkManager = new NetworkManager();
        if (!networkManager.startServer(port)) return;
        new Thread(networkManager).start();
        PackageServer.networkManager = networkManager;
    }

    public static void stop() {
        try {
            PackageServer.RUNNING = false;
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
