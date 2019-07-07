package me.mattstudios.travelingcitizens.commands.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandBase {
    private String name;
    private String permission;

    private boolean allowConsole;

    private List<String> aliases;

    private int minimumArguments;
    private int maximumArguments;

    protected CommandBase(String name, String permission, boolean allowConsole,
                          String[] aliases, int minimumArguments, int maximumArguments) {
        this.name = name;
        this.permission = permission;

        this.allowConsole = allowConsole;

        this.aliases = aliases == null ? new ArrayList<>() : Arrays.asList(aliases);

        this.minimumArguments = minimumArguments;
        this.maximumArguments = maximumArguments;
    }

    public void execute(CommandSender sender, String[] args) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void execute(Player sender, String[] args) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public boolean allowConsole() {
        return allowConsole;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public int getMinimumArguments() {
        return minimumArguments;
    }

    public int getMaximumArguments() {
        return maximumArguments;
    }
}
