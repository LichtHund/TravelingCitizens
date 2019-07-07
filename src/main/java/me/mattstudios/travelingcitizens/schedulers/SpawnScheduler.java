package me.mattstudios.travelingcitizens.schedulers;

import me.mattstudios.travelingcitizens.TravelingCitizens;
import me.mattstudios.travelingcitizens.handlers.NPCSpawnHandler;

import java.util.Objects;

public class SpawnScheduler implements Runnable {

    private TravelingCitizens plugin;
    private NPCSpawnHandler npcSpawnHandler;

    public SpawnScheduler(TravelingCitizens plugin) {
        this.plugin = plugin;
        this.npcSpawnHandler = new NPCSpawnHandler(plugin);
    }

    @Override
    public void run() {
        if (!plugin.getConfig().contains("npc-location") || Objects.requireNonNull(plugin.getConfig().getString("npc-location")).isEmpty()) return;



        npcSpawnHandler.trySpawn();
    }
}
