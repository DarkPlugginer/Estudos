package me.dark.listener;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.dark.npc.NPC;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    List<NPC> npcs = new ArrayList<>();

    public void add(Player player) {
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

        NPC npc = new NPC("pele", "zenozinho", player.getLocation(), Material.DIAMOND_SWORD, false);
        npc.spawn();

        npc.setBoots(Material.IRON_BOOTS);
        npc.setChestplate(Material.IRON_CHESTPLATE);
        npc.setHelmet(Material.IRON_HELMET);
        npc.setLeggings(Material.IRON_LEGGINGS);

        npcs.add(npc);
    }

    private void setValue(Object instance, String field, Object value) throws Exception {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(instance, value);
    }

    private int toFixedPoint(double d) {
        return (int) (d * 32.0);
    }

    private byte toPackedByte(float f) {
        return (byte) ((int) (f * 256.0F / 360.0F));
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        PacketPlayOutAnimation outAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(outAnimation);

        npcs.get(0).teleport(player.getLocation());
    }
}
