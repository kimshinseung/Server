package org.network.data;

import java.awt.*;
import java.io.Serializable;

public class UserMoveData implements Serializable {
    public String username;
    public int seeDirection;//0 = 위, 1 = 오른쪽, 2 = 왼쪽, 3 = 아래
    public Point currentPos;
    public UserMoveData(String userName, int seeDirection, Point point){
        this.username = userName;
        this.seeDirection = seeDirection;
        this.currentPos = point;
    }
}
