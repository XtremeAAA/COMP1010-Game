package mastersofmq.game;

import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import mastersofmq.model.Team;
import mastersofmq.fileio.GameDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Core game engine implementing turn-based combat system.
 * 
 * Turn Order:
 * 1. Player Character 1
 * 2. Enemy Character 1
 * 3. Player Character 2
 * 4. Enemy Character 2
 * (and so on...)
 * 
 * Available Actions:
 * - Basic Attack: 10 stamina, damage = max(1, strength + random(0-5) - defence)
 * - Defend: 0 stamina, reduces incoming damage by 50%
 * - Use Skill: variable stamina cost, follows skill's damage/healing rules
 * 
 * End of Round Effects:
 * - Stamina regeneration = max(1, endurance/2)
 * - Clear defensive stances
 * - Reduce skill cooldowns
 * 
 * Access Control:
 * - Public class as it's the main interface for game mechanics
 * - Private fields to maintain game state integrity
 * - Private helper methods for internal battle mechanics
 * - Public methods for starting and running battles
 */
public class GameEngine {
    private Team playerTeam;
    private Team enemyTeam;
    private final Scanner sc = new Scanner(System.in);
    private final Random rng = new Random();

    public GameEngine(Team playerTeam) {
        this.playerTeam = playerTeam;
    }

