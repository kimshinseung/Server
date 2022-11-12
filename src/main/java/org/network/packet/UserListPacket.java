package org.network.packet;

import java.util.List;

public class UserListPacket extends Packet{

    public List<String> userlist;
    public UserListPacket(int id, String username,List<String> userlist) {
        super(id, PacketType.USER_LIST_DATA, username);
        this.userlist = userlist;
    }
}
