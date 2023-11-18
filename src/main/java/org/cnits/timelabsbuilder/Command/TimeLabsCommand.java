package org.cnits.timelabsbuilder.Command;

import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

import static net.citizensnpcs.api.CitizensAPI.getDataFolder;
import static org.cnits.timelabsbuilder.API.GetList.listSchematics;
import static org.cnits.timelabsbuilder.API.LoadSchematic.loadSchematicToClipboard;
import static org.cnits.timelabsbuilder.API.NpcSpawn.spawnNpcs;
import static org.cnits.timelabsbuilder.API.StartBuilding.startBuilding;

public class TimeLabsCommand implements CommandExecutor {

    private ClipboardHolder currentSchematic = null;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("timelabsbuilder")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("schematic") && args.length == 2) {
                    // 스키매틱 불러오기
                    currentSchematic = loadSchematicToClipboard(args[1], player);
                    if (currentSchematic != null) {
                        player.sendMessage(ChatColor.GREEN + "Schematic '" + args[1] + "' loaded.");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("buildstart") && args.length == 2) {
                    // 건설 시작
                    if (currentSchematic == null) {
                        player.sendMessage(ChatColor.RED + "Please load a schematic first using '/tb schematic [name]'.");
                        return true;
                    }
                    try {
                        int amount = Integer.parseInt(args[1]);
                        List<Entity> npcs = spawnNpcs(amount, player.getLocation());
                        if (npcs != null) {
                            startBuilding(currentSchematic, player.getLocation(), npcs);
                            player.sendMessage(ChatColor.GREEN + "Building process started with " + amount + " NPCs.");
                            currentSchematic = null;
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number format.");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("list") && args.length == 2) {
                    try {
                        int page = Integer.parseInt(args[1]);
                        listSchematics(sender, page);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid page number.");
                    }
                    return true;
                }
                // 여기에 추가 API에 대한 명령어 처리 로직을 추가할 수 있습니다.
            }
        }
        return false;
    }


}
