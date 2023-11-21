package org.cnits.timelabsbuilder.API;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BreakBuilding {
    private static JavaPlugin plugin;

    public static void setPlugin (JavaPlugin plugin){
        BreakBuilding.plugin = plugin;
    }

    public static void breakBuilding(Region region, World world, int NpcCount, List<Entity> npcsset){
        List<Entity> npcs = npcsset;
        List<Region> dividedRegions = divideRegion(region, NpcCount);

        for (int i = 0; i< npcs.size(); i++){
            final Entity npc = npcs.get(i);
            final Region npcRegion = dividedRegions.get(i);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                breakBlocks(npcRegion, world, npc);
            });
        }
    }


    private static List<Region> divideRegion(Region region, int npcCount) {
        List<Region> dividedRegions = new ArrayList<>();

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int width = max.getBlockX() - min.getBlockX() + 1;
        int partWidth = width / npcCount; // 각 NPC가 처리할 영역의 너비

        // 마지막 NPC의 영역을 위해 남은 너비를 계산합니다.
        int remainingWidth = width % npcCount;

        int currentX = min.getBlockX();

        // 각 NPC에 대한 영역을 계산합니다.
        for (int i = 0; i < npcCount; i++) {
            int endX = currentX + partWidth - 1;

            // 마지막 NPC의 경우 남은 너비를 추가합니다.
            if (i == npcCount - 1) {
                endX += remainingWidth;
            }

            BlockVector3 partMin = BlockVector3.at(currentX, min.getBlockY(), min.getBlockZ());
            BlockVector3 partMax = BlockVector3.at(endX, max.getBlockY(), max.getBlockZ());

            dividedRegions.add(new CuboidRegion(partMin, partMax));

            // 다음 영역의 시작점을 업데이트합니다.
            currentX = endX + 1;
        }

        return dividedRegions;
    }

    private static void breakBlocks(Region region, World world, Entity npc) {
        AtomicInteger delayCounter = new AtomicInteger(0);

        // y 좌표를 위에서 아래로 순회합니다.
        for (int y = region.getMaximumPoint().getBlockY(); y >= region.getMinimumPoint().getBlockY(); y--) {
            // x와 z 좌표를 순회합니다.
            for (int x = region.getMinimumPoint().getBlockX(); x <= region.getMaximumPoint().getBlockX(); x++) {
                for (int z = region.getMinimumPoint().getBlockZ(); z <= region.getMaximumPoint().getBlockZ(); z++) {
                    BlockVector3 vector = BlockVector3.at(x, y, z);
                    Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());

                    // 이미 공기(AIR)인 블록은 넘어갑니다.
                    if (block.getType() == Material.AIR) {
                        continue;
                    }

                    // 지연 시간을 설정하고 블록을 부수는 작업을 예약합니다.
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        npc.teleport(new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                        block.setType(Material.AIR, false);
                    }, delayCounter.getAndIncrement());
                }
            }
        }
    }
}
