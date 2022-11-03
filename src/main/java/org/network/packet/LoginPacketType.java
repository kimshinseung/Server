package org.network.packet;

public enum LoginPacketType {
    SIGN_IN(0),
    LOGIN(1),
    LOGOUT(2);
    private final int environments;

    LoginPacketType(int environments){
        this.environments = environments;
    }
    public int toInt(){
        return environments;
    }
}
