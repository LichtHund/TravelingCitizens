package me.mattstudios.travelingcitizens.utility;

import me.mattstudios.travelingcitizens.TravelingCitizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Utils {

    /**
     * Utility to use color codes easily
     *
     * @param msg The message String
     * @return returns the string with color
     */
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Simplified way for sending console messages
     *
     * @param msg the message to be sent to the console
     */
    public static void info(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(color(msg));
    }

    public static boolean shouldSpawn(int chance) {
        return (new Random().nextInt(100) + 1) <= chance;
    }

    public static String locationToString(Location loc) {
        if (loc == null) {
            return null;
        }
        return Objects.requireNonNull(loc.getWorld()).getName() +
                ':' +
                precision(loc.getX(), 4) +
                ':' +
                precision(loc.getY(), 4) +
                ':' +
                precision(loc.getZ(), 4) +
                ':' +
                precision(loc.getPitch(), 4) +
                ':' +
                precision(loc.getYaw(), 4);
    }

    private static double precision(double x, int p) {
        double pow = Math.pow(10, p);
        return Math.round(x * pow) / pow;
    }

    public static Location stringToLocation(String storedLoc) {
        if (storedLoc == null) {
            return null;
        }
        String[] args = ACFPatterns.COLON.split(storedLoc);
        if (args.length >= 4 || (args.length == 3)) {
            String world = args[0];
            int i = args.length == 3 ? 0 : 1;
            double x = Double.parseDouble(args[i]);
            double y = Double.parseDouble(args[i + 1]);
            double z = Double.parseDouble(args[i + 2]);
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            if (args.length >= 6) {
                loc.setPitch(Float.parseFloat(args[4]));
                loc.setYaw(Float.parseFloat(args[5]));
            }
            return loc;
        } else if (args.length == 2) {
            String[] args2 = ACFPatterns.COMMA.split(args[1]);
            if (args2.length == 3) {
                String world = args[0];
                double x = Double.parseDouble(args2[0]);
                double y = Double.parseDouble(args2[1]);
                double z = Double.parseDouble(args2[2]);
                return new Location(Bukkit.getWorld(world), x, y, z);
            }
        }
        return null;
    }

    public static void setSkin(NPC npc, String textureData, String textureSignature) {
        try {
            npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, textureData);
            npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, textureSignature);
            npc.data().set("cached-skin-uuid-name", "null");
            npc.data().set("player-skin-name", "null");
            npc.data().set("cached-skin-uuid", UUID.randomUUID().toString());
            npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
            if (npc instanceof SkinnableEntity) {
                ((SkinnableEntity) npc).getSkinTracker().notifySkinChange(true);
            }
            if (npc.getEntity() instanceof SkinnableEntity) {
                ((SkinnableEntity) npc.getEntity()).getSkinTracker().notifySkinChange(true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static List<String> setToList(Set<String> set) {
        return new ArrayList<>(set);
    }

    public static void checkSpawnedNPC(TravelingCitizens plugin) {
        if (!plugin.getConfig().contains("npc-id")) return;

        int id = plugin.getConfig().getInt("npc-id");
        NPC npc = CitizensAPI.getNPCRegistry().getById(id);
        if (npc == null) return;
        if (npc.isSpawned()) {
            npc.despawn();
        }
    }

}
