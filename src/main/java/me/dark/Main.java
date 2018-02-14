package me.dark;

import me.dark.commands.Model;
import me.dark.commands.common.BaseCommandExecutor;
import me.dark.commands.common.CommandManager;
import me.dark.listener.PlayerListener;
import me.dark.packets.Login;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main extends JavaPlugin {

    public static Main getMain() {
        return getPlugin(Main.class);
    }

    public static String hash(String str) {
        try {
            byte[] digest = digest(str, "SHA-1");
            return new BigInteger(digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] digest(String str, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        return md.digest(strBytes);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        ((CraftServer) getServer()).getCommandMap().register("estudos", new BaseCommandExecutor("estudos"));
        CommandManager.register(Model.class);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        new Login().enable();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void setValue(Object instance, String field, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
