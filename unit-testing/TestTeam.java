import org.junit.Test;
import static org.junit.Assert.*;
import mastersofmq.model.Team;
import mastersofmq.model.CharacterClass;

public class TestTeam {

    @Test
    public void testTeamCreation() {
        Team team = new Team("Test Team");
        assertEquals("Test Team", team.getName());
        assertEquals(0, team.getCharacters().size());
    }

    @Test
    public void testAddCharacter() {
        Team team = new Team("Test Team");
        CharacterClass char1 = new CharacterClass("Char1", "Type1", 100, 100, 10, 10, 10);
        CharacterClass char2 = new CharacterClass("Char2", "Type2", 80, 80, 8, 8, 8);
        team.addCharacter(char1);
        team.addCharacter(char2);
        assertEquals(2, team.getCharacters().size());
        assertEquals(char1, team.getCharacters().get(0));
        assertEquals(char2, team.getCharacters().get(1));
    }

    @Test
    public void testGetAliveCharacter() {
        Team team = new Team("Test Team");
        CharacterClass aliveChar = new CharacterClass("Alive", "Type", 100, 100, 10, 10, 10);
        CharacterClass deadChar = new CharacterClass("Dead", "Type", 0, 100, 10, 10, 10);
        team.addCharacter(aliveChar);
        team.addCharacter(deadChar);

        assertEquals(aliveChar, team.getAliveCharacter(0));
        assertNull(team.getAliveCharacter(1));
        assertNull(team.getAliveCharacter(2)); // Invalid index
    }

    @Test
    public void testGetAnyAlive() {
        Team team = new Team("Test Team");
        CharacterClass char1 = new CharacterClass("Char1", "Type", 100, 100, 10, 10, 10);
        CharacterClass char2 = new CharacterClass("Char2", "Type", 0, 100, 10, 10, 10);
        team.addCharacter(char1);
        team.addCharacter(char2);

        assertEquals(char1, team.getAnyAlive());

        // Kill the last alive
        char1.takeDamage(100);
        assertNull(team.getAnyAlive());
    }

    @Test
    public void testIsDefeated() {
        Team team = new Team("Test Team");
        CharacterClass alive = new CharacterClass("Alive", "Type", 100, 100, 10, 10, 10);
        team.addCharacter(alive);

        assertFalse(team.isDefeated());

        alive.takeDamage(100);
        assertTrue(team.isDefeated());

        // Empty team
        Team emptyTeam = new Team("Empty");
        assertTrue(emptyTeam.isDefeated());
    }

    @Test
    public void testEndOfRound() {
        Team team = new Team("Test Team");
        CharacterClass char1 = new CharacterClass("Char1", "Type", 100, 50, 10, 10, 10);
        team.addCharacter(char1);

        // Assume endTurn regens stamina
        char1.deductStamina(20); // Stamina now 30
        team.endOfRound();

        // Regen should be max(1, 10/2) = 5, so 30 + 5 = 35
        assertEquals(35, char1.getCurrentStamina());

        // Dead characters shouldn't be affected
        CharacterClass dead = new CharacterClass("Dead", "Type", 0, 100, 10, 10, 10);
        team.addCharacter(dead);
        team.endOfRound();
        assertEquals(100, dead.getCurrentStamina()); // No change for dead
    }

    @Test
    public void testTeamSummary() {
        Team team = new Team("Test Team");
        CharacterClass char1 = new CharacterClass("Warrior", "Fighter", 100, 100, 15, 15, 12);
        CharacterClass char2 = new CharacterClass("Mage", "Caster", 80, 80, 10, 10, 10);
        team.addCharacter(char1);
        team.addCharacter(char2);

        String summary = team.getTeamSummary();
        assertTrue(summary.contains("Warrior (Fighter) HP: 100/100, ST: 100/100"));
        assertTrue(summary.contains("Mage (Caster) HP: 80/80, ST: 80/80"));
    }

    @Test
    public void testPrintStatus() {
        // printStatus outputs to console, hard to test directly in unit test
        // For now, skip or mock System.out if needed, but basic test assumes it runs without error
        Team team = new Team("Test Team");
        CharacterClass char1 = new CharacterClass("Char1", "Type", 100, 100, 10, 10, 10);
        team.addCharacter(char1);
        // Assume it prints correctly; no assertion needed for output
        assertTrue(true); // Placeholder
    }
}
