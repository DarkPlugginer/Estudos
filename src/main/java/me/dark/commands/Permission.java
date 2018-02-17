/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.commands;

import org.bukkit.command.CommandSender;

public enum Permission {

    NONE("");

    private String node;

    Permission(String node) {
        this.node = node;
    }

    private static String getPermission(Permission permission) {
        return ".estudos" + permission.getNode();
    }

    public static Boolean has(Permission permission, CommandSender target) {
        return target.hasPermission(getPermission(permission));
    }

    public String getNode() {
        return node;
    }
}
