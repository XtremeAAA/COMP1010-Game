package mastersofmq.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Team contains an ArrayList of CharacterClass (composition).
 */
public class Team {
    private final String name;
    private final List<CharacterClass> characters = new ArrayList<>();

    public Team(String name) { this.name = name; }
    public String getName() { return name; }
    public void addCharacter(CharacterClass c) { characters.add(c); }

    public CharacterClass getAliveCharacter(int idx) {
        if (idx < 0 || idx >= characters.size()) return null;
        CharacterClass c = characters.get(idx);
        return c.isAlive() ? c : null;
    }

    public CharacterClass getAnyAlive() {
        for (CharacterClass c : characters) if (c.isAlive()) return c;
        return null;
    }

    public boolean isDefeated() {
        for (CharacterClass c : characters) if (c.isAlive()) return false;
        return true;
    }

    public List<CharacterClass> getCharacters() {
        return characters;
    }

    public void endOfRound() {
        for (CharacterClass c : characters) if (c.isAlive()) c.endTurn();
    }

    public String getTeamSummary() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < characters.size(); i++) {
            CharacterClass c = characters.get(i);
            sb.append(c.getName()).append(" (").append(c.getType()).append(") HP: ").append(c.getCurrentHP()).append("/").append(c.getMaxHP()).append(", ST: ").append(c.getCurrentStamina()).append("/").append(c.getMaxStamina());
            if (i < characters.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public void printStatus() {
        System.out.println(name + ":");
        for (CharacterClass c : characters) {
            String status = c.isAlive() ? "" : " [DEFEATED]";
            System.out.printf(" - %s: HP:%d/%d, ST:%d/%d%s\n", c.getName(), c.getCurrentHP(), c.getMaxHP(), c.getCurrentStamina(), c.getMaxStamina(), status);
        }
    }
}
