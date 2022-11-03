package org.network.packet;

public class UserChatPacket extends Packet{
    public String chat;
    public String target;
    public UserChatPacket(int id, String username,String chat, String target) {
        super(id, PacketType.USER_CHAT, username);
        this.target = target;
        this.chat = chat;
    }
}
