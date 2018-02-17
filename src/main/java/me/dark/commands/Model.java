/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.commands;

import me.dark.commands.common.BaseCommand;
import org.bukkit.command.CommandSender;

public class Model {

    @BaseCommand(aliases = "m", desc = "testa o comando", usage = "model [a]", permission = Permission.NONE)
    public void onTesteCmd(CommandSender sender, String commandLabel, String[] strings) {
        sender.sendMessage("Funcionou kaceta");
    }
}
