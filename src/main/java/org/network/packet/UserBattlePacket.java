package org.network.packet;

import java.util.ArrayList;
import java.util.List;

public class UserBattlePacket extends Packet{
    public String commandType;
    public String target;
    public List<Integer> args = new ArrayList<>();
    public UserBattlePacket(int id, String username, String commandType,String target,List<Integer> args) {
        super(id, PacketType.USER_BATTLE, username);
        this.commandType = commandType;
        this.args = args;
        this.target = target;
    }
}
