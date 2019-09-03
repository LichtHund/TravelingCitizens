package me.mattstudios.travelingcitizens.commands;

import me.mattstudios.travelingcitizens.TravelingCitizens;
import me.mattstudios.travelingcitizens.commands.base.CommandBase;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.command.CommandContext;
import org.bukkit.entity.Player;

import static me.mattstudios.travelingcitizens.utility.Utils.color;
import static me.mattstudios.utils.NumbersUtils.isInteger;

public class CMDId extends CommandBase {

    private TravelingCitizens plugin;

    public CMDId(TravelingCitizens plugin) {
        super("id", "travelingcitizens.id", false, null, 0, 1);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        int id = 0;
        if (args.length == 0) {
            if (CitizensAPI.getDefaultNPCSelector().getSelected(player) == null) {
                player.sendMessage(color("&cYou must either select an NPC or input it's ID!"));
                return;
            }

            id = CitizensAPI.getDefaultNPCSelector().getSelected(player).getId();
        }

        if (args.length == 1) {
            if (!isInteger(args[0])) {
                player.sendMessage(color("&cThe NPC ID must be a number!"));
                return;
            }

            id = Integer.parseInt(args[0]);
        }

        plugin.getConfig().set("npc-id", id);
        plugin.saveConfig();

        if (CitizensAPI.getNPCRegistry().getById(id) != null) {
            CitizensAPI.getNPCRegistry().getById(id).despawn();
        }

        Citizens plugin2 = (Citizens) CitizensAPI.getPlugin();
        plugin2.storeNPCs(new CommandContext(args));

        player.sendMessage(color("&aNPC ID set successfully!"));
    }

}
