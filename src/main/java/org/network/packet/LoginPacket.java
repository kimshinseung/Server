package org.network.packet;

public class LoginPacket extends Packet{
    public String password;
    public LoginPacketType loginPacketType;
    public LoginPacket(int id,LoginPacketType loginPacketType, String username, String password) {
        super(id,PacketType.USER_ACCOUNT, username);
        this.password = password;
        this.loginPacketType = loginPacketType;
    }
}
