package org.cnits.timelabsbuilder.API;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NpcSpawn {
    private static final String[] SKIN_NAMES = {"yeoubi_WY", "GBFmeta", "iMasterProX", "_KingAura","SterD__","s_soha","_ddolkong","hongddibros"}; // 스킨 이름 목록

    public static List<Entity> spawnNpcs(int amount, Location location) {
        List<Entity> npcs = new ArrayList<>();
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        Random random = new Random();

        for (int i = 0; i < amount; i++) {
            String randomSkinName = SKIN_NAMES[random.nextInt(SKIN_NAMES.length)];
            NPC npc = registry.createNPC(EntityType.PLAYER, randomSkinName);
            npc.spawn(location);

            npc.spawn(location);
            npcs.add(npc.getEntity());
        }
        return npcs;
    }
}