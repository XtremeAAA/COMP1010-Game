package mastersofmq.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill with recursive prerequisites.
 * Minimal cooldown implementation.
 */
public class Skill implements Cloneable {
    private final String id;
    private final String name;
    private final int damage;
    private final int staminaCost;
    private final int cooldownMax;
    private int cooldownRemaining = 0;
    private final List<Skill> prerequisiteSkills = new ArrayList<>();

    public Skill(String id, String name, int damage, int staminaCost, int cooldown) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.staminaCost = staminaCost;
        this.cooldownMax = cooldown;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getStaminaCost() { return staminaCost; }

    public void addPrerequisite(Skill s) { prerequisiteSkills.add(s); }
    public List<Skill> getPrerequisites() { return prerequisiteSkills; }
    
    /**
     * Recursively checks if all prerequisites are unlocked.
     * A skill is unlocked if either:
     * 1. It has no prerequisites
     * 2. All its prerequisites are unlocked
     * @param unlockedSkills List of currently unlocked skill IDs
     * @return true if all prerequisites are met
     */
    public boolean checkPrerequisitesRecursively(List<String> unlockedSkills) {
        // Base case: if this skill is already unlocked
        if (unlockedSkills.contains(this.id)) {
            return true;
        }
        
        // Base case: if no prerequisites, skill is available
        if (prerequisiteSkills.isEmpty()) {
            return true;
        }
        
        // Recursive case: check all prerequisites
        for (Skill prereq : prerequisiteSkills) {
            if (!prereq.checkPrerequisitesRecursively(unlockedSkills)) {
                return false;
            }
        }
        
        // If we get here, all prerequisites are met
        unlockedSkills.add(this.id);
        return true;
    }
    
    /**
     * Recursively calculate the total stamina cost including all prerequisites
     * @return Total stamina cost of this skill and all its prerequisites
     */
    public int calculateTotalStaminaCostRecursively() {
        // Base case: if no prerequisites, return only this skill's cost
        if (prerequisiteSkills.isEmpty()) {
            return this.staminaCost;
        }
        
        // Recursive case: sum this skill's cost with all prerequisites
        int totalCost = this.staminaCost;
        for (Skill prereq : prerequisiteSkills) {
            totalCost += prereq.calculateTotalStaminaCostRecursively();
        }
        return totalCost;
    }
    
    /**
     * Recursively find the maximum prerequisite chain depth
     * @return The depth of the prerequisite tree
     */
    public int getPrerequisiteDepthRecursively() {
        // Base case: if no prerequisites, depth is 0
        if (prerequisiteSkills.isEmpty()) {
            return 0;
        }
        
        // Recursive case: find max depth among prerequisites and add 1
        int maxDepth = 0;
        for (Skill prereq : prerequisiteSkills) {
            int depth = prereq.getPrerequisiteDepthRecursively();
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        return maxDepth + 1;
    }

    public boolean isOnCooldown() { return cooldownRemaining > 0; }
    public int getCooldownRemaining() { return cooldownRemaining; }
    public int getCooldownMax() { return cooldownMax; }
    public void triggerCooldown() { cooldownRemaining = cooldownMax; }
    public void reduceCooldown() { if (cooldownRemaining>0) cooldownRemaining--; }

    @Override
    public Skill clone() {
        Skill s = new Skill(id, name, damage, staminaCost, cooldownMax);
        for (Skill p : prerequisiteSkills) s.addPrerequisite(p.clone());
        return s;
    }
}
