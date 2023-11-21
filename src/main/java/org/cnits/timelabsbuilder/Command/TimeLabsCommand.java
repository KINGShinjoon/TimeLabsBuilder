package org.cnits.timelabsbuilder.Command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.cnits.timelabsbuilder.API.NpcSpawn;

import java.util.List;

import static org.cnits.timelabsbuilder.API.BreakBuilding.breakBuilding;
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
                switch (args[0].toLowerCase()) {
                    case "schematic":
                        if (args.length == 2) {
                            // 스키매틱 불러오기
                            currentSchematic = loadSchematicToClipboard(args[1], player);
                            if (currentSchematic != null) {
                                player.sendMessage(ChatColor.GREEN + "Schematic '" + args[1] + "' loaded.");
                            }
                            return true;
                        }
                        break;
                    case "buildstart":
                        if (args.length == 2) {
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
                        }
                        break;
                    case "list":
                        if (args.length == 2) {
                            try {
                                int page = Integer.parseInt(args[1]);
                                listSchematics(sender, page);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid page number.");
                            }
                            return true;
                        }
                        break;
                    case "breakbuilding":
                        if (args.length == 2) {

                            try {
                                int npcCount = Integer.parseInt(args[1]);

                                com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
                                LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(wePlayer);
                                RegionSelector regionSelector = localSession.getRegionSelector(wePlayer.getWorld());
                                if (!regionSelector.isDefined()) {
                                    // 영역이 선택되지 않았을 경우
                                    player.sendMessage(ChatColor.RED + "You need to select a region first using WorldEdit.");
                                    return true;
                                }

                                // 선택된 영역을 가져옵니다.
                                Region region = regionSelector.getRegion();
                                breakBuilding(region, player.getWorld(), npcCount, NpcSpawn.spawnNpcs(npcCount, player.getLocation()));
                                player.sendMessage(ChatColor.GREEN + "Breaking process started with " + npcCount + " NPCs.");
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Invalid number format.");
                            } catch (IncompleteRegionException e) {
                                player.sendMessage(ChatColor.RED + "Your WorldEdit selection is incomplete.");
                            }
                            return true;
                        }
                        break;
                    default:
                        // 잘못된 명령어 처리
                        player.sendMessage(ChatColor.RED + "Invalid command.");
                        return true;
                }
            }
        }
        return false;
    }


}
