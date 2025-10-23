package mastersofmq.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple character class with stats, stamina, and skill list.
 */
public class CharacterClass implements Cloneable {
    private final String name;
    private final String type;
    private final int maxHP;
    private final int maxStamina;
    private int currentHP;
    private int currentStamina;
    private final int strength;
    private final int defence;
    private final int endurance;
    private final List<Skill> skills = new ArrayList<>();
    private boolean defending = false;

    public CharacterClass(String name, String type, int hp, int stamina, int strength, int defence, int endurance) {
        this.name = name;
        this.type = type;
        this.maxHP = hp;
        this.maxStamina = stamina;
        this.currentHP = hp;
        this.currentStamina = stamina;
        this.strength = strength;
        this.defence = defence;
        this.endurance = endurance;
    }

    // Constructor for opponents
    public static CharacterClass createOpponent(String name) {
        switch(name) {
            case "Dark Warrior":
                return new CharacterClass(name, "Fighter", 120, 90, 18, 12, 10);
            case "Shadow Mage":
                return new CharacterClass(name, "Caster", 85, 100, 8, 8, 12);
            case "Corrupted Rogue":
                return new CharacterClass(name, "Assassin", 95, 95, 16, 6, 14);
            case "Dark Priest":
                return new CharacterClass(name, "Healer", 75, 85, 9, 10, 16);
            case "Boss Overlord":
                return new CharacterClass(name, "Fighter", 200, 150, 25, 20, 20);
            default:
                throw new IllegalArgumentException("Unknown opponent: " + name);
        }
    }

    // getters
    public String getName() { return name; }
    public String getType() { return type; }
    public int getCurrentHP() { return currentHP; }
    public int getCurrentStamina() { return currentStamina; }
    public int getStrength() { return strength; }
    public int getDefence() { return defence; }
    public int getEndurance() { return endurance; }
    public List<Skill> getSkills() { return skills; }
    public boolean isDefending() { return defending; }

    public void addSkill(Skill s) { skills.add(s); }

    public void takeDamage(int d) {
        currentHP = Math.max(0, Math.min(maxHP, currentHP - d));
    }

    public void heal(int amount) {
        currentHP = Math.min(maxHP, currentHP + amount);
    }

    public void restoreStamina(int amount) {
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    public void deductStamina(int amount) {
        currentStamina = Math.max(0, currentStamina - amount);
    }

    public void defend() {
        this.defending = true;
    }

    public void endTurn() {
        // regen stamina based on endurance (simple flat rule)
        int regen = Math.max(1, endurance/2);
        currentStamina = Math.min(maxStamina, currentStamina + regen);
        // reduce skill cooldowns
        for (Skill s : skills) s.reduceCooldown();
        // stop defending at end of round
        defending = false;
    }

    public boolean isAlive() { return currentHP > 0; }

    public boolean canUseSkill(Skill s) {
        return currentStamina >= s.getStaminaCost() && !s.isOnCooldown();
    }

    public void useSkill(Skill s, CharacterClass target, Random rng) {
        if (!canUseSkill(s)) return;
        currentStamina -= s.getStaminaCost();
        int rand = rng.nextInt(6);
        int dmg = s.getDamage();
        if (dmg > 0) {
            dmg = Math.max(1, dmg + rand - target.getDefence());
            if (target.isDefending()) dmg = Math.max(1, dmg/2);
            target.takeDamage(dmg);
            System.out.printf("%s uses %s on %s!\n%s deals %d damage!\n%s takes %d damage! (HP: %d/%d)\n", name, s.getName(), target.getName(), s.getName(), dmg, target.getName(), dmg, target.getCurrentHP(), target.getMaxHP());
        } else {
            int heal = Math.abs(dmg) + rand;
            target.takeDamage(dmg); // negative damage heals
            System.out.printf("%s uses %s on %s!\n%s restores %d HP!\n%s is healed for %d HP. (HP: %d/%d)\n", name, s.getName(), target.getName(), s.getName(), heal, target.getName(), heal, target.getCurrentHP(), target.getMaxHP());
        }
        s.triggerCooldown();
    }

    public int getMaxHP() { return maxHP; }
    public int getMaxStamina() { return maxStamina; }

    // used when cloning from data
    @Override
    public CharacterClass clone() {
        CharacterClass c = new CharacterClass(name, type, maxHP, maxStamina, strength, defence, endurance);
        for (Skill s : skills) c.addSkill(s.clone());
        return c;
    }
}
