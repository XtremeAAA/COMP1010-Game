import mastersofmq.game.GameEngine;
import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import mastersofmq.model.Team;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Random;

/**
 * Unit tests for complex battle scenarios
 */
public class TestBattleScenarios {
    private Team heroTeam;
    private Team enemyTeam;
    private CharacterClass warrior;
    private CharacterClass mage;
    private Random rng;

    @Before
    public void setup() {
        rng = new Random(42); // Fixed seed for reproducible tests
        
        // Create hero team with standard composition
        heroTeam = new Team("Heroes");
        warrior = new CharacterClass("Warrior", "Fighter", 100, 80, 15, 10, 8);
        mage = new CharacterClass("Mage", "Caster", 70, 120, 6, 5, 15);
        
        // Add basic skills
        warrior.addSkill(new Skill("slash", "Slash", 20, 15, 1));
        warrior.addSkill(new Skill("defend", "Defend", 0, 10, 1));
        mage.addSkill(new Skill("fireball", "Fireball", 25, 20, 2));
        mage.addSkill(new Skill("heal", "Heal", -15, 20, 2));
        
        heroTeam.addCharacter(warrior);
        heroTeam.addCharacter(mage);

        // Create enemy team
        enemyTeam = new Team("Dark Forces");
        CharacterClass darkWarrior = new CharacterClass("Dark Warrior", "Fighter", 120, 90, 12, 8, 6);
        CharacterClass shadowMage = new CharacterClass("Shadow Mage", "Caster", 80, 100, 8, 4, 12);
        
        darkWarrior.addSkill(new Skill("dark_slash", "Dark Slash", 25, 20, 2));
        shadowMage.addSkill(new Skill("shadow_bolt", "Shadow Bolt", 30, 25, 2));
        
        enemyTeam.addCharacter(darkWarrior);
        enemyTeam.addCharacter(shadowMage);
    }

    @Test
    public void testFullBattleScenario() {
        // Simulate several rounds of combat
        CharacterClass darkWarrior = enemyTeam.getCharacters().get(0);
        CharacterClass shadowMage = enemyTeam.getCharacters().get(1);

        // Round 1: Warrior attacks Dark Warrior
        int initialDarkWarriorHP = darkWarrior.getCurrentHP();
        warrior.useSkill(warrior.getSkills().get(0), darkWarrior, rng);
        assertTrue("Dark Warrior should take damage", darkWarrior.getCurrentHP() < initialDarkWarriorHP);

        // Enemy counterattack
        int initialWarriorHP = warrior.getCurrentHP();
        darkWarrior.useSkill(darkWarrior.getSkills().get(0), warrior, rng);
        assertTrue("Warrior should take damage from counterattack", warrior.getCurrentHP() < initialWarriorHP);

        // Mage heals Warrior
        int damagedWarriorHP = warrior.getCurrentHP();
        mage.useSkill(mage.getSkills().get(1), warrior, rng);
        assertTrue("Warrior should be healed by Mage", warrior.getCurrentHP() > damagedWarriorHP);

        // End of round effects
        heroTeam.endOfRound();
        enemyTeam.endOfRound();
        
        // Verify stamina regeneration
        assertTrue("Characters should regenerate stamina", warrior.getCurrentStamina() > 0);
        assertTrue("Characters should regenerate stamina", mage.getCurrentStamina() > 0);
    }

    @Test
    public void testDefensiveStrategy() {
        CharacterClass darkWarrior = enemyTeam.getCharacters().get(0);
        
        // Warrior defends
        warrior.defend();
        assertTrue("Warrior should be in defensive stance", warrior.isDefending());
        
        // Enemy attacks defending warrior
        int defendingWarriorHP = warrior.getCurrentHP();
        darkWarrior.useSkill(darkWarrior.getSkills().get(0), warrior, rng);
        
        // Damage should be reduced while defending
        int damageTaken = defendingWarriorHP - warrior.getCurrentHP();
        assertTrue("Damage should be reduced while defending", damageTaken < 20);
    }

    @Test
    public void testTeamSynergy() {
        // Test warrior and mage synergy
        CharacterClass darkWarrior = enemyTeam.getCharacters().get(0);
        
        // Warrior attacks first
        warrior.useSkill(warrior.getSkills().get(0), darkWarrior, rng);
        int afterWarriorHP = darkWarrior.getCurrentHP();
        
        // Mage follows up with fireball
        mage.useSkill(mage.getSkills().get(0), darkWarrior, rng);
        int afterMageHP = darkWarrior.getCurrentHP();
        
        assertTrue("Combined attacks should deal significant damage", 
                  afterMageHP < afterWarriorHP);
    }

    @Test
    public void testResourceManagement() {
        // Test stamina management over multiple actions
        int initialStamina = warrior.getCurrentStamina();
        
        // Use multiple skills
        Skill slash = warrior.getSkills().get(0);
        CharacterClass darkWarrior = enemyTeam.getCharacters().get(0);
        
        // First use
        warrior.useSkill(slash, darkWarrior, rng);
        int afterFirstUseStamina = warrior.getCurrentStamina();
        assertTrue("Stamina should decrease after skill use", 
                  afterFirstUseStamina < initialStamina);
        
        // Second use (if we have enough stamina)
        if (warrior.canUseSkill(slash)) {
            warrior.useSkill(slash, darkWarrior, rng);
            int afterSecondUseStamina = warrior.getCurrentStamina();
            assertTrue("Stamina should decrease further", 
                      afterSecondUseStamina < afterFirstUseStamina);
        }
        
        // Verify cooldown
        assertTrue("Skill should be on cooldown", slash.isOnCooldown());
    }

    @Test
    public void testStatusEffects() {
        // Test defending status
        warrior.defend();
        assertTrue("Defend status should be active", warrior.isDefending());
        
        // Status should clear at end of turn
        warrior.endTurn();
        assertFalse("Defend status should clear after turn", warrior.isDefending());
    }

    @Test
    public void testBattleEndConditions() {
        // Kill enemy team
        CharacterClass darkWarrior = enemyTeam.getCharacters().get(0);
        CharacterClass shadowMage = enemyTeam.getCharacters().get(1);
        
        darkWarrior.takeDamage(darkWarrior.getCurrentHP());
        shadowMage.takeDamage(shadowMage.getCurrentHP());
        
        assertTrue("Enemy team should be defeated when all members are dead", 
                  enemyTeam.isDefeated());
        assertNull("No alive characters should be found", 
                  enemyTeam.getAnyAlive());
    }
}