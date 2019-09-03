package me.mattstudios.travelingcitizens;

import me.mattstudios.travelingcitizens.commands.CMDId;
import me.mattstudios.travelingcitizens.commands.CMDReload;
import me.mattstudios.travelingcitizens.commands.CMDSet;
import me.mattstudios.travelingcitizens.commands.base.CommandHandler;
import me.mattstudios.travelingcitizens.schedulers.SpawnScheduler;
import me.mattstudios.travelingcitizens.utility.Utils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.getScheduler;

public final class TravelingCitizens extends JavaPlugin implements CommandExecutor {

    private CommandHandler commandHandler;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        if (!getConfig().contains("npc-data.npc-type")) getConfig().set("npc-data.npc-type", "PLAYER");

        commandHandler = new CommandHandler(this);
        commandHandler.enable();

        Objects.requireNonNull(getCommand("tc")).setExecutor(commandHandler);
        Stream.of(new CMDSet(this), new CMDReload(this), new CMDId(this)).forEach(commandHandler::register);

        if (!getConfig().contains("npc-location") || Objects.requireNonNull(getConfig().getString("npc-location")).isEmpty()) {
            Utils.info("&5[TravelingCitizens] &cLocation not set, NPC won't spawn!");
        } else {
            Utils.info("&5[TravelingCitizens] &aNPC is now spawning!");
        }

        getScheduler().runTaskLater(this, () -> Utils.checkSpawnedNPC(this), 2 * 20L);
        getScheduler().scheduleSyncRepeatingTask(this, new SpawnScheduler(this), 30 * 20L, getConfig().getInt("npc-repeat") * 20L);
    }

    @Override
    public void onDisable() {
        if (commandHandler != null) {
            commandHandler.disable();
        }
    }
}
