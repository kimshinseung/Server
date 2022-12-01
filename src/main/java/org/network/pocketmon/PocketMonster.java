package org.network.pocketmon;

public class PocketMonster implements Fight {
    private String name;//포켓몬 이름 ex)피카츄, 파이리
    private String condition;//현재상태

    private String frontPath;
    private String backPath;
    private int current_HP;
    private int max_HP;
    private int atk;//공격력
    private Skill[] skill_list;//해당 포켓몬이 사용할 수 있는 스킬(4가지)

    //생성자
    public PocketMonster() {

        skill_list = new Skill[4];
    }

    public PocketMonster(PocketMonster pocketMonster) {
        this.name = pocketMonster.name;
        this.condition = pocketMonster.condition;
        this.frontPath = pocketMonster.frontPath;
        this.backPath = pocketMonster.backPath;
        this.current_HP = pocketMonster.current_HP;
        this.max_HP = pocketMonster.max_HP;
        this.atk = pocketMonster.atk;
        this.skill_list = pocketMonster.skill_list;
    }

    public String getFrontPath() {
        return frontPath;
    }

    public void setFrontPath(String frontPath) {
        this.frontPath = frontPath;
    }

    public String getBackPath() {
        return backPath;
    }

    public void setBackPath(String backPath) {
        this.backPath = backPath;
    }
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getCurrent_HP() {
        return current_HP;
    }

    public void setCurrent_HP(int current_HP) {
        this.current_HP = current_HP;
    }

    public void changeCurrent_HP(int num) {
        this.current_HP += num;
        if(this.current_HP < 0) {
            this.current_HP = 0;
        }
        else if (this.current_HP > this.max_HP) {
            this.current_HP = this.max_HP;
        }
    }

    public int getMax_HP() {
        return max_HP;
    }

    public void setMax_HP(int max_HP) {
        this.max_HP = max_HP;
    }

    public void changeMax_HP(int num) {
        this.max_HP += num;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void changeAtk(int num) {
        this.atk += num;
    }



    public Skill[] getSkill_list() {
        return skill_list;
    }

    public void setSkill_list(Skill[] skill_list) {
        this.skill_list = skill_list;
    }
    @Override //num : 사용할 스킬 넘버, p : 공격당하는 포켓몬
    public String use_skill(int num, PocketMonster p) { //몇번 스킬을 누구에게 사용하기 메소드
        System.out.println("--------- 스킬 사용 --------");
        Skill temp = skill_list[num];
        int dam = temp.getPower() * this.getAtk();
        String str_base = this.getName()+"의 "+temp.getName()+" 공격!";
        String str_normal = "효과가 있었다!";
        String str_good = "효과는 굉장했다!";
        String str_bad = "효과가 모자른 것 같다.";
//        if(temp.getType().equals("water"))
//        {
//            if(p.getType().equals("fire"))
//            {
//                dam*=2;
//                str_base += str_good;
//            }
//            else
//                str_base +=str_normal;
//        }
//        else if(temp.getType().equals("electric"))
//        {
//            if(p.getType().equals("water"))
//            {
//                dam*=2;
//                str_base += str_good;
//            }
//            else if(p.getType().equals("grass"))
//            {
//                dam/=2;
//                str_base += str_bad;
//            }
//            else
//                str_base +=str_normal;
//        }
//        else if(temp.getType().equals("grass"))
//        {
//            if(p.getType().equals("water"))
//            {
//                dam*=2;
//                str_base += str_good;
//            }
//            else if(p.getType().equals("fire"))
//            {
//                dam/=2;
//                str_base += str_bad;
//            }
//            else
//                str_base +=str_normal;
//        }
//        else if(temp.getType().equals("fire"))
//        {
//            if(p.getType().equals("grass"))
//            {
//                dam*=2;
//                str_base += str_good;
//            }
//            else if(p.getType().equals("water"))
//            {
//                dam/=2;
//                str_base += str_bad;
//            }
//            else
//                str_base +=str_normal;
//        }
        //dam -= p.getDef();//실제 받는 데미지 = dam - p의 def
        dam/=10;
        p.changeCurrent_HP(-dam);

        return str_base;
    }
    public PocketMonster getHardCopy(){
        return new PocketMonster(this);
    }
    public void is_dead()
    {
        if(this.getCurrent_HP()==0)
        {
            this.setCondition("DEAD");
        }
    }

}
