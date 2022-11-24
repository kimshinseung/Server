package org.network.pocketmon;

public class firi extends PocketMonster{
    public firi()
    {
        setBackPath("Pocketmon/firi-back.png");
        setFrontPath("Pocketmon/firi-front.png");

        Skill s1 = new Skill("몸통박치기",30);
        Skill s2 = new Skill("화염방사",50);
        Skill s3 = new Skill("깨물기",40);
        Skill s4 = new Skill("불대문자",50);
        Skill[] temp_skill = new Skill[4];
        temp_skill[0]=s1;
        temp_skill[1]=s2;
        temp_skill[2]=s3;
        temp_skill[3]=s4;
        this.setSkill_list(temp_skill);

        this.setName("파이리");
        this.setCurrent_HP(100);
        this.setMax_HP(100);
        //this.setType("fire");
        this.setAtk(10);

        this.setCondition("normal");


    }

}
