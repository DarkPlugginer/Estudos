package me.dark.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.dark.npc.NPC;
import me.dark.others.Skin;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayerListener implements Listener {

    private List<NPC> npcs = new ArrayList<>();
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

    private void add(Player player) {
        ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {

            //PlayIn - Não intercept os de login
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
        Skin skin = new Skin(getInitialUUID(names.get((new Random().nextInt(names.size()) - 1))));
        propertyMap.put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignatur()));

        try {
            setValue(profile, "properties", propertyMap);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        PlayerList list = MinecraftServer.getServer().getPlayerList();
        list.moveToWorld(((CraftPlayer) player).getHandle(), 0, false, Bukkit.getWorld("world").getSpawnLocation(), false);
        player.teleport(Bukkit.getWorld("world").getSpawnLocation().add(0, 1, 0));

        NPC npc = new NPC("pele", "zenozinho", player.getLocation(), Material.DIAMOND_SWORD, false);
        npc.spawn();

        npc.setBoots(Material.IRON_BOOTS);
        npc.setChestplate(Material.IRON_CHESTPLATE);
        npc.setHelmet(Material.IRON_HELMET);
        npc.setLeggings(Material.IRON_LEGGINGS);

        npcs.add(npc);
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        npcs.get(0).teleport(player.getLocation().subtract(1, 0, 1));
    }

    private void setValue(Object instance, String field, Object value) throws Exception {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(instance, value);
    }

    private String getInitialUUID(String name) {
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
