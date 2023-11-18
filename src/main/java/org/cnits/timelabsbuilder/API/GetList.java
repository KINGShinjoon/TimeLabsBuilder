package org.cnits.timelabsbuilder.API;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.cnits.timelabsbuilder.TimeLabsBuilder;

import java.io.File;



public class GetList {
    private static TimeLabsBuilder plugin;

    public static void setPlugin(TimeLabsBuilder plugin) {
        GetList.plugin = plugin;
    }

    public static void listSchematics(CommandSender sender, int page) {
        File schematicsFolder = plugin.getWorldEditSchematicsFolder();
        if (!schematicsFolder.exists() || !schematicsFolder.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "Schematics folder not found.");
            return;
        }

        File[] files = schematicsFolder.listFiles();
        if (files == null || files.length == 0) {
            sender.sendMessage(ChatColor.RED + "No schematics found.");
            return;
        }

        int itemsPerPage = 5;
        int totalPage = (int) Math.ceil(files.length / (double) itemsPerPage);
        if (page < 1 || page > totalPage) {
            sender.sendMessage(ChatColor.RED + "Invalid page number. There are only " + totalPage + " pages.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Schematics (Page " + page + " of " + totalPage + "):");
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, files.length);
        for (int i = start; i < end; i++) {
            sender.sendMessage(ChatColor.YELLOW + " - " + files[i].getName());
        }
    }
}
