/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.commands.common;

import me.dark.Main;
import me.dark.commands.Permission;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class BaseCommandExecutor extends BukkitCommand {

    private String name;

    public BaseCommandExecutor(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CommandManager.getExtra() + "__________________.[ " + CommandManager.getHighlight() + Main.getMain().getName() + CommandManager.getExtra() + " ].__________________");
            sender.sendMessage(CommandManager.getDark() + "Description: " + CommandManager.getLight() + Main.getMain().getDescription().getDescription());
            sender.sendMessage(CommandManager.getDark() + "Author: " + CommandManager.getLight() + Main.getMain().getDescription().getAuthors().get(0));
            sender.sendMessage(CommandManager.getDark() + "Version: " + CommandManager.getLight() + Main.getMain().getDescription().getVersion());
            sender.sendMessage(CommandManager.getDark() + "Website: " + CommandManager.getLight() + Main.getMain().getDescription().getWebsite());
            sender.sendMessage(CommandManager.getExtra() + "---------------------------------------------------");
            return true;
        }

        if (CommandManager.getCommand(args[0]) == null) {
            sender.sendMessage(CommandManager.getError() + "The specified command was not found!");
            return true;
        }

        BaseCommand command = CommandManager.getCommand(args[0]);
        Object[] commandArgs = ArrayUtils.remove(args, 0);

        if (sender instanceof Player && !(command.player())) {
            sender.sendMessage(CommandManager.getError() + "This command cannot be ran as a player!");
            return true;
        }

        if (sender instanceof ConsoleCommandSender && !(command.console())) {
            sender.sendMessage(CommandManager.getError() + "This command cannot be ran from the console!");
            return true;
        }

        if (command.permission() != null && !(command.permission().equals(Permission.NONE)) && !(Permission.has(command.permission(), sender))) {
            sender.sendMessage(CommandManager.getError() + "You do not have permission for this command!");
            return true;
        }

        if ((commandArgs.length < command.min()) || (commandArgs.length > command.max() && command.max() != -1)) {
            sender.sendMessage(CommandManager.getError() + "Usage: /" + commandLabel + " " + command.aliases()[0] + " " + command.usage());
            return true;
        }

        CommandManager.execute(command, sender, commandLabel, commandArgs);

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }
}
