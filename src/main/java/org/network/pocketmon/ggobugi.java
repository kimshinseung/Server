package org.network.pocketmon;

public class ggobugi extends PocketMonster {
    public ggobugi()
    {
        setBackPath("Pocketmon/ggobugi-back.png");
        setFrontPath("Pocketmon/ggobugi-front.png");

        Skill s1 = new Skill("몸통박치기",30);
        Skill s2 = new Skill("물대포",40);
        Skill s3 = new Skill("깨물기",40);
        Skill s4 = new Skill("파도타기",50);

        //해당 포켓몬 기본 스킬 배열 : temp_list[]
        Skill[] temp_skill = new Skill[4];
        temp_skill[0]=s1;
        temp_skill[1]=s2;
        temp_skill[2]=s3;
        temp_skill[3]=s4;
        this.setSkill_list(temp_skill);

        this.setName("꼬부기");
        this.setCurrent_HP(100);
        this.setMax_HP(100);
        //this.setType("water");
        this.setAtk(10);
        this.setCondition("normal");

    }


}
