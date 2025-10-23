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
 * Entry point for MastersOfMQ.
 * Simple console-driven 2-player local game.
 */
public class MastersOfMQ {
    public static void main(String[] args) {
        System.out.println("Masters of MQ - Starting game...");

        String dataPath = Paths.get("data", "characters.json").toString();
        GameDataLoader loader = new GameDataLoader(dataPath);
        try {
            loader.load();
        } catch (Exception e) {
            System.err.println("Failed to load game data: " + e.getMessage());
            return;
        }

        List<CharacterClass> characters = loader.getCharacters();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Masters of MQ ===");
        System.out.println("Characters loaded successfully!\n");

        Team playerTeam = selectTeam("Hero", characters, sc);

        System.out.println("\n=== YOUR TEAM ===");
        System.out.println(playerTeam.getName() + "'s Team: " + playerTeam.getTeamSummary());
        System.out.println("================\n");

        GameEngine engine = new GameEngine(playerTeam);
        engine.startNarrative();
    }

    private static Team selectTeam(String playerName, List<CharacterClass> characters, Scanner sc) {
        Team team = new Team(playerName);
        List<Integer> selected = new ArrayList<>();
        System.out.println(playerName + ", select your team:\n");

        printAvailableCharacters(characters);

        for (int slot = 1; slot <= 2; slot++) {
            System.out.println("\n" + playerName + " - Character " + slot);
            while (true) {
                System.out.print("Enter a number to select, or 'info <number>' for details: ");
                String input = sc.nextLine().trim();
                if (input.startsWith("info ")) {
                    try {
                        int num = Integer.parseInt(input.substring(5)) - 1;
                        if (isValidPick(num, characters.size())) {
                            printCharacterInfo(characters.get(num));
                        } else {
                            System.out.println("Invalid character number for info.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter 'info <number>' where <number> is a valid character number.");
                    }
                } else {
                    try {
                        int num = Integer.parseInt(input) - 1;
                        if (isValidPick(num, characters.size()) && !selected.contains(num)) {
                            CharacterClass selectedChar = characters.get(num).clone();
                            team.addCharacter(selectedChar);
                            selected.add(num);
                            System.out.println(selectedChar.getName() + " added to your team.");
                            break;
                        } else if (selected.contains(num)) {
                            System.out.println("You already selected this character. Choose another.");
                        } else {
                            System.out.println("Invalid selection. Try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number or 'info <number>'.");
                    }
                }
            }
        }
        return team;
    }

    private static void printAvailableCharacters(List<CharacterClass> characters) {
        System.out.println("Available Characters:");
        for (int i = 0; i < characters.size(); i++) {
            CharacterClass c = characters.get(i);
            System.out.printf("%d. %s (%s)\n", i + 1, c.getName(), c.getType());
        }
    }

    private static void printCharacterInfo(CharacterClass c) {
        System.out.println("\n--- " + c.getName() + " ---");
        System.out.println("Type: " + c.getType());
        System.out.printf("HP: %d/%d\n", c.getCurrentHP(), c.getMaxHP());
        System.out.printf("Stamina: %d/%d\n", c.getCurrentStamina(), c.getMaxStamina());
        System.out.printf("Str: %d | Def: %d | End: %d\n", c.getStrength(), c.getDefence(), c.getEndurance());
        System.out.println("Skills:");
        for (int j = 0; j < c.getSkills().size(); j++) {
            Skill s = c.getSkills().get(j);
            String cd = s.isOnCooldown() ? " [" + s.getCooldownRemaining() + "/" + s.getCooldownMax() + "]" : " [Ready]";
            System.out.printf("  - %s (Cost: %d ST, Dmg: %d, CD: %d/%d)%s\n", s.getName(), s.getStaminaCost(), s.getDamage(), s.getCooldownRemaining(), s.getCooldownMax(), cd);
        }
        System.out.println();
    }

    private static boolean isValidPick(int pick, int size) {
        return pick >= 0 && pick < size;
    }
}
