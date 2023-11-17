package org.cnits.timelabsbuilder.API;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class StartBuilding {
    static JavaPlugin plugin;
    public static void setPlugin(JavaPlugin plugin) {
        StartBuilding.plugin = plugin;
    }
    public static void startBuilding(ClipboardHolder clipboardHolder, Location startLocation, List<Entity> npcs) {
        Clipboard clipboard = clipboardHolder.getClipboard();
        BlockVector3 origin = clipboard.getOrigin();
        World world = startLocation.getWorld();
        Region region = clipboard.getRegion();

        // NPC 수에 따라 영역을 나눕니다.
        List<Region> dividedRegions = divideRegion(region, npcs.size());

        for (int i = 0; i < npcs.size(); i++) {
            Entity npc = npcs.get(i);
            Region npcRegion = dividedRegions.get(i);

            // 각 NPC가 해당 영역에서 블록을 설치합니다.
            npcRegion.forEach(blockVector3 -> {
                BlockStateHolder block = clipboard.getBlock(blockVector3);
                Material material = Material.getMaterial(block.getBlockType().getId().toUpperCase());

                if (material != null && material != Material.AIR) {
                    BlockVector3 relativePosition = blockVector3.subtract(origin);
                    Location blockLocation = new Location(world, startLocation.getX() + relativePosition.getX(), startLocation.getY() + relativePosition.getY(), startLocation.getZ() + relativePosition.getZ());
                    setBlockAsync(world, blockLocation, material, npc);
                }
            });
        }
    }

    private static void setBlockAsync(World world, Location location, Material material, Entity npc) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            // NPC가 블록 위치로 이동하는 로직을 추가할 수 있습니다.
            // 예: npc.teleport(new Location(world, location.getX(), location.getY(), location.getZ()));
            Block block = world.getBlockAt(location);
            block.setType(material, false);
        });
    }

    // 스키매틱 영역을 나누는 메소드
    private static List<Region> divideRegion(Region region, int parts) {
        List<Region> dividedRegions = new ArrayList<>();

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int width = max.getBlockX() - min.getBlockX();
        int partWidth = width / parts;

        for (int i = 0; i < parts; i++) {
            int startX = min.getBlockX() + (partWidth * i);
            int endX = (i == parts - 1) ? max.getBlockX() : startX + partWidth;

            BlockVector3 partMin = BlockVector3.at(startX, min.getBlockY(), min.getBlockZ());
            BlockVector3 partMax = BlockVector3.at(endX, max.getBlockY(), max.getBlockZ());

            Region partRegion = new CuboidRegion(partMin, partMax);
            dividedRegions.add(partRegion);
        }

        return dividedRegions;
    }
}
