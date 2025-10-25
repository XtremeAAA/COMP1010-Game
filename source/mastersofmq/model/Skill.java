package mastersofmq.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a combat ability that characters can use.
 * Features:
 * - Unique identification
 * - Damage/healing values
 * - Resource cost (stamina)
 * - Cooldown system
 * - Prerequisite skill tree system
 * 
 * Key concepts:
 * - Negative damage values represent healing
 * - Skills can't be used while on cooldown
 * - Skills require sufficient stamina AND all prerequisites unlocked
 * - Cooldown decreases by 1 each turn
 * 
 * Access Control:
 * - Public class to allow skill creation across the game
 * - Private fields for immutable skill properties
 * - Public methods for skill usage and prerequisite checking
 * - Implements Cloneable to support skill template copying
 */
public class Skill implements Cloneable {
    private final String id; // Unique identifier for the skill
    private final String name; // Display name of the skill
    private final int damage; // Damage value (negative = healing)
    private final int staminaCost; // Stamina points required to use
    private final int cooldownMax; // Maximum cooldown duration
    private int cooldownRemaining = 0; // Current cooldown counter
    private final List<Skill> prerequisiteSkills = new ArrayList<>(); // Skills required before this one can be used

    public Skill(String id, String name, int damage, int staminaCost, int cooldown) { // Constructor to initialize skill properties
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.staminaCost = staminaCost;
        this.cooldownMax = cooldown;
    }

    public String getId() { return id; } // Getter for skill ID
    public String getName() { return name; } // Getter for skill name
    public int getDamage() { return damage; } // Getter for skill damage
    public int getStaminaCost() { return staminaCost; } // Getter for stamina cost

    public void addPrerequisite(Skill s) { prerequisiteSkills.add(s); } // Adds a prerequisite skill
    public List<Skill> getPrerequisites() { return prerequisiteSkills; } // Getter for prerequisite skills
    
    public boolean checkPrerequisitesRecursively(List<String> unlockedSkills) {
        
        if (unlockedSkills.contains(this.id)) { // Base case: if this skill is already unlocked
            return true;
        }
        
        if (prerequisiteSkills.isEmpty()) { // Base case: if no prerequisites, skill is available
            return true;
        }
        
        for (Skill prereq : prerequisiteSkills) { // Recursive case: check all prerequisites
            if (!prereq.checkPrerequisitesRecursively(unlockedSkills)) { // If any prerequisite is not met
                return false;
            }
        }
        unlockedSkills.add(this.id); // If we get here, all prerequisites are met
        return true;
    }
    
    public int calculateTotalStaminaCostRecursively() {
        if (prerequisiteSkills.isEmpty()) { // Base case: if no prerequisites, return only this skill's cost
            return this.staminaCost;
        }
        
        int totalCost = this.staminaCost;
        for (Skill prereq : prerequisiteSkills) { // Recursive case: sum this skill's cost with all prerequisites
            totalCost += prereq.calculateTotalStaminaCostRecursively();
        }
        return totalCost;
    }
    
    public int getPrerequisiteDepthRecursively() {
        if (prerequisiteSkills.isEmpty()) { // Base case: if no prerequisites, depth is 0
            return 0;
        }
        
        int maxDepth = 0;
        for (Skill prereq : prerequisiteSkills) { // Recursive case: find max depth among prerequisites and add 1
            int depth = prereq.getPrerequisiteDepthRecursively(); // Get depth of this prerequisite
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        return maxDepth + 1;
    }

    public boolean isOnCooldown() { // Checks if the skill is currently on cooldown
        return cooldownRemaining > 0;
    }

    public int getCooldownRemaining() { // Gets the remaining cooldown
        return cooldownRemaining;
    }

    public int getCooldownMax() { // Gets the maximum cooldown
        return cooldownMax;
    }

    public void triggerCooldown() { // Sets the cooldown to maximum
        cooldownRemaining = cooldownMax;
    }

    public void reduceCooldown() { // Decreases the cooldown by 1 if it's greater than 0
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    @Override
    public Skill clone() {
        Skill s = new Skill(id, name, damage, staminaCost, cooldownMax); // Create a new Skill with the same basic properties

        for (Skill p : prerequisiteSkills) { // Clone and add each prerequisite skill to the new Skill
            s.addPrerequisite(p.clone());
        }

        return s; // Return the fully cloned Skill
    }
}
