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
