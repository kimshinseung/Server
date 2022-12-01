package org.network.data;

public class BattlePocketData {
    public int pocketId;
    private int maxHealth;

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    private int currentHealth;
    public boolean isDead;
    public void giveDamage(int damage){
        currentHealth-=damage;
        if (currentHealth<=0){
            currentHealth = 0;
            isDead = true;
        }
    }
    public void giveHeal(int heal){
        currentHealth += heal;
        if (currentHealth > maxHealth){
            currentHealth = maxHealth;
        }
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
}
