package me.mattstudios.travelingcitizens.handlers;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.travelingcitizens.TravelingCitizens;
import me.mattstudios.travelingcitizens.utility.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.mattstudios.travelingcitizens.utility.Utils.color;
import static org.bukkit.Bukkit.getScheduler;

public class NPCSpawnHandler {

    private TravelingCitizens plugin;
    private List<NPC> spawnedNpcs = new ArrayList<>();

    public NPCSpawnHandler(TravelingCitizens plugin) {
        this.plugin = plugin;
    }

    public void trySpawn() {

        if (!spawnedNpcs.isEmpty()) return;

        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        if (!plugin.getConfig().contains("chance")) return;

        if (!Utils.shouldSpawn(plugin.getConfig().getInt("chance"))) return;

        System.out.println("here");

        NPC npc;
        int id = 0;

        if (plugin.getConfig().contains("npc-id")) {
            id = plugin.getConfig().getInt("npc-id");
        }

        if (id != 0 && CitizensAPI.getNPCRegistry().getById(id) != null) {
            npc = CitizensAPI.getNPCRegistry().getById(id);
        } else {

            EntityType npcType = EntityType.PLAYER;

            if (plugin.getConfig().contains("npc-data.npc-type")) {
                String type = plugin.getConfig().getString("npc-data.npc-type");
                if (containsType(type)) npcType = EntityType.valueOf(type);
            }

            npc = CitizensAPI.getNPCRegistry().createNPC(npcType, color(plugin.getConfig().getString("npc-data.display-name")));
            Utils.setSkin(npc, plugin.getConfig().getString("npc-data.texture-data"), plugin.getConfig().getString("npc-data.texture-signature"));
        }

        if (plugin.getConfig().getBoolean("npc-data.look-close")) npc.getTrait(LookClose.class).lookClose(true);

        List<String> set = Utils.setToList(Objects.requireNonNull(plugin.getConfig().getConfigurationSection("npc-data.commands")).getKeys(false));
        int random = new Random().nextInt(set.size());
        for (String commandData : plugin.getConfig().getStringList("npc-data.commands." + set.get(random))) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] (.*)");
            Matcher matcher = pattern.matcher(commandData);
            if (matcher.find()) {
                boolean left = false;
                String command = matcher.group(2);

                if (command.contains("-l")) {
                    left = true;
                    command = command.replace("-l", "");
                }

                CitizensCMD.getApi().addCommand(npc.getId(), matcher.group(1), command, left);
            }
        }

        Location npcLocation = Utils.stringToLocation(plugin.getConfig().getString("npc-location"));

        for (String messageString : plugin.getConfig().contains("spawn-messages") ? plugin.getConfig().getStringList("spawn-messages") : new ArrayList<String>()) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] (.*)");
            Matcher matcher = pattern.matcher(messageString);
            String indicator = "";
            String message = "";
            if (matcher.find()) {
                indicator = matcher.group(1);
                message = matcher.group(2);
            }

            switch (indicator.toLowerCase()) {
                case "broadcast":
                    plugin.getServer().broadcastMessage(color(message));
                    break;

                case "title":
                    List<String> titleStrings = Arrays.asList(message.split("\\|"));
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.sendTitle(color(titleStrings.get(0)), titleStrings.size() > 1 ? color(titleStrings.get(1)) : "", 10, titleStrings.size() > 2 ? Integer.parseInt(titleStrings.get(2).replaceAll("\\s+", "")) : 40, 10);
                    }
                    break;

                case "particle":
                    String finalMessage = message;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> Objects.requireNonNull(Objects.requireNonNull(npcLocation).getWorld()).spawnParticle(Particle.valueOf(finalMessage), npcLocation, 50, .5, 1.5, .5, 0), 5L);
                    break;

                case "sound":
                    Pattern patternSound = Pattern.compile("(\\w+)\\s([\\d.]+)\\s([\\d.]+)");
                    Matcher matchSound = patternSound.matcher(message);
                    if (matchSound.find() && soundExists(matchSound.group(1))) {
                        npcLocation.getWorld().playSound(npcLocation, Sound.valueOf(matchSound.group(1)), Float.parseFloat(matchSound.group(2)), Float.parseFloat(matchSound.group(3)));
                    }
                    break;

                default:
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), message);
                    break;
            }
        }

        npc.spawn(npcLocation);
        spawnedNpcs.add(npc);

        NPC finalNpc = npc;
        getScheduler().runTaskLater(plugin, () -> {
            CitizensCMD.getApi().removeNPCData(finalNpc.getId());
            if (finalNpc.isSpawned()) finalNpc.despawn();
            spawnedNpcs.clear();

            for (String messageString : plugin.getConfig().contains("despawn-messages") ? plugin.getConfig().getStringList("despawn-messages") : new ArrayList<String>()) {
                Pattern pattern = Pattern.compile("\\[([^]]*)] (.*)");
                Matcher matcher = pattern.matcher(messageString);
                String indicator = "";
                String message = "";
                if (matcher.find()) {
                    indicator = matcher.group(1);
                    message = matcher.group(2);
                }

                switch (indicator.toLowerCase()) {
                    case "broadcast":
                        plugin.getServer().broadcastMessage(color(message));
                        break;

                    case "title":
                        List<String> titleStrings = Arrays.asList(message.split("\\|"));
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            player.sendTitle(color(titleStrings.get(0)), titleStrings.size() > 1 ? color(titleStrings.get(1)) : "", 10, titleStrings.size() > 2 ? Integer.parseInt(titleStrings.get(2)) : 40, 10);
                        }
                        break;

                    case "particle":
                        Objects.requireNonNull(Objects.requireNonNull(npcLocation).getWorld()).spawnParticle(Particle.valueOf(message), npcLocation, 50, 1, 2, 1);
                        break;

                    case "sound":
                        Pattern patternSound = Pattern.compile("(\\w+)\\s([\\d.]+)\\s([\\d.]+)");
                        Matcher matchSound = patternSound.matcher(message);
                        if (matchSound.find() && soundExists(matchSound.group(1))) {
                            npcLocation.getWorld().playSound(npcLocation, Sound.valueOf(matchSound.group(1)), Float.parseFloat(matchSound.group(2)), Float.parseFloat(matchSound.group(3)));
                        }
                        break;

                    default:
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), message);
                        break;
                }
            }
        }, plugin.getConfig().getInt("npc-stay-time") * 20L);
    }

    private boolean containsType(String type) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.toString().equalsIgnoreCase(type)) return true;
        }

        return false;
    }

    private static boolean soundExists(String soundName) {
        for (Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(soundName)) return true;
        }

        return false;
    }
}
