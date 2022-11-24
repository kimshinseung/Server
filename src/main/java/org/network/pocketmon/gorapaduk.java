package org.network.pocketmon;


public class gorapaduk extends PocketMonster {
    public gorapaduk()
    {
        setBackPath("Pocketmon/gorapaduk-back.png");
        setFrontPath("Pocketmon/gorapaduk-front.png");

        Skill s1 = new Skill("마구할퀴기",30);
        Skill s2 = new Skill("물의파동",40);
        Skill s3 = new Skill("염동력",20);
        Skill s4 = new Skill("사슬묶기",40);

        Skill[] temp_skill = new Skill[4];
        temp_skill[0]=s1;
        temp_skill[1]=s2;
        temp_skill[2]=s3;
        temp_skill[3]=s4;
        this.setSkill_list(temp_skill);

        this.setName("고라파턱");
        this.setCurrent_HP(100);
        this.setMax_HP(100);
        //this.setType("grass");
        this.setAtk(10);
        this.setCondition("normal");

    }

}
