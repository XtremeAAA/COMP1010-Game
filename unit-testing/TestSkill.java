import org.junit.Test;
import static org.junit.Assert.*;
import mastersofmq.model.Skill;

public class TestSkill {

    @Test
    public void testSkillCreation() {
        Skill skill = new Skill("test_id", "Test Skill", 10, 5, 3);
        assertEquals("test_id", skill.getId());
        assertEquals("Test Skill", skill.getName());
        assertEquals(10, skill.getDamage());
        assertEquals(5, skill.getStaminaCost());
        assertEquals(3, skill.getCooldownMax());
        assertEquals(0, skill.getCooldownRemaining());
        assertFalse(skill.isOnCooldown());
    }

    @Test
    public void testAddPrerequisite() {
        Skill skill = new Skill("test_id", "Test Skill", 10, 5, 3);
        Skill prereq = new Skill("prereq_id", "Prerequisite", 5, 2, 1);
        skill.addPrerequisite(prereq);
        assertEquals(1, skill.getPrerequisites().size());
        assertEquals(prereq, skill.getPrerequisites().get(0));
    }

    @Test
    public void testCooldown() {
        Skill skill = new Skill("test_id", "Test Skill", 10, 5, 3);
        assertFalse(skill.isOnCooldown());
        skill.triggerCooldown();
        assertTrue(skill.isOnCooldown());
        assertEquals(3, skill.getCooldownRemaining());
        skill.reduceCooldown();
        assertEquals(2, skill.getCooldownRemaining());
        skill.reduceCooldown();
        skill.reduceCooldown();
        assertEquals(0, skill.getCooldownRemaining());
        assertFalse(skill.isOnCooldown());
    }

    @Test
    public void testClone() {
        Skill original = new Skill("test_id", "Test Skill", 10, 5, 3);
        Skill prereq = new Skill("prereq_id", "Prerequisite", 5, 2, 1);
        original.addPrerequisite(prereq);

        Skill cloned = original.clone();

        // Check basic properties
        assertEquals(original.getId(), cloned.getId());
        assertEquals(original.getName(), cloned.getName());
        assertEquals(original.getDamage(), cloned.getDamage());
        assertEquals(original.getStaminaCost(), cloned.getStaminaCost());
        assertEquals(original.getCooldownMax(), cloned.getCooldownMax());

        // Check prerequisites are cloned (not same reference)
        assertNotSame(original, cloned);
        assertEquals(1, cloned.getPrerequisites().size());
        Skill clonedPrereq = cloned.getPrerequisites().get(0);
        assertNotSame(prereq, clonedPrereq);
        assertEquals(prereq.getId(), clonedPrereq.getId());
    }

    @Test
    public void testRecursiveClone() {
        Skill root = new Skill("root", "Root Skill", 10, 5, 3);
        Skill mid = new Skill("mid", "Mid Skill", 8, 4, 2);
        Skill leaf = new Skill("leaf", "Leaf Skill", 6, 3, 1);
        mid.addPrerequisite(leaf);
        root.addPrerequisite(mid);

        Skill clonedRoot = root.clone();

        // Verify structure
        assertEquals(1, clonedRoot.getPrerequisites().size());
        Skill clonedMid = clonedRoot.getPrerequisites().get(0);
        assertNotSame(mid, clonedMid);
        assertEquals(1, clonedMid.getPrerequisites().size());
        Skill clonedLeaf = clonedMid.getPrerequisites().get(0);
        assertNotSame(leaf, clonedLeaf);
        assertEquals("leaf", clonedLeaf.getId());
    }
}
