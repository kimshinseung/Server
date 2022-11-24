package org.network.pocketmon;

public class isanghessi extends PocketMonster {
    public isanghessi()
    {
        setBackPath("Pocketmon/isanghessi-back.png");
        setFrontPath("Pocketmon/isanghessi-front.png");

        Skill s1 = new Skill("몸통박치기",30);
        Skill s2 = new Skill("넝쿨채찍",40);
        Skill s3 = new Skill("잎날가르기",40);
        Skill s4 = new Skill("수면가루",0);

        Skill[] temp_skill = new Skill[4];
        temp_skill[0]=s1;
        temp_skill[1]=s2;
        temp_skill[2]=s3;
        temp_skill[3]=s4;
        this.setSkill_list(temp_skill);

        this.setName("이상해씨");
        this.setCurrent_HP(100);
        this.setMax_HP(100);
        //this.setType("grass");
        this.setAtk(10);
        this.setCondition("normal");

    }

}
