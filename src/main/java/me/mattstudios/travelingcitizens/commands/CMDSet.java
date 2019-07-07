package me.mattstudios.travelingcitizens.commands;

import me.mattstudios.travelingcitizens.TravelingCitizens;
import me.mattstudios.travelingcitizens.commands.base.CommandBase;
import me.mattstudios.travelingcitizens.utility.Utils;
import org.bukkit.entity.Player;

public class CMDSet extends CommandBase {

private TravelingCitizens plugin;

    public CMDSet(TravelingCitizens plugin) {
        super("set", "travelingcitizens.set", false, null, 0, 0);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        plugin.getConfig().set("npc-location", Utils.locationToString(player.getLocation()));
        plugin.saveConfig();
        player.sendMessage(Utils.color("&aNPC location set successfully!"));
    }

}
