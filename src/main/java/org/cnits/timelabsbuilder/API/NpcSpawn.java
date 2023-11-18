package org.cnits.timelabsbuilder.API;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class NpcSpawn {
    public static List<Entity> spawnNpcs(int amount, Location location) {
        List<Entity> npcs = new ArrayList<>();
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        for (int i = 0; i < amount; i++) {
            NPC npc = registry.createNPC(EntityType.PLAYER, "Builder");
            npc.spawn(location);
            npcs.add(npc.getEntity());
        }
        return npcs;
    }
}