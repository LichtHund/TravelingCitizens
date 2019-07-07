package me.mattstudios.travelingcitizens.commands;

import me.mattstudios.travelingcitizens.TravelingCitizens;
import me.mattstudios.travelingcitizens.commands.base.CommandBase;
import me.mattstudios.travelingcitizens.utility.Utils;
import org.bukkit.entity.Player;

public class CMDReload extends CommandBase {

private TravelingCitizens plugin;

    public CMDReload(TravelingCitizens plugin) {
        super("reload", "travelingcitizens.reload", false, null, 0, 0);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Utils.checkSpawnedNPC(plugin);
        plugin.reloadConfig();
        player.sendMessage(Utils.color("&aConfig file reloaded!"));
    }

}
