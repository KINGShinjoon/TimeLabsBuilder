package org.cnits.timelabsbuilder.API;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cnits.timelabsbuilder.TimeLabsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static net.citizensnpcs.api.CitizensAPI.getDataFolder;

public class LoadSchematic {
    private static TimeLabsBuilder plugin;

    public static void setPlugin(TimeLabsBuilder plugin) {
        LoadSchematic.plugin = plugin;
    }
    public static ClipboardHolder loadSchematicToClipboard(String schematicName, Player player) {
        File schematicsFolder = plugin.getWorldEditSchematicsFolder();
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdirs();
        }

        // .schematic 및 .schem 확장자를 모두 확인
        File schematicFile = findSchematicFile(schematicsFolder, schematicName);

        if (schematicFile == null) {
            player.sendMessage(ChatColor.RED + "Schematic file not found.");
            return null;
        }

        try (FileInputStream fis = new FileInputStream(schematicFile)) {
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            try (ClipboardReader reader = format.getReader(fis)) {
                return new ClipboardHolder(reader.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Error loading schematic.");
            return null;
        }
    }

    private static File findSchematicFile(File folder, String schematicName) {
        File schematicFile = new File(folder, schematicName + ".schematic");
        if (schematicFile.exists()) {
            return schematicFile;
        }

        schematicFile = new File(folder, schematicName + ".schem");
        if (schematicFile.exists()) {
            return schematicFile;
        }

        return null;
    }
}
