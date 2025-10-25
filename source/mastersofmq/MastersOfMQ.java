package mastersofmq;

import mastersofmq.game.GameEngine;
import mastersofmq.fileio.GameDataLoader;
import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import mastersofmq.model.Team;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Masters of MQ game.
 * 
 * Game Flow:
 * 1. Load character/skill data from JSON
 * 2. Player selects team members
 * 3. Start story narrative
 * 4. Progress through 3 battles:
 *    - Library Battle (2 enemies)
 *    - Courtyard Battle (2 tougher enemies)
 *    - Final Boss Battle
 * 
 * Features:
 * - Console-based UI
 * - Turn-based combat
 * - Team composition strategy
 * - Progressive difficulty
 * - Narrative elements
 * 
 * Access Control:
 * - Public class as it contains the main entry point
 * - Private helper methods for team selection and UI
 * - Static methods as this is a utility class
 * - Public main method for program entry
 */





/*
 * Main class for Masters of MQ game.
 */
public class MastersOfMQ {
    public static void main(String[] args) {
        System.out.println("Masters of MQ - Starting game...");

        /*
            * builds a path to data/characters.json, creates a GameDataLoader to read that file,
            * and tries to load the game data. If loading fails, it prints an error message and stops the program.
        */
        String dataPath = Paths.get("data", "characters.json").toString(); // Creates a platform-independent path to characters.json
        GameDataLoader loader = new GameDataLoader(dataPath); // Initializes a loader to read game data from the JSON file
        try {
            loader.load(); // Attempts to load the game data
        } catch (Exception e) { // Catches any exceptions during loading
            System.err.println("Failed to load game data: " + e.getMessage());
            return;
        }
        /*
         * After successfully loading the game data, the program retrieves the list of character templates from the loader.
         * It then initializes a Scanner to read user input from the console.
         */
        List<CharacterClass> characters = loader.getCharacters();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Masters of MQ ===");
        System.out.println("Characters loaded successfully!\n");

        // Ask for team name (max 20 characters)
        System.out.print("Enter your team's name (max 20 characters): ");
        String teamName = sc.nextLine().trim();
        while (teamName.isEmpty() || teamName.length() > 20) { // Validate team name length
            if (teamName.isEmpty()) { // Error handling for empty team name
                System.out.print("Team name cannot be empty. Please enter a name: ");
            } else { // Error handling for too long team name
                System.out.printf("Team name too long (max 20 chars). You entered %d chars. Try again: ", teamName.length());
            }
            teamName = sc.nextLine().trim();
        }

        Team playerTeam = selectTeam(teamName, characters, sc);

        // Display team summary
        System.out.println("\n=== " + playerTeam.getName() + "'s Team ===");
        System.out.println(playerTeam.getTeamSummary());
        System.out.println("================\n");

        GameEngine engine = new GameEngine(playerTeam); // Initializes the game engine with the player's team
        engine.startNarrative(); // Starts the game's narrative
    }


    /*
     * Prompts the player to select their team from available characters.
     * @param playerName The name of the player's team (chosen by the player)
     * @param characters List of available character templates to choose from
     * @param sc Scanner for reading user input
     * @return A new team with the chosen name and selected characters
     */
    private static Team selectTeam(String playerName, List<CharacterClass> characters, Scanner sc) {
        Team team = new Team(playerName);
        List<Integer> selected = new ArrayList<>(); // Track selected character indices
        System.out.println(playerName + ", select your team:\n");

        printAvailableCharacters(characters);

        for (int slot = 1; slot <= 2; slot++) {
            System.out.println("\n" + playerName + " - Character " + slot);
            while (true) {
                System.out.print("Enter a number to select, or 'info <number>' for details: ");
                String input = sc.nextLine().trim();
                if (input.startsWith("info ")) { // Checks if user input starts with "info " | Runs when a user is trying to get info about a character
                    try {
                        int num = Integer.parseInt(input.substring(5)) - 1; // gets the number after "info ", converts it to int, then subtracts 1 for zero-based index
                        if (isValidPick(num, characters.size())) {
                            printCharacterInfo(characters.get(num)); // Print detailed info about the selected character ONLY if input is valid
                        } else {
                            System.out.println("Invalid character number for info."); // Error handling for invalid info number
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter 'info <number>' where <number> is a valid character number."); // Error handling for non-integer info input
                    }
                } else { // Runs when a user is trying to select a character
                    try {
                        int num = Integer.parseInt(input) - 1; // Convert input to zero-based index
                        if (isValidPick(num, characters.size()) && !selected.contains(num)) { // Valid selection and not already chosen
                            CharacterClass selectedChar = characters.get(num).clone(); // Clone's the selected class
                            team.addCharacter(selectedChar);
                            selected.add(num);
                            System.out.println(selectedChar.getName() + " added to your team.");
                            break;
                        } else if (selected.contains(num)) { // Error Handling for already selected character
                            System.out.println("You already selected this character. Choose another.");
                        } else { // Error handling for invalid selection
                            System.out.println("Invalid selection. Try again.");
                        }
                    } catch (NumberFormatException e) { // Error handling for non-integer selection input
                        System.out.println("Invalid input. Please enter a number or 'info <number>'.");
                    }
                }
            }
        }
        return team;
    }

    /*  
     * Prints the list of available characters with their indices.
     */
    private static void printAvailableCharacters(List<CharacterClass> characters) {
        System.out.println("Available Characters:");
        for (int i = 0; i < characters.size(); i++) {
            CharacterClass c = characters.get(i);
            System.out.printf("%d. %s (%s)\n", i + 1, c.getName(), c.getType()); // Character name and type
        }
    }

    /*  
     * Prints detailed information about a character.
     */
    private static void printCharacterInfo(CharacterClass c) {
        System.out.println("\n--- " + c.getName() + " ---"); // Character name as header
        System.out.println("Type: " + c.getType()); // Character type
        System.out.printf("HP: %d/%d\n", c.getCurrentHP(), c.getMaxHP()); // Current and max HP
        System.out.printf("Stamina: %d/%d\n", c.getCurrentStamina(), c.getMaxStamina()); // Current and max Stamina
        System.out.printf("Str: %d | Def: %d | End: %d\n", c.getStrength(), c.getDefence(), c.getEndurance()); // Stats
        System.out.println("Skills:");
        for (int j = 0; j < c.getSkills().size(); j++) { // Iterate through skills
            Skill s = c.getSkills().get(j);
            String cd = s.isOnCooldown() ? " [" + s.getCooldownRemaining() + "/" + s.getCooldownMax() + "]" : " [Ready]";
            System.out.printf("  - %s (Cost: %d ST, Dmg: %d, CD: %d/%d)%s\n", s.getName(), s.getStaminaCost(), s.getDamage(), s.getCooldownRemaining(), s.getCooldownMax(), cd); // Skill details
        }
        System.out.println();
    }

    private static boolean isValidPick(int pick, int size) {
        return pick >= 0 && pick < size; // Check if pick is within valid range
    }
}
