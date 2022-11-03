package org.network.packet;

public enum BattlePacketType {
    REQUEST_BATTLE(1),
    ACCEPT_BATTLE(2),
    REJECT_BATTLE(3),
    GIVE_DAMAGE(4),
    USE_HEAL_ITEM(11);
    private final int environments;

    BattlePacketType(int environments){
        this.environments = environments;
    }
    public int toInt(){
        return environments;
    }
}
