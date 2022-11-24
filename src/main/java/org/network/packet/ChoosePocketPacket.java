package org.network.packet;

import java.util.ArrayList;
import java.util.List;

public class ChoosePocketPacket extends Packet{
    public List<Integer> pocketMonList= new ArrayList<>();
    public ChoosePocketPacket(int id, String username,List<Integer>pocketMonList) {
        super(id, PacketType.CHOOSE_POCKET,username);
        this.pocketMonList=pocketMonList;
    }
}
