package org.network.packet;

public enum PacketType {
    USER_DEFAULT(0),
    USER_ACCOUNT(100),
    USER_CHAT(200),
    USER_BATTLE(300),
    USER_MOVE(500),
    USER_LIST_DATA(600),
    SERVER_DEFAULT(1000);
    private final int environments;

    PacketType(int environments){
        this.environments = environments;
    }
    public int toInt(){
        return environments;
    }
}