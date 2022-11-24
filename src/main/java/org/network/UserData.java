package org.network;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserData {
    public int id;
    public String userName;
    public String password;
    public String state = "Default";
    public List<Integer> pocketMonList = new ArrayList<>();
    public int seeDirection = 0;//0 = 위, 1 = 오른쪽, 2 = 왼쪽, 3 = 아래
    public Point currentPos = new Point(WindowConfig.WIDTH/3,WindowConfig.HEIGHT/2);//캐릭터 생성 초기 위치
}
