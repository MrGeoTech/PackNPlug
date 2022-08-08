package net.mrgeotech.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

public class PackageDownloader {

    public static CompletableFuture<Boolean> downloadPackage(String url, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try(InputStream inputStream = new URL(url).openStream()) {
                Files.copy(inputStream, Paths.get(name), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                return false;
            }
        });
    }

}
