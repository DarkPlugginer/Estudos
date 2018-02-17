/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.dark.others.Skin;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayerListener implements Listener {

    private List<String> names = Arrays.asList("darkplugginer",
            "pele",
            "xuxa",
            "joao",
            "pedro",
            "maria",
            "Preens",
            "menstruatinq",
            "diepeep",
            "creeper",
            "naruto",
            "sasuke",
            "orochimaru",
            "saitama",
            "genos",
            "izayoi");

    /**
     * @param player - jogador a ser adicionado
     */
    private void add(Player player) {
        ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {

            //PlayIn - Não intercepa os de login
            @Override
            public void channelRead(ChannelHandlerContext context, Object object) throws Exception {
                super.channelRead(context, object);
            }

            //PlayOut
            @Override
            public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) throws Exception {
                super.write(context, object, promise);
            }
        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), duplexHandler);
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        add(player);

        GameProfile profile = ((CraftPlayer) player).getProfile();

        PropertyMap propertyMap = new PropertyMap();
        Skin skin = new Skin(getUuid(names.get((new Random().nextInt(names.size()) - 1))));
        propertyMap.put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignatur()));

        try {
            setValue(profile, "properties", propertyMap);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        PlayerList list = MinecraftServer.getServer().getPlayerList();
        list.moveToWorld(((CraftPlayer) player).getHandle(), 0, false, Bukkit.getWorld("world").getSpawnLocation(), false);
        player.teleport(Bukkit.getWorld("world").getSpawnLocation().add(0, 1, 0));
    }

    private void setValue(Object instance, String field, Object value) throws Exception {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(instance, value);
    }

    private String getUuid(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JSONParser jp = new JSONParser();
            JSONObject json = (JSONObject) jp.parse(new InputStreamReader(request.getInputStream()));
            return json.get("id").toString();
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
        return "";
    }
}
