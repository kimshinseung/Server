package org.network.packet;

import java.util.ArrayList;
import java.util.List;

public class UserBattlePacket extends Packet{
    public String userInput;
    public BattlePacketType battlePacketType;
    public List<Integer> args = new ArrayList<>();
    public UserBattlePacket(int id, String username, String userInput,BattlePacketType battlePacketType,List<Integer> args) {
        super(id, PacketType.USER_BATTLE, username);
        this.userInput = userInput;
        this.battlePacketType = battlePacketType;
        this.args = args;
    }
}
