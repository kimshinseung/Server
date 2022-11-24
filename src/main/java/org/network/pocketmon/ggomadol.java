package org.network.pocketmon;

public class ggomadol extends PocketMonster {
    public ggomadol()
    {
        setBackPath("Pocketmon/ggomadol-back.png");
        setFrontPath("Pocketmon/ggomadol-front.png");

        Skill s1 = new Skill("몸통박치기",30);
        Skill s2 = new Skill("흙놀이",20);
        Skill s3 = new Skill("돌떨구기",50);
        Skill s4 = new Skill("지진",40);

        Skill[] temp_skill = new Skill[4];
        temp_skill[0]=s1;
        temp_skill[1]=s2;
        temp_skill[2]=s3;
        temp_skill[3]=s4;
        this.setSkill_list(temp_skill);

        this.setName("꼬마돌");
        this.setCurrent_HP(100);
        this.setMax_HP(100);
        //this.setType("grass");
        this.setCondition("normal");

    }

}
