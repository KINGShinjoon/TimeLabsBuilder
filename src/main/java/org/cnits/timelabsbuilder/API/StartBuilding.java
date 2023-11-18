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
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        List<Region> dividedRegions = divideRegion(clipboard,region, npcs.size());
        plugin.getServer().getLogger().info("clipboard: "+clipboard);
        plugin.getServer().getLogger().info("origin: "+origin);
        plugin.getServer().getLogger().info("World: "+world);
        plugin.getServer().getLogger().info("region: "+region);
        plugin.getServer().getLogger().info("dividedRegions: "+dividedRegions);


        AtomicInteger completedNpcs = new AtomicInteger(0);
        long totalDelay = 0;
        for (int i = 0; i < npcs.size(); i++) {
            final Entity npc = npcs.get(i);
            final Region npcRegion = dividedRegions.get(i);

            // 각 NPC에 대한 작업을 별도의 비동기 작업으로 처리
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                long delay = 0;

                // 일반 블록 먼저 설치
                processBlocks(npcRegion, clipboard, origin, world, startLocation, npc, delay, false
                );

                // 특별한 블록 나중에 설치
                //processBlocks(npcRegion, clipboard, origin, world, startLocation, npc, delay, false);


            });
//            Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                npc.remove();
//                if (completedNpcs.incrementAndGet() == npcs.size()) {
//                    Bukkit.getServer().broadcastMessage("작업이 완료되었습니다."); // 모든 NPC 작업 완료 시 메시지 표시
//                }
//            }, delay);
        }

    }
    private static boolean isSpecialBlock(Material material) {
        // 특정 유형의 블록을 식별하는 로직
        return material.name().endsWith("ANVIL") ||
                material.name().endsWith("STAIRS") ||
                material.name().endsWith("SLAB") ||
                material.name().contains("FENCE");
    }

    private static void processBlocks(Region region, Clipboard clipboard, BlockVector3 origin, World world, Location startLocation, Entity npc, long delay, boolean processSpecial) {
        for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++) {
            for (BlockVector3 blockVector3 : region) {
                if (blockVector3.getBlockY() == y) {
                    try {
                        BlockStateHolder block = clipboard.getBlock(blockVector3);
                        String blockTypeName = block.getBlockType().getName();
                        Material material = Material.matchMaterial(blockTypeName);

                        if (material != null && material != Material.AIR && isSpecialBlock(material) == processSpecial) {
                            BlockVector3 relativePosition = blockVector3.subtract(origin);
                            Location blockLocation = new Location(world, startLocation.getX() + relativePosition.getX(), startLocation.getY() + relativePosition.getY(), startLocation.getZ() + relativePosition.getZ());

                            setBlockAsync(world, blockLocation, material, npc, delay);
                            delay += 2; // 0.2초 지연 추가
                        }
                    } catch (Exception e) {
                        plugin.getServer().getLogger().severe("Error while setting block: " + e.getMessage());
                    }
                }
            }
        }
    }
    private static void setBlockAsync(World world, Location location, Material material, Entity npc, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            npc.teleport(new Location(world, location.getX(), location.getY(), location.getZ()));
            Block block = world.getBlockAt(location);
            block.setType(material, false);
        }, delay);
    }


    // 스키매틱 영역을 나누는 메소드
    private static List<Region> divideRegion(Clipboard clipboard, Region region, int npcCount) {
        List<Region> dividedRegions = new ArrayList<>();

        int partsX = (int) Math.ceil(Math.sqrt(npcCount));
        int partsZ = (int) Math.ceil((double) npcCount / partsX);

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int width = max.getBlockX() - min.getBlockX();
        int depth = max.getBlockZ() - min.getBlockZ();

        int partWidth = width / partsX;
        int partDepth = depth / partsZ;

        for (int x = 0; x < partsX; x++) {
            for (int z = 0; z < partsZ; z++) {
                BlockVector3 partMin = BlockVector3.at(
                        min.getBlockX() + (partWidth * x),
                        min.getBlockY(),
                        min.getBlockZ() + (partDepth * z)
                );
                BlockVector3 partMax = BlockVector3.at(
                        (x == partsX - 1) ? max.getBlockX() : partMin.getBlockX() + partWidth,
                        max.getBlockY(),
                        (z == partsZ - 1) ? max.getBlockZ() : partMin.getBlockZ() + partDepth
                );

                Region partRegion = new CuboidRegion(partMin, partMax);
                if (regionContainsGroundBlocks(clipboard, partRegion)) {
                    dividedRegions.add(partRegion);
                }
            }
        }

        return dividedRegions;
    }

    private static boolean regionContainsGroundBlocks(Clipboard clipboard, Region region) {
        for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++) {
            boolean hasBlock = false;
            for (int x = region.getMinimumPoint().getBlockX(); x <= region.getMaximumPoint().getBlockX(); x++) {
                for (int z = region.getMinimumPoint().getBlockZ(); z <= region.getMaximumPoint().getBlockZ(); z++) {
                    if (!clipboard.getBlock(BlockVector3.at(x, y, z)).getBlockType().getMaterial().isAir()) {
                        hasBlock = true;
                        break;
                    }
                }
                if (hasBlock) break;
            }
            if (hasBlock) return true; // 최소 한 개의 블록이 땅에 존재하는 경우
        }
        return false;
    }


    private static boolean regionContainsBlocks(Clipboard clipboard, Region region) {
        for (BlockVector3 vector : region) {
            if (!clipboard.getBlock(vector).getBlockType().getMaterial().isAir()) {
                return true;
            }
        }
        return false;
    }


}
