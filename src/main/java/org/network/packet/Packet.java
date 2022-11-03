package org.network.packet;

import java.io.Serializable;

public abstract class Packet implements Serializable {
    public int id;
    public PacketType packetType;
    public String username;
    public Packet(int id,PacketType packetType, String username){
        this.id = id;
        this.packetType = packetType;
        this.username = username;
    }
}