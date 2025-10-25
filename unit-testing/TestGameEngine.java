import mastersofmq.game.GameEngine;
import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import mastersofmq.model.Team;

import java.util.Random;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for the GameEngine class to verify battle mechanics
 */
public class TestGameEngine {
    private GameEngine engine;
    private Team playerTeam;
    private Team enemyTeam;
    private CharacterClass warrior;
    private CharacterClass mage;
    private CharacterClass darkWarrior;
    private CharacterClass shadowMage;

    @Before
    public void setup() {
        // Create player team
        warrior = new CharacterClass("Warrior", "Fighter", 100, 80, 15, 10, 8);
        mage = new CharacterClass("Mage", "Caster", 70, 120, 6, 5, 15);
        
        // Add skills
        Skill slash = new Skill("slash", "Slash", 20, 15, 1);
        Skill defend = new Skill("defend", "Defend", 0, 10, 1);
        warrior.addSkill(slash);
        warrior.addSkill(defend);
        
        Skill fireball = new Skill("fireball", "Fireball", 25, 20, 2);
        Skill heal = new Skill("heal", "Heal", -15, 20, 2);
        mage.addSkill(fireball);
        mage.addSkill(heal);

        playerTeam = new Team("Hero");
        playerTeam.addCharacter(warrior);
        playerTeam.addCharacter(mage);

        // Create enemy team
        darkWarrior = CharacterClass.createOpponent("Dark Warrior");
        shadowMage = CharacterClass.createOpponent("Shadow Mage");
        enemyTeam = new Team("Shadow Patrol");
        enemyTeam.addCharacter(darkWarrior);
        enemyTeam.addCharacter(shadowMage);

        // Initialize game engine
        engine = new GameEngine(playerTeam);
    }

    @Test
    public void testTeamInitialization() {
        assertNotNull("Player team should be initialized", playerTeam);
        assertNotNull("Enemy team should be initialized", enemyTeam);
        assertEquals("Player team should have 2 characters", 2, playerTeam.getCharacters().size());
        assertEquals("Enemy team should have 2 characters", 2, enemyTeam.getCharacters().size());
    }

    @Test
    public void testCharacterStats() {
        // Test warrior stats
        assertEquals("Warrior should have correct HP", 100, warrior.getMaxHP());
        assertEquals("Warrior should have correct stamina", 80, warrior.getMaxStamina());
        assertEquals("Warrior should have correct strength", 15, warrior.getStrength());
        assertEquals("Warrior should have correct defense", 10, warrior.getDefence());

        // Test mage stats
        assertEquals("Mage should have correct HP", 70, mage.getMaxHP());
        assertEquals("Mage should have correct stamina", 120, mage.getMaxStamina());
        assertEquals("Mage should have correct strength", 6, mage.getStrength());
        assertEquals("Mage should have correct defense", 5, mage.getDefence());
    }

    @Test
    public void testSkillFunctionality() {
        // Test warrior's slash skill
        Skill slash = warrior.getSkills().get(0);
        assertFalse("Slash should not be on cooldown initially", slash.isOnCooldown());
        assertTrue("Warrior should have enough stamina for slash", warrior.canUseSkill(slash));
        
        // Test mage's heal skill
        Skill heal = mage.getSkills().get(1);
        assertFalse("Heal should not be on cooldown initially", heal.isOnCooldown());
        assertTrue("Mage should have enough stamina for heal", mage.canUseSkill(heal));
    }

    @Test
    public void testDamageCalculation() {
        int initialHP = darkWarrior.getCurrentHP();
        warrior.useSkill(warrior.getSkills().get(0), darkWarrior, new Random(42)); // Use fixed seed for reproducibility
        assertTrue("Dark Warrior should take damage", darkWarrior.getCurrentHP() < initialHP);
    }

    @Test
    public void testHealingMechanics() {
        // Damage the warrior first
        warrior.takeDamage(30);
        int damagedHP = warrior.getCurrentHP();
        
        // Test healing
        mage.useSkill(mage.getSkills().get(1), warrior, new Random());
        assertTrue("Warrior should be healed", warrior.getCurrentHP() > damagedHP);
    }

    @Test
    public void testDefenseMechanics() {
        warrior.defend();
        assertTrue("Warrior should be in defensive stance", warrior.isDefending());
        warrior.endTurn();
        assertFalse("Defensive stance should end after turn", warrior.isDefending());
    }

    @Test
    public void testStaminaRegeneration() {
        // Use a skill to consume stamina
        warrior.deductStamina(30);
        int reducedStamina = warrior.getCurrentStamina();
        
        // End turn to trigger regeneration
        warrior.endTurn();
        assertTrue("Stamina should regenerate after turn", warrior.getCurrentStamina() > reducedStamina);
    }

    @Test
    public void testDefeatedState() {
        warrior.takeDamage(warrior.getCurrentHP()); // Deal fatal damage
        mage.takeDamage(mage.getCurrentHP()); // Deal fatal damage to second character
        assertTrue("Team should be marked as defeated when all characters die", playerTeam.isDefeated());
        assertNull("getAnyAlive should return null for defeated team", playerTeam.getAnyAlive());
    }

    @Test
    public void testSkillCooldowns() {
        Skill fireball = mage.getSkills().get(0);
        mage.useSkill(fireball, darkWarrior, new Random());
        assertTrue("Skill should be on cooldown after use", fireball.isOnCooldown());
        assertEquals("Cooldown should be at maximum", fireball.getCooldownMax(), fireball.getCooldownRemaining());
        
        // Test cooldown reduction
        fireball.reduceCooldown();
        assertTrue("Cooldown should decrease", fireball.getCooldownRemaining() < fireball.getCooldownMax());
    }
}