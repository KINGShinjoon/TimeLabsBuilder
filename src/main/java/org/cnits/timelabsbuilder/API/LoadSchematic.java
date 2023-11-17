package org.cnits.timelabsbuilder.API;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static net.citizensnpcs.api.CitizensAPI.getDataFolder;

public class LoadSchematic {
    public static ClipboardHolder loadSchematicToClipboard(String schematicName, Player player) {
        // 'schematics' 폴더 경로 생성
        File schematicsFolder = new File(getDataFolder(), "schematics");

        // 폴더가 존재하지 않으면 생성
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdirs();
        }

        // 스키매틱 파일 경로
        File schematicFile = new File(schematicsFolder, schematicName + ".schematic");

        if (!schematicFile.exists()) {
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
}