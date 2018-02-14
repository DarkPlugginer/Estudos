package me.dark.packets;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import me.dark.Main;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.io.Charsets;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

public class Login {

    TinyProtocol protocol;
    private Reflection.FieldAccessor<GameProfile> gameProfile = Reflection.getField("{nms}.PacketLoginInStart", GameProfile.class, 0);

    public void enable() {

        protocol = new TinyProtocol(Main.getMain()) {
            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (packet instanceof PacketLoginInStart) {
                    if (gameProfile.hasField(packet)) {
                        System.out.println("jose");
                        new BypassLogin(channel);
                        packet = null;
                    }
                }

                return super.onPacketInAsync(sender, channel, packet);
            }
        };

       /* PacketEvent packetEvent = new PacketEvent() {

            @Override
            public PacketSend onSend(PacketSend packetSend) {
                return packetSend;
            }

            @Override
            public PacketReceive onReceive(PacketReceive packetReceive) {
                if (packetReceive.getPacket().equals("PacketLoginInStart")) {
                    if (gameProfile.hasField(packetReceive.getPacket())) {
                        try {
                            if (recieveLogin(packetReceive)) {
                                // Se o jogador for pirata
                                packetReceive.setCancelled(true);
                            } else {
                                // Se o jogador for original
                            }
                            Thread.sleep(500L);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return packetReceive;
            }
        };*/

    }

   /* private boolean recieveLogin(Channel packet) {
        ReturnType type = MajorMojangAPI.checkNicknamePremiumStatusAsync(gameProfile.get(packet).getName());
        if (type == ReturnType.ORIGINAL) {
            // Se o player for original
            return false;
        } else if (type == ReturnType.PIRATA) {
            // Se o player for pirata
            System.out.println("Pirata");

            return true;
        } else if (type == ReturnType.INVALID_NICKNAME) {
            disconnect("Seu nome de usuario é invalido!", networkList(packet.getMajorChannel().remoteAddress()));
        } else if (type == ReturnType.TIMEOUT) {
            disconnect("Você demorou muito para logar!", networkList(packet.getMajorChannel().remoteAddress()));
        } else {
            disconnect("Estamos passando por muitas requisições no momento\n\nPor favor aguarde!",
                    networkList(packet.getMajorChannel().remoteAddress()));
        }
        return true;
    } */

    public void disconnect(String s, NetworkManager nm) {
        try {
            ChatComponentText exception = new ChatComponentText(s);
            nm.handle(new PacketLoginOutDisconnect(exception));
            nm.close(exception);
        } catch (Exception arg2) {
        }

    }

    @SuppressWarnings("unchecked")
    private NetworkManager networkList(SocketAddress socketAddress) {
        try {
            for (NetworkManager next : (List<NetworkManager>) ClassReflection.getField("h",
                    MinecraftServer.getServer().aq(), 0)) {
                if (next.getSocketAddress().equals(socketAddress)) {
                    return next;
                }
            }
        } catch (Exception e1) {

        }
        return null;
    }

    private class BypassLogin extends LoginListener {

        private BypassLogin(Channel packet) {
            super(MinecraftServer.getServer(), networkList(packet.remoteAddress()));
            try {
                ClassReflection.setField("m", this, this.networkManager, 0);
                ClassReflection.setField("i", gameProfile.get(packet), this, 1);
            } catch (Exception e1) {
            }
        }

        @Override
        public void b() {
            c();
        }

        @Override
        public void c() {
            try {
                GameProfile validProfile = (GameProfile) ClassReflection.getField("i", this, 1);
                validProfile = new GameProfile(
                        UUID.nameUUIDFromBytes(("OfflinePlayer:" + validProfile.getName()).getBytes(Charsets.UTF_8)),
                        validProfile.getName());
                EntityPlayer attemptLogin = new EntityPlayer(MinecraftServer.getServer(),
                        MinecraftServer.getServer().getWorldServer(0), validProfile,
                        new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0)));

                // Attempt login not null
                this.networkManager.handle(new PacketLoginOutSuccess(validProfile));
                MinecraftServer.getServer().getPlayerList().a(this.networkManager, attemptLogin);
                int h = (int) ClassReflection.getField("h", this, 1);
                if (h++ == 600) {
                    this.disconnect("Took too long to log in");
                }
            } catch (Exception e1) {

            }
        }
    }
}
