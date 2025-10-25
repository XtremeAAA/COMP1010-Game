package mastersofmq.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a playable or enemy character in the game.
 * Each character has:
 * - Basic stats (HP, Stamina, Strength, Defence, Endurance)
 * - Combat state (current HP/Stamina, defending status)
 * - List of usable skills
 * 
 * Key formulas:
 * - Stamina regeneration per turn = max(1, endurance/2)
 * - Basic attack damage = max(1, strength + random(0-5) - target.defence)
 * - Damage reduction while defending = 50%
 * 
 * Access Control:
 * - Public class to allow creation of characters throughout the game
 * - Private fields to protect character state integrity
 * - Public methods for controlled interaction with character stats and abilities
 * - Implements Cloneable for creating copies of character templates
 */
public class CharacterClass implements Cloneable { // Represents a character class (playable or enemy)
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

    public CharacterClass(String name, String type, int hp, int stamina, int strength, int defence, int endurance) { // Constructor to initialize character stats
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


    public String getName() { // Getter for character name
        return name;
    }

    public String getType() { // Getter for character type
        return type;
    }

    public int getCurrentHP() { // Getter for current HP
        return currentHP;
    }

    public int getCurrentStamina() { // Getter for current Stamina
        return currentStamina;
    }

    public int getStrength() { // Getter for strength
        return strength;
    }

    public int getDefence() { // Getter for defence
        return defence;
    }

    public int getEndurance() { // Getter for endurance
        return endurance;
    }
    
    public List<Skill> getSkills() { // Getter for skills
        return skills;
    }

    public boolean isDefending() {
        return defending;
    }

    public void addSkill(Skill s) { // Adds a skill to the character's skill list
        skills.add(s); }

    public void takeDamage(int d) { // Reduces current HP by damage amount, not going below 0
        currentHP = Math.max(0, Math.min(maxHP, currentHP - d));
    }

    public void heal(int amount) { // Heals the character by a specified amount, not exceeding max HP
        currentHP = Math.min(maxHP, currentHP + amount);
    }

    public void restoreStamina(int amount) { // Restores stamina by a specified amount, not exceeding max Stamina
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    public void deductStamina(int amount) { // Deducts stamina by a specified amount, not going below 0
        currentStamina = Math.max(0, currentStamina - amount);
    }

    public void defend() { // Sets the character to defending state
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

    public boolean isAlive() { // Checks if the character is alive (current HP > 0)
        return currentHP > 0;
    }

    public boolean canUseSkill(Skill s) { // Checks if the character can use a given skill
        return currentStamina >= s.getStaminaCost() && !s.isOnCooldown();
    }

    public void useSkill(Skill s, CharacterClass target, Random rng) { // Uses a skill on a target character
        if (!canUseSkill(s)) return;
        currentStamina -= s.getStaminaCost();
        int rand = rng.nextInt(6); // random factor between 0-5
        int dmg = s.getDamage();
        if (dmg > 0) {
            dmg = Math.max(1, dmg + rand - target.getDefence()); // basic damage formula
            if (target.isDefending()) dmg = Math.max(1, dmg/2); // reduce damage if target is defending
            target.takeDamage(dmg); // deal damage to target
            System.out.printf("%s uses %s on %s!\n%s deals %d damage!\n%s takes %d damage! (HP: %d/%d)\n", name, s.getName(), target.getName(), s.getName(), dmg, target.getName(), dmg, target.getCurrentHP(), target.getMaxHP());
        } else {
            int heal = Math.abs(dmg) + rand; // negative damage heals
            target.takeDamage(dmg); // negative damage heals
            System.out.printf("%s uses %s on %s!\n%s restores %d HP!\n%s is healed for %d HP. (HP: %d/%d)\n", name, s.getName(), target.getName(), s.getName(), heal, target.getName(), heal, target.getCurrentHP(), target.getMaxHP());
        }
        s.triggerCooldown();
    }

    public int getMaxHP() { return maxHP; }
    public int getMaxStamina() { return maxStamina; }

    // used when cloning from data
    @Override
    public CharacterClass clone() { // Creates a deep copy of the character class
        CharacterClass c = new CharacterClass(name, type, maxHP, maxStamina, strength, defence, endurance);
        for (Skill s : skills) c.addSkill(s.clone());
        return c;
    }
}
