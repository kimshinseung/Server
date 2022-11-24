package org.network.data;

import org.network.pocketmon.*;

import java.util.HashMap;
import java.util.Map;

public class PocketMonData {
    public static final Map<Integer, PocketMonster> monsterInfo = new HashMap<>();
    public static void InitPocketmonData(){
        //Json을 사용하기에는 시간이 부족하여 이곳에 작성
        monsterInfo.put(0,new firi());
        monsterInfo.put(1,new ggobugi());
        monsterInfo.put(2,new ggomadol());
        monsterInfo.put(3,new gorapaduk());
        monsterInfo.put(4,new isanghessi());
        monsterInfo.put(5,new pikachu());
    }
}