    public void startNarrative() {
        System.out.println("\n=== Masters of MQ: The Dark Rising ===");
        System.out.println("Welcome, brave warriors of Macquarie University!");
        System.out.println("A dark force has emerged from the shadows, threatening our peaceful halls of learning.");
        System.out.println("You must face three challenges to save our university from the darkness.");
        
        String opponentsPath = java.nio.file.Paths.get("data", "opponents.json").toString();
        GameDataLoader opponentsLoader = new GameDataLoader(opponentsPath); // Load opponents data
        try {
            opponentsLoader.load();
        } catch (Exception e) {
            System.err.println("Failed to load opponent data: " + e.getMessage());
            return;
        }
        List<CharacterClass> opponentsList = opponentsLoader.getCharacters(); // Get loaded opponents

        // First Battle
        System.out.println("\nChapter 1: The Library Ambush");
        System.out.println("As you study in the library, dark figures emerge from the shadows...");
        Team enemies1 = new Team("Shadow Patrol"); // Create enemy team
        // Find Dark Warrior and Shadow Mage in the opponents list
        CharacterClass darkWarrior = findOpponentByName("Dark Warrior", opponentsList); // Find Dark Warrior
        CharacterClass shadowMage = findOpponentByName("Shadow Mage", opponentsList); // Find Shadow Mage
        if (darkWarrior != null && shadowMage != null) { // If both opponents found
            enemies1.addCharacter(darkWarrior.clone()); // Add Dark Warrior to enemy team
            enemies1.addCharacter(shadowMage.clone()); // Add Shadow Mage to enemy team
            this.enemyTeam = enemies1; // Set current enemy team
            
            if (!runBattle("Library Battle")) { // Attempt to run the first battle, "Library Battle"
                return;
        } else {
            System.err.println("Could not find required opponents for battle 1");
            return;
        }

        healTeam(playerTeam); // Recovery and story progression
        System.out.println("\nYou've survived the first encounter, but the danger is not over...");
        pressEnterToContinue();

        // Second Battle
        System.out.println("\nChapter 2: Courtyard Confrontation");
        System.out.println("In Central Courtyard, more powerful enemies await...");
        Team enemies2 = new Team("Dark Legion"); // Create enemy team
        CharacterClass corruptedRogue = findOpponentByName("Corrupted Rogue", opponentsList); // Find Corrupted Rogue
        CharacterClass darkPriest = findOpponentByName("Dark Priest", opponentsList); // Find Dark Priest
        if (corruptedRogue != null && darkPriest != null) { // If both opponents found
            enemies2.addCharacter(corruptedRogue.clone()); // Add Corrupted Rogue to enemy team
            enemies2.addCharacter(darkPriest.clone()); // Add Dark Priest to enemy team
            this.enemyTeam = enemies2; // Set current enemy team
            if (!runBattle("Courtyard Battle")) return; // Attempt to run the second battle, "Courtyard Battle"
        } else {
            System.err.println("Could not find required opponents for battle 2");
            return;
        }

        healTeam(playerTeam); // Recovery and story progression
        System.out.println("\nYou've survived the second encounter, but the danger is not over...");
        pressEnterToContinue();

        // Final Battle
        healTeam(playerTeam); // Recovery and story progression
        System.out.println("\nChapter 3: The Final Showdown"); // Final battle introduction
        System.out.println("You've made it to 4RPD where the source of darkness resides...");
        Team finalBoss = new Team("Overlord's Guard"); // Create enemy team
        CharacterClass bossOverlord = findOpponentByName("Boss Overlord", opponentsList); // Find Boss Overlord
        if (bossOverlord != null) { // If Boss Overlord found
            finalBoss.addCharacter(bossOverlord.clone()); // Add Boss Overlord to enemy team
            this.enemyTeam = finalBoss; // Set current enemy team
            if (!runBattle("Final Battle")) return; // Attempt to run the final battle, "Final Battle"
        } else {
            System.err.println("Could not find required opponent for final battle");
            return;
        }

        // Victory
        System.out.println("\n=== VICTORY ===");
        System.out.println("Congratulations! You have saved Macquarie University from the dark forces!");
        System.out.println("Your names will be remembered in the halls of MQ forever!");
    }

    private void healTeam(Team team) { // Heals and restores stamina for all alive characters in the team
        for (CharacterClass character : team.getCharacters()) {
            if (character.isAlive()) { // If character is alive
                character.heal(character.getMaxHP()); // Heal to max HP
                character.restoreStamina(character.getMaxStamina()); // Restore to max Stamina
            }
        }
        System.out.println("\nYour team has been healed and restored!");
    }

    private void pressEnterToContinue() { // Utility method to pause until user presses Enter
        System.out.println("\nPress Enter to continue...");
        sc.nextLine();
    }

    private boolean runBattle(String battleName) { // Runs a battle and returns true if player wins
        System.out.println("\n=== " + battleName + " Begins! ===");
        run();
        return !playerTeam.isDefeated();
    }

    public void run() {
        int round = 1;
        while (!playerTeam.isDefeated() && !enemyTeam.isDefeated()) { // Battle loop
            System.out.println("\n--- Round " + round + " ---");
            // fixed turn order
            takeTurn(playerTeam, enemyTeam, 0); // Player Character 1
            if (enemyTeam.isDefeated()) break; // Check for enemy defeat
            takeAITurn(enemyTeam, playerTeam, 0); // Enemy Character 1
            if (playerTeam.isDefeated()) break; // Check for player defeat
            takeTurn(playerTeam, enemyTeam, 1); // Player Character 2
            if (enemyTeam.isDefeated()) break; // Check for enemy defeat
            takeAITurn(enemyTeam, playerTeam, 1); // Enemy Character 2
            playerTeam.endOfRound(); // End of round effects
            enemyTeam.endOfRound(); // End of round effects
            printStatus();
            round++;
        }
        System.out.println("\n=== BATTLE CONCLUDED ===");
        if (playerTeam.isDefeated()) { // Defeat messages
            System.out.println("Team " + playerTeam.getName() + " has fallen in battle!");
            if (enemyTeam.getName().equals("Shadow Patrol")) {
                System.out.println("The library falls silent as darkness consumes the halls of knowledge...");
            } else if (enemyTeam.getName().equals("Dark Legion")) {
                System.out.println("The courtyard is overtaken as students flee in terror...");
            } else if (enemyTeam.getName().equals("Overlord's Guard")) {
                System.out.println("The last hope of Macquarie University fades as the Overlord's power grows stronger...");
            }
            System.out.println("Macquarie University needs heroes... Will others rise to face the darkness?");
        } else { // Victory messages
            System.out.println("Team " + playerTeam.getName() + " emerges victorious!");
            if (enemyTeam.getName().equals("Shadow Patrol")) {
                System.out.println("The library is secured! Knowledge shall remain free and accessible!");
            } else if (enemyTeam.getName().equals("Dark Legion")) {
                System.out.println("The courtyard is safe once more! Students can again walk without fear!");
            } else if (enemyTeam.getName().equals("Overlord's Guard")) {
                System.out.println("The Overlord's forces are defeated! Light returns to Macquarie University!");
            }
        }
        System.out.println("\nThe battle raged for " + (round - 1) + " intense rounds.\n");
        System.out.println("Final Status:");
        playerTeam.printStatus(); // Print final status of player team
        enemyTeam.printStatus(); // Print final status of enemy team
    }

    private void takeAITurn(Team acting, Team opponent, int index) {
        CharacterClass actor = acting.getAliveCharacter(index); // Get the acting character
        if (actor == null) return; // If no alive character, skip turn

        int regen = Math.max(1, actor.getEndurance() / 2); // Calculate stamina regeneration
        System.out.printf("\n--- %s's Turn (%s) ---\n", actor.getName(), acting.getName());
        System.out.printf("Stamina regenerated +%d (Endurance). ST: %d/%d.\n", regen, actor.getCurrentStamina(), actor.getMaxStamina());

        List<String> possibleActions = new ArrayList<>(); // Get all available actions
        

        if (actor.getCurrentStamina() >= 10) { // If enough stamina for attack, add to possible actions
            possibleActions.add("attack");
        }
        
        possibleActions.add("defend"); // Add defend action to possible actions
        
        for (Skill s : actor.getSkills()) { // Add available skills
            if (actor.canUseSkill(s)) { // If skill can be used
                possibleActions.add("skill:" + actor.getSkills().indexOf(s)); // Add skill to possible actions
            }
        }

        String action = possibleActions.get(rng.nextInt(possibleActions.size())); // Randomly select an action
        
        CharacterClass target;
        if (action.contains("heal")) { // If it's a healing skill, target own team
            List<CharacterClass> aliveAllies = new ArrayList<>(); // Find alive allies
            for (CharacterClass c : acting.getCharacters()) { // For each character in acting team
                if (c.isAlive()) { // If character is alive
                    aliveAllies.add(c); // Add to alive allies list
                }
            }
            target = aliveAllies.get(rng.nextInt(aliveAllies.size())); // Randomly select an ally target
        } else {

            List<CharacterClass> aliveEnemies = new ArrayList<>(); // Find alive enemies
            for (CharacterClass c : opponent.getCharacters()) { // For each character in opponent team
                if (c.isAlive()) { // If character is alive
                    aliveEnemies.add(c); // Add to alive enemies list
                }
            }
            target = aliveEnemies.get(rng.nextInt(aliveEnemies.size())); // Randomly select an enemy target
        }


        if (action.equals("attack")) { // Execute basic attack
            basicAttack(actor, target);
        } else if (action.equals("defend")) { // Execute defend
            actor.defend();
            System.out.println(actor.getName() + " takes a defensive stance!");
        } else if (action.startsWith("skill:")) { // Execute skill
            int skillIndex = Integer.parseInt(action.split(":")[1]); // Get skill index
            Skill selectedSkill = actor.getSkills().get(skillIndex); // Get selected skill
            if (selectedSkill.getDamage() < 0) {
                target = acting.getCharacters().get(rng.nextInt(acting.getCharacters().size())); // Randomly select an ally target
            }
            actor.useSkill(selectedSkill, target, rng); // Use the skill
        }
    }



    private void takeTurn(Team acting, Team opponent, int index) { //Taking turn system
        CharacterClass actor = acting.getAliveCharacter(index); // Get the acting character
        if (actor == null) return; // If no alive character, skip turn


        int regen = Math.max(1, actor.getEndurance() / 2); // Calculate stamina regeneration
        System.out.printf("\n--- %s's Turn (%s) ---\n", actor.getName(), acting.getName());
        System.out.printf("Stamina regenerated +%d (Endurance). ST: %d/%d.\n", regen, actor.getCurrentStamina(), actor.getMaxStamina());

        // Action menu
        System.out.println("1. Attack (Cost: 10 ST, Base Dmg: Str=" + actor.getStrength() + ")");
        System.out.println("2. Defend (Cost: 0 ST)");
        System.out.println("3. Skills");
        for (int i = 0; i < actor.getSkills().size(); i++) {
            Skill s = actor.getSkills().get(i);
            String status = actor.canUseSkill(s) ? "[Ready]" : "[ON COOLDOWN]";
            System.out.printf("  3.%d %s (Cost: %d ST, Dmg: %d, CD: %d/%d) %s\n", i + 1, s.getName(), s.getStaminaCost(), s.getDamage(), s.getCooldownRemaining(), s.getCooldownMax(), status);
        }

        while (true) {
            System.out.print("Choose an action: ");
            String input = sc.nextLine().trim();
            if (input.equals("1")) { // Basic attack
                if (actor.getCurrentStamina() >= 10) { // If enough stamina for attack
                    CharacterClass target = selectTarget(opponent); // Select target
                    if (target != null) { // If target selected
                        basicAttack(actor, target); // Execute basic attack
                        break;
                    }
                } else { // Not enough stamina
                    System.out.println("Not enough stamina for attack.");
                }
            } else if (input.equals("2")) { // Defend
                actor.defend();
                System.out.println(actor.getName() + " takes a defensive stance.");
                break;
            } else if (input.equals("3")) { // Skill
                if (handleSkillSelection(actor, opponent, acting)) { // Handle skill selection
                    break;
                }
            } else if (input.matches("3\\.\\d+")) { // Direct skill selection
                if (handleDirectSkillSelection(actor, opponent, acting, input)) { // Handle direct skill selection
                    break;
                }
            } else {
                System.out.println("Invalid choice. Please choose 1, 2, 3, or 3.1, 3.2, etc."); // Error handling
            }
        }
    }

    private boolean handleSkillSelection(CharacterClass actor, Team opponent, Team acting) { // Skill selection handling
        if (actor.getSkills().isEmpty()) { // If no skills available
            System.out.println("No skills available.");
            return false;
        }
        while (true) { // Skill selection loop
            System.out.print("Choose a skill (3.1, 3.2, etc.): "); 
            String input = sc.nextLine().trim();
            if (input.matches("3\\.\\d+")) { // If valid skill selection
                int skillIndex = Integer.parseInt(input.substring(2)) - 1; // Get skill index
                if (skillIndex >= 0 && skillIndex < actor.getSkills().size()) { // If valid skill index
                    Skill s = actor.getSkills().get(skillIndex); // Get selected skill
                    if (actor.canUseSkill(s)) { // If skill can be used
                        Team targetTeam = s.getDamage() < 0 ? acting : opponent; // Determine target team based on skill type
                        CharacterClass target = selectTarget(targetTeam); // Select target
                        if (target != null) { // If target selected
                            actor.useSkill(s, target, rng); // Use the skill
                            return true; // success
                        }
                    } else { // Invalid input
                        System.out.println("Cannot use " + s.getName() + " - check cooldown or stamina");
                    }
                } else { // Invalid input
                    System.out.println("Invalid skill number. Please choose 3.1 or 3.2.");
                }
            } else { // Invalid input
                System.out.println("Invalid skill selection. Please enter 3.1, 3.2, etc.");
            }
        }
    }


    private CharacterClass findOpponentByName(String name, List<CharacterClass> opponents) { // Helper method to find an opponent by name in the list of opponents
        for (CharacterClass opponent : opponents) { // For each opponent
            if (opponent.getName().equals(name)) { // If names match
                return opponent;
            }
        }
        return null;
    }

    private boolean handleDirectSkillSelection(CharacterClass actor, Team opponent, Team acting, String input) { // Direct skill selection handling
        int skillIndex = Integer.parseInt(input.substring(2)) - 1; // Get skill index
        if (skillIndex >= 0 && skillIndex < actor.getSkills().size()) { // If valid skill index
            Skill s = actor.getSkills().get(skillIndex); // Get selected skill
            if (actor.canUseSkill(s)) { // If skill can be used 
                Team targetTeam = s.getDamage() < 0 ? acting : opponent; // Determine target team based on skill type
                CharacterClass target = selectTarget(targetTeam); // Select target
                if (target != null) { // If target selected
                    actor.useSkill(s, target, rng); // Use the skill
                    return true; // success
                }
            } else { // If skill cannot be used
                System.out.println("Cannot use " + s.getName() + " - check cooldown or stamina");
            }
        } else { // If invalid skill index
            System.out.println("Invalid skill number. Please choose 3.1 or 3.2.");
        }
        return false;
    }

    private CharacterClass selectTarget(Team opponent) { // Target selection system
        List<CharacterClass> alive = new ArrayList<>(); // List of alive opponents
        for (int i = 0; i < opponent.getCharacters().size(); i++) { // For each character in opponent team
            CharacterClass c = opponent.getCharacters().get(i); // Get character
            if (c.isAlive()) {
                alive.add(c);
            }
        }
        if (alive.isEmpty()) return null; // No alive targets

        System.out.println("Choose a target:"); // Display alive targets
        for (int i = 0; i < alive.size(); i++) { // For each alive character
            CharacterClass c = alive.get(i); // Get character
            System.out.printf("%d. %s (%s) HP: %d/%d, ST: %d/%d\n", i + 1, c.getName(), c.getType(), c.getCurrentHP(), c.getMaxHP(), c.getCurrentStamina(), c.getMaxStamina());
        }
        while (true) {
            System.out.print("> "); // Prompt for target selection
            String input = sc.nextLine().trim(); // Get user input
            try {
                int num = Integer.parseInt(input) - 1; // Convert to zero-based index
                if (num >= 0 && num < alive.size()) { // If valid selection
                    return alive.get(num);
                } else { // If invalid selection
                    System.out.println("Invalid target selection. Please choose 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void basicAttack(CharacterClass actor, CharacterClass target) { // Basic attack implementation
        if (target == null) return; // If no target, return
        actor.deductStamina(10); // cost 10 stamina
        int rand = rng.nextInt(6); // 0..5
        int dmg = Math.max(1, actor.getStrength() + rand - target.getDefence()); // base damage
        if (target.isDefending()) dmg = Math.max(1, dmg/2); // halve damage if defending
        target.takeDamage(dmg); // apply damage
        System.out.printf("%s attacks %s!\nStrength (%d) + Roll (%d) - Defence (%d) = %d damage.\n%s takes %d damage! (HP: %d/%d)\n", actor.getName(), target.getName(), actor.getStrength(), rand, target.getDefence(), dmg, target.getName(), dmg, target.getCurrentHP(), target.getMaxHP());
    }

    private void printStatus() { // Print status of both teams
        System.out.println("\n--- Status ---");
        playerTeam.printStatus();
        enemyTeam.printStatus();
        System.out.println("---------------\n");
    }
}
