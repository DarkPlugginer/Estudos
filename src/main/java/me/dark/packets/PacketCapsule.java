/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.packets;

public class PacketCapsule {

    private Object packet;

    public static PacketCapsule create(Object packet) {
        PacketCapsule packetCapsule = new PacketCapsule();
        packetCapsule.packet = packet;
        return packetCapsule;
    }

    public Object getPacket() {
        return packet;
    }
}
