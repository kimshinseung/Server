package org.network.packet;

public enum LoginPacketType {
    SIGN_IN(0),
    LOGIN(1),
    LOGOUT(2),
    LOGIN_ACCEPT(100),
    LOGIN_REFUSE(200),
    SIGN_IN_ACCEPT(300),
    SIGN_IN_REFUSE(400);
    private final int environments;

    LoginPacketType(int environments){
        this.environments = environments;
    }
    public int toInt(){
        return environments;
    }
}
