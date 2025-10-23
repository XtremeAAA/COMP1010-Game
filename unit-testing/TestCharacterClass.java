import org.junit.Test;
import static org.junit.Assert.*;
import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import java.util.Random;

public class TestCharacterClass {

    @Test
    public void testCharacterCreation() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12);
        assertEquals("TestChar", character.getName());
        assertEquals("Fighter", character.getType());
        assertEquals(100, character.getCurrentHP());
        assertEquals(100, character.getMaxHP());
        assertEquals(100, character.getCurrentStamina());
        assertEquals(100, character.getMaxStamina());
        assertEquals(15, character.getStrength());
        assertEquals(10, character.getDefence());
        assertEquals(12, character.getEndurance());
        assertTrue(character.isAlive());
        assertFalse(character.isDefending());
    }

    @Test
    public void testTakeDamage() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12);
        character.takeDamage(30);
        assertEquals(70, character.getCurrentHP());
        character.takeDamage(80);
        assertEquals(0, character.getCurrentHP());
        assertFalse(character.isAlive());
    }

    @Test
    public void testDeductStamina() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12);
        character.deductStamina(20);
        assertEquals(80, character.getCurrentStamina());
        character.deductStamina(100);
        assertEquals(0, character.getCurrentStamina());
    }

    @Test
    public void testDefend() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12);
        assertFalse(character.isDefending());
        character.defend();
        assertTrue(character.isDefending());
        character.endTurn(); // Should reset defending
        assertFalse(character.isDefending());
    }

    @Test
    public void testEndTurn() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 50, 15, 10, 12);
        character.deductStamina(20); // Stamina 30
        character.defend(); // Set defending
        character.endTurn();
        // Regen: max(1, 12/2) = 6, so 30 + 6 = 36
        assertEquals(36, character.getCurrentStamina());
        assertFalse(character.isDefending());
    }

    @Test
    public void testAddSkill() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12);
        Skill skill = new Skill("skill_id", "Test Skill", 10, 5, 3);
        character.addSkill(skill);
        assertEquals(1, character.getSkills().size());
        assertEquals(skill, character.getSkills().get(0));
    }

    @Test
    public void testCanUseSkill() {
        CharacterClass character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12);
        Skill skill = new Skill("skill_id", "Test Skill", 10, 5, 3);
        character.addSkill(skill);

        assertTrue(character.canUseSkill(skill));

        character.deductStamina(100); // No stamina
        assertFalse(character.canUseSkill(skill));

        character = new CharacterClass("TestChar", "Fighter", 100, 100, 15, 10, 12); // Reset
        character.addSkill(skill);
        skill.triggerCooldown();
        assertFalse(character.canUseSkill(skill));
    }

    @Test
    public void testUseSkill() {
        CharacterClass attacker = new CharacterClass("Attacker", "Fighter", 100, 100, 15, 10, 12);
        CharacterClass target = new CharacterClass("Target", "Fighter", 100, 100, 10, 10, 10);
        Skill skill = new Skill("skill_id", "Test Skill", 20, 10, 2);
        attacker.addSkill(skill);

        Random rng = new Random(0); // Fixed seed for predictable roll
        attacker.useSkill(skill, target, rng);

        // Damage: 20 + roll (assume roll 0-5, say 3) - defence 10 = 13
        // But since random, check stamina deducted and cooldown triggered
        assertEquals(90, attacker.getCurrentStamina()); // 100 - 10
        assertTrue(skill.isOnCooldown());
        assertEquals(2, skill.getCooldownRemaining());
        // Target HP reduced
        assertTrue(target.getCurrentHP() < 100);
    }

    @Test
    public void testClone() {
        CharacterClass original = new CharacterClass("Original", "Fighter", 100, 100, 15, 10, 12);
        Skill skill = new Skill("skill_id", "Test Skill", 10, 5, 3);
        original.addSkill(skill);

        CharacterClass cloned = original.clone();

        assertNotSame(original, cloned);
        assertEquals(original.getName(), cloned.getName());
        assertEquals(original.getCurrentHP(), cloned.getCurrentHP());
        assertEquals(1, cloned.getSkills().size());
        assertNotSame(skill, cloned.getSkills().get(0));
        assertEquals(skill.getId(), cloned.getSkills().get(0).getId());
    }
}
