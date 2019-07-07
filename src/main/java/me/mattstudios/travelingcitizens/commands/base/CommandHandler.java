package me.mattstudios.travelingcitizens.commands.base;

import me.mattstudios.travelingcitizens.TravelingCitizens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter, IHandler {

    private TravelingCitizens plugin;
    private List<CommandBase> commands;

    public CommandHandler(TravelingCitizens plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        commands = new ArrayList<>();
    }

    @Override
    public void disable() {
        commands.clear();
        commands = null;
    }

    public void register(CommandBase command) {
        commands.add(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("tc")) {
            return true;
        }

        if (args.length == 0 || args[0].isEmpty()) {
            if (sender.hasPermission("citizenscmd.npcmd"))
                if (sender instanceof Player)
                    getCommand("help").execute((Player) sender, args);
            return true;
        }

        for (CommandBase command : commands) {
            if (!command.getName().equalsIgnoreCase(args[0]) && !command.getAliases()
                    .contains(args[0].toLowerCase())) {
                continue;
            }

            if (!command.allowConsole() && !(sender instanceof Player)) {
                //sender.sendMessage(color(HEADER));
                //sender.sendMessage(plugin.getLang().getMessage(Path.CONSOLE_NOT_ALLOWED));
                return true;
            }

            if (!sender.hasPermission(command.getPermission())) {
                //sender.sendMessage(color(HEADER));
                //sender.sendMessage(plugin.getLang().getMessage(Path.NO_PERMISSION));
                return true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);

            if ((command.getMinimumArguments() != -1 && command.getMinimumArguments() > args.length)
                    || (command.getMaximumArguments() != -1
                    && command.getMaximumArguments() < args.length)) {
                //sender.sendMessage(color(HEADER));
                //sender.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
                return true;
            }

            if (command.allowConsole()) {
                if (sender instanceof Player)
                    command.execute((Player) sender, args);
                else
                    command.execute(sender, args);
                return true;
            } else {
                command.execute((Player) sender, args);
                return true;
            }
        }
        //sender.sendMessage(color(HEADER));
        //sender.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
        return true;
    }

    private CommandBase getCommand(String name) {
        return commands.stream().filter(
                command -> command.getName() != null && command.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public List<CommandBase> getCommands() {
        return commands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel,
                                      String[] args) {
        if (cmd.getName().equalsIgnoreCase("tc")) {
            if (args.length == 1) {
                List<String> commandNames = new ArrayList<>();

                if (!args[0].equals("")) {
                    for (String commandName : commands.stream().map(CommandBase::getName)
                            .collect(Collectors.toList())) {
                        if (!commandName.startsWith(args[0].toLowerCase())) continue;
                        commandNames.add(commandName);
                    }
                } else {
                    commandNames =
                            commands.stream().map(CommandBase::getName)
                                    .collect(Collectors.toList());
                }

                Collections.sort(commandNames);

                return commandNames;

            }
        }

        return null;
    }

}
