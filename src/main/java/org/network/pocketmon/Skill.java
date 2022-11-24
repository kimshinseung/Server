package org.network.pocketmon;
public class Skill {
    private String name;
    //private String type;
    private int power;
    //생성자 깃 연습
    public Skill() {}
    public Skill(String name) {
        this.name = name;
    }
    public Skill(String name, int power) {
        this(name);
        this.power = power;
    }
//    public Skill(String name, int power, String type) {
//        this(name,power);
//        this.type = type;
//    }

    //getter, setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
//    public String getType() {
//        return type;
//    }
//    public void setType(String type) {
//        this.type = type;
//    }
    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }
}

