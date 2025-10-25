package mastersofmq.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a team of characters in the game.
 * Uses composition to contain an ArrayList of CharacterClass instances.
 * Manages team-wide operations like checking defeat conditions and end-of-round effects.
 * 
 * Access Control:
 * - Public class as teams need to be instantiated by GameEngine and other classes
 * - Private fields to ensure data encapsulation
 * - Public methods for controlled access to team functionality
 */
public class Team {
    private final String name;
    private final List<CharacterClass> characters = new ArrayList<>(); // List of characters in the team
    public Team(String name) { this.name = name; } // Constructor to set team name
    public String getName() { return name; } // Getter for team name
    public void addCharacter(CharacterClass c) { characters.add(c); } // Adds a character to the team
    public CharacterClass getAliveCharacter(int idx) { // Retrieves a living character by index
        if (idx < 0 || idx >= characters.size()) return null; // Error handeling for invalid index
        CharacterClass c = characters.get(idx); // Get character at index
        return c.isAlive() ? c : null; // Return character if alive, else null
    }

    public CharacterClass getAnyAlive() {
        for (CharacterClass c : characters) {
            if (c.isAlive()) {
                return c; // Return the first alive character
            }
        }
        return null; // No alive characters found
    }

    public boolean isDefeated() {
        for (CharacterClass c : characters) {
            if (c.isAlive()) {
                return false; // Returns false if found a living character, not defeated
            }
        }
        return true; // No living characters found, team is defeated
    }

    public List<CharacterClass> getCharacters() {
        return characters;
    }

    public void endOfRound() {
        for (CharacterClass c : characters) {
            if (c.isAlive()) {
                c.endTurn(); // Execute end-of-turn logic for this character
            }
        }
    }

    public String getTeamSummary() {
        StringBuilder sb = new StringBuilder(); // Start building the team summary

        for (int i = 0; i < characters.size(); i++) {
            CharacterClass c = characters.get(i);
            sb.append("\n");
            // Append character details: Name, Type, HP, Stamina
            sb.append(c.getName()) // Name
            .append(" (").append(c.getType()).append(")") // Type
            .append(" HP: ").append(c.getCurrentHP()).append("/").append(c.getMaxHP()) // HP
            .append(", ST: ").append(c.getCurrentStamina()).append("/").append(c.getMaxStamina()); // Stamina

            // Add newline for separation
            if (i < characters.size()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public void printStatus() {
        System.out.println(name + ":"); // Print team or player name

        for (CharacterClass c : characters) {
            String status = c.isAlive() ? "" : " [DEFEATED]"; // Determine if character is defeated

            System.out.printf( // Print character stats
                " - %s: HP:%d/%d, ST:%d/%d%s%n", 
                c.getName(), // Name
                c.getCurrentHP(), // Current HP
                c.getMaxHP(), // Max HP
                c.getCurrentStamina(), // Current Stamina
                c.getMaxStamina(), // Max Stamina
                status
            );
        }
    }
}
