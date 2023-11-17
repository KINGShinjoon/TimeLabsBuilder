package org.cnits.timelabsbuilder.Command;

import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

import static org.cnits.timelabsbuilder.API.LoadSchematic.loadSchematicToClipboard;
import static org.cnits.timelabsbuilder.API.NpcSpawn.spawnNpcs;
import static org.cnits.timelabsbuilder.API.StartBuilding.startBuilding;

public class TimeLabsCommand implements CommandExecutor {
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
                    ClipboardHolder clipboard = loadSchematicToClipboard(args[1], player);
                    if (clipboard != null) {
                        player.sendMessage(ChatColor.GREEN + "Schematic loaded.");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("buildstart") && args.length == 2) {
                    // NPC 스폰 및 건설 시작
                    try {
                        int amount = Integer.parseInt(args[1]);
                        List<Entity> npcs = spawnNpcs(amount, player.getLocation());
                        ClipboardHolder clipboard = loadSchematicToClipboard(args[1], player); // 스키매틱 이름을 args[1]에서 가져옵니다.
                        if (clipboard != null && npcs != null) {
                            startBuilding(clipboard, player.getLocation(), npcs);
                            player.sendMessage(ChatColor.GREEN + "Building process started with " + amount + " NPCs.");
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number format.");
                    }
                    return true;
                }
                // 여기에 추가 API에 대한 명령어 처리 로직을 추가할 수 있습니다.
            }
        }
        return false;
    }
}
