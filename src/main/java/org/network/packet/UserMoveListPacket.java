package org.network.packet;

import org.network.data.UserMoveData;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class UserMoveListPacket extends Packet{
    public List<UserMoveData> userMoveList;
    public UserMoveListPacket(int id, String username,List<UserMoveData> userMoveList) {
        super(id, PacketType.USER_MOVE_LIST_DATA, username);
        this.userMoveList = userMoveList;
    }
}
