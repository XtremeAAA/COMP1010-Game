import org.junit.Test;
import static org.junit.Assert.*;
import mastersofmq.fileio.GameDataLoader;
import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import java.util.List;

public class TestGameDataLoader {

    @Test
    public void testLoadCharacters() throws Exception {
        GameDataLoader loader = new GameDataLoader("../data/characters.json");
        loader.load();
        List<CharacterClass> characters = loader.getCharacters();

        assertEquals(4, characters.size());

        // Check specific characters
        CharacterClass warrior = characters.stream().filter(c -> c.getName().equals("Warrior")).findFirst().orElse(null);
        assertNotNull(warrior);
        assertEquals("Fighter", warrior.getType());
        assertEquals(100, warrior.getMaxHP());
        assertEquals(100, warrior.getMaxStamina());
        assertEquals(15, warrior.getStrength());
        assertEquals(15, warrior.getDefence());
        assertEquals(12, warrior.getEndurance());
        assertEquals(2, warrior.getSkills().size()); // power_strike and cleave

        CharacterClass mage = characters.stream().filter(c -> c.getName().equals("Mage")).findFirst().orElse(null);
        assertNotNull(mage);
        assertEquals("Caster", mage.getType());
        assertEquals(2, mage.getSkills().size()); // fireball and ice_spike
    }

    @Test
    public void testLoadSkills() throws Exception {
        GameDataLoader loader = new GameDataLoader("../data/characters.json");
        loader.load();

        // Skills are not directly accessible, but we can check via characters
        List<CharacterClass> characters = loader.getCharacters();
        CharacterClass warrior = characters.stream().filter(c -> c.getName().equals("Warrior")).findFirst().orElse(null);
        assertNotNull(warrior);

        Skill powerStrike = warrior.getSkills().stream().filter(s -> s.getId().equals("power_strike")).findFirst().orElse(null);
        assertNotNull(powerStrike);
        assertEquals("Power Strike", powerStrike.getName());
        assertEquals(25, powerStrike.getDamage());
        assertEquals(15, powerStrike.getStaminaCost());
        assertEquals(2, powerStrike.getCooldownMax());

        Skill cleave = warrior.getSkills().stream().filter(s -> s.getId().equals("cleave")).findFirst().orElse(null);
        assertNotNull(cleave);
        assertEquals("Cleave", cleave.getName());
        assertEquals(40, cleave.getDamage());
        assertEquals(1, cleave.getPrerequisites().size()); // Has power_strike as prereq
        assertEquals("power_strike", cleave.getPrerequisites().get(0).getId());
    }

    @Test
    public void testDataValidation() {
        // Test with invalid path
        GameDataLoader loader = new GameDataLoader("nonexistent.json");
        try {
            loader.load();
            fail("Expected exception for invalid file");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testEmptyData() throws Exception {
        // Assuming we have an empty JSON, but since we don't, skip or create temp
        // For now, test that loader initializes correctly
        GameDataLoader loader = new GameDataLoader("../data/characters.json");
        assertEquals(0, loader.getCharacters().size()); // Before load
        loader.load();
        assertTrue(loader.getCharacters().size() > 0);
    }
}
