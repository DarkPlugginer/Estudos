/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import me.dark.Main;
import me.dark.others.CheckPremium;
import me.dark.packets.reflection.ClassReflection;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.io.Charsets;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Login {

    private Reflection.FieldAccessor<GameProfile> gameProfile = Reflection.getField("{nms}.PacketLoginInStart", GameProfile.class, 0);

    public void enable() {

        new TinyProtocol(Main.getMain()) {
            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (packet instanceof PacketLoginInStart) {
                    if (gameProfile.hasField(packet)) {
                        GameProfile profile = gameProfile.get(packet);

                        if (!(new CheckPremium(profile.getName()).getResult())) {
                            System.out.println("Jogador pirata interceptado!");
                            System.out.println("Nome: " + profile.getName());

                            new BypassLogin(channel, profile);
                            return null;
                        } else {
                            if (!(loginCheck(profile.getName(), profile.getId().toString()))) {
                                disconnect("Ocorreu um erro, por favor, tente novamente.", networkList(channel.localAddress()));
                            }
                        }
                    }
                }

                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    private void disconnect(String s, NetworkManager nm) {
        try {
            ChatComponentText exception = new ChatComponentText(s);
            nm.handle(new PacketLoginOutDisconnect(exception));
            nm.close(exception);
        } catch (Exception arg2) {
            System.err.println(arg2.getMessage());
        }

    }

    private String fetchUUID(String username) {
        try {

            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            InputStream stream = url.openStream();
            InputStreamReader inr = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(inr);

            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            String result = sb.toString();

            JsonElement element = new JsonParser().parse(result);
            JsonObject obj = element.getAsJsonObject();

            String uuid = obj.get("id").toString();

            uuid = uuid.substring(1);
            return uuid.substring(0, uuid.length() - 1);


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    private boolean loginCheck(String name, String uuid) {
        return (Objects.equals(fetchUUID(name), uuid)) || (Objects.equals(fetchUUID(name), uuid));
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
            System.err.println(e1.getMessage());
        }
        return null;
    }

    private class BypassLogin extends LoginListener {

        private BypassLogin(Channel packet, GameProfile profile) {
            super(MinecraftServer.getServer(), networkList(packet.remoteAddress()));
            try {
                ClassReflection.setField("m", this, this.networkManager, 0);
                ClassReflection.setField("i", profile, this, 1);
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
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
                validProfile = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + validProfile.getName()).getBytes(Charsets.UTF_8)), validProfile.getName());


                EntityPlayer attemptLogin = new EntityPlayer(MinecraftServer.getServer(), MinecraftServer.getServer().getWorldServer(0), validProfile, new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0)));

                this.networkManager.handle(new PacketLoginOutSuccess(validProfile));
                MinecraftServer.getServer().getPlayerList().a(this.networkManager, attemptLogin);

                int h = (int) ClassReflection.getField("h", this, 1);
                h++;
                if (h == 600) {
                    this.disconnect("Took too long to log in");
                }
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
            }
        }
    }
}
