package net.mrgeotech.commands;

import net.mrgeotech.network.NetworkManager;
import net.mrgeotech.network.PackageDownloader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PackNPlugCommand implements CommandExecutor {

    private final String noPermissionMessage = "&cSorry but you do not have permission to execute this command!";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            return returnAndSend(sender, "&6&lPackNPlug Commands:\n" +
                    "&6/packnplug help &cpacknplug.help &eShows the commands of this plugin\n" +
                    "&6/packnplug install &cpacknplug.install &eInstalls specified package (run for more information)\n" +
                    "&6/packnplug uninstall &cpacknplug.uninstall &eUninstalls specified package (run for more information)\n");
        }

        switch (args[0]) {
            case "install":
                if (!sender.hasPermission("packnplug.install")) return returnAndSend(sender, noPermissionMessage);
                if (args.length < 2) return returnAndSend(sender, "&cIncorrect Usage!\n  &c/packnplug install <package-name>\n" +
                        "  &c/packnplug install <package-name> <version>\n" +
                        "  &c/packnplug install <repo> <package-name> <version>");

                // Getting the future
                CompletableFuture<String> future;
                if (args.length == 2) {
                    future = NetworkManager.getDownloadUrl(args[1]);
                } else if (args.length == 3) {
                    future = NetworkManager.getDownloadUrl(args[1], args[2]);
                } else {
                    future = NetworkManager.getDownloadUrl(args[1], args[2], args[3]);
                }

                // Completing the future
                try {
                    future.thenAccept(url -> {
                        if (url == null) {
                            returnAndSend(sender, "&cCould not find a package with that name/version! Trying being more specific in your command!");
                            return;
                        }
                        try {
                            PackageDownloader.downloadPackage(url, args[1]).thenAccept(result -> {
                                if (result)
                                    returnAndSend(sender, "&aPackage was successfully downloaded and installed! Enabling now...");
                                else
                                    returnAndSend(sender, "&cPackage could not be downloaded! Look at console log to diagnose issue!");
                            }).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            returnAndSend(sender, "&cPackage could not be downloaded! Look at console log to diagnose issue!");
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return returnAndSend(sender, "&cPackage could not be downloaded! Look at console log to diagnose issue!");
                }
                break;
            case "uninstall":

                break;
        }

        return true;
    }

    private boolean returnAndSend(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }

}
