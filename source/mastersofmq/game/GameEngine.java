package mastersofmq.game;

import mastersofmq.model.CharacterClass;
import mastersofmq.model.Skill;
import mastersofmq.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Very small turn-based engine.
 * Turn order: P1.Char1 -> P2.Char1 -> P1.Char2 -> P2.Char2 ...
 * Actions: attack, defend, use skill (simple)
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
        
        // First Battle
        System.out.println("\nChapter 1: The Library Ambush");
        System.out.println("As you study in the library, dark figures emerge from the shadows...");
        Team enemies1 = new Team("Shadow Patrol");
        enemies1.addCharacter(CharacterClass.createOpponent("Dark Warrior"));
        enemies1.addCharacter(CharacterClass.createOpponent("Shadow Mage"));
        this.enemyTeam = enemies1;
        if (!runBattle("Library Battle")) return;

        // Recovery and story progression
        healTeam(playerTeam);
        System.out.println("\nYou've survived the first encounter, but the danger is not over...");
        pressEnterToContinue();

        // Second Battle
        System.out.println("\nChapter 2: Courtyard Confrontation");
        System.out.println("In the university courtyard, more powerful enemies await...");
        Team enemies2 = new Team("Dark Legion");
        enemies2.addCharacter(CharacterClass.createOpponent("Corrupted Rogue"));
        enemies2.addCharacter(CharacterClass.createOpponent("Dark Priest"));
        this.enemyTeam = enemies2;
        if (!runBattle("Courtyard Battle")) return;

        // Final Battle
        healTeam(playerTeam);
        System.out.println("\nChapter 3: The Final Showdown");
        System.out.println("You've made it to the central tower where the source of darkness resides...");
        Team finalBoss = new Team("Overlord's Guard");
        finalBoss.addCharacter(CharacterClass.createOpponent("Boss Overlord"));
        this.enemyTeam = finalBoss;
        if (!runBattle("Final Battle")) return;

        // Victory
        System.out.println("\n=== VICTORY ===");
        System.out.println("Congratulations! You have saved Macquarie University from the dark forces!");
        System.out.println("Your names will be remembered in the halls of MQ forever!");
    }

    private void healTeam(Team team) {
        for (CharacterClass character : team.getCharacters()) {
            if (character.isAlive()) {
                character.heal(character.getMaxHP());
                character.restoreStamina(character.getMaxStamina());
            }
        }
        System.out.println("\nYour team has been healed and restored!");
    }

    private void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        sc.nextLine();
    }

    private boolean runBattle(String battleName) {
        System.out.println("\n=== " + battleName + " Begins! ===");
        run();
        return !playerTeam.isDefeated();
    }

    public void run() {
        int round = 1;
        while (!playerTeam.isDefeated() && !enemyTeam.isDefeated()) {
            System.out.println("\n--- Round " + round + " ---");
            // fixed turn order
            takeTurn(playerTeam, enemyTeam, 0);
            if (enemyTeam.isDefeated()) break;
            takeAITurn(enemyTeam, playerTeam, 0);
            if (playerTeam.isDefeated()) break;
            takeTurn(playerTeam, enemyTeam, 1);
            if (enemyTeam.isDefeated()) break;
            takeAITurn(enemyTeam, playerTeam, 1);
            // end of round: regen stamina and reduce cooldowns
            playerTeam.endOfRound();
            enemyTeam.endOfRound();
            printStatus();
            round++;
        }
        System.out.println("\n=== BATTLE ENDED ===");
        if (playerTeam.isDefeated()) {
            System.out.println(enemyTeam.getName() + " wins! The darkness prevails...");
        } else {
            System.out.println(playerTeam.getName() + " wins! A victory for the light!");
        }
        System.out.println("Battle lasted " + (round - 1) + " rounds.\n");
        System.out.println("Final Status:");
        playerTeam.printStatus();
        enemyTeam.printStatus();
    }

    private void takeAITurn(Team acting, Team opponent, int index) {
        CharacterClass actor = acting.getAliveCharacter(index);
        if (actor == null) return;

        // Stamina regeneration message
        int regen = Math.max(1, actor.getEndurance() / 2);
        System.out.printf("\n--- %s's Turn (%s) ---\n", actor.getName(), acting.getName());
        System.out.printf("Stamina regenerated +%d (Endurance). ST: %d/%d.\n", regen, actor.getCurrentStamina(), actor.getMaxStamina());

        // Get all available actions
        List<String> possibleActions = new ArrayList<>();
        
        // Add basic attack if enough stamina
        if (actor.getCurrentStamina() >= 10) {
            possibleActions.add("attack");
        }
        
        // Add defend (always possible)
        possibleActions.add("defend");
        
        // Add available skills
        for (Skill s : actor.getSkills()) {
            if (actor.canUseSkill(s)) {
                possibleActions.add("skill:" + actor.getSkills().indexOf(s));
            }
        }

        // Randomly select an action
        String action = possibleActions.get(rng.nextInt(possibleActions.size()));
        
        // Choose random target
        CharacterClass target;
        if (action.contains("heal")) {
            // For healing skills, target own team
            List<CharacterClass> aliveAllies = new ArrayList<>();
            for (CharacterClass c : acting.getCharacters()) {
                if (c.isAlive()) {
                    aliveAllies.add(c);
                }
            }
            target = aliveAllies.get(rng.nextInt(aliveAllies.size()));
        } else {
            // For attacks, target opponent team
            List<CharacterClass> aliveEnemies = new ArrayList<>();
            for (CharacterClass c : opponent.getCharacters()) {
                if (c.isAlive()) {
                    aliveEnemies.add(c);
                }
            }
            target = aliveEnemies.get(rng.nextInt(aliveEnemies.size()));
        }

        // Execute the random action
        if (action.equals("attack")) {
            basicAttack(actor, target);
        } else if (action.equals("defend")) {
            actor.defend();
            System.out.println(actor.getName() + " takes a defensive stance!");
        } else if (action.startsWith("skill:")) {
            int skillIndex = Integer.parseInt(action.split(":")[1]);
            Skill selectedSkill = actor.getSkills().get(skillIndex);
            if (selectedSkill.getDamage() < 0) {
                // If it's a healing skill, target ally
                target = acting.getCharacters().get(rng.nextInt(acting.getCharacters().size()));
            }
            actor.useSkill(selectedSkill, target, rng);
        }
    }



    private void takeTurn(Team acting, Team opponent, int index) {
        CharacterClass actor = acting.getAliveCharacter(index);
        if (actor == null) return;

        // Stamina regeneration message
        int regen = Math.max(1, actor.getEndurance() / 2);
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
            if (input.equals("1")) {
                if (actor.getCurrentStamina() >= 10) {
                    // Select target
                    CharacterClass target = selectTarget(opponent);
                    if (target != null) {
                        basicAttack(actor, target);
                        break;
                    }
                } else {
                    System.out.println("Not enough stamina for attack.");
                }
            } else if (input.equals("2")) {
                actor.defend();
                System.out.println(actor.getName() + " takes a defensive stance.");
                break;
            } else if (input.equals("3")) {
                // Skill submenu
                if (handleSkillSelection(actor, opponent, acting)) {
                    break;
                }
            } else if (input.matches("3\\.\\d+")) {
                // Direct skill selection
                if (handleDirectSkillSelection(actor, opponent, acting, input)) {
                    break;
                }
            } else {
                System.out.println("Invalid choice. Please choose 1, 2, 3, or 3.1, 3.2, etc.");
            }
        }
    }

    private boolean handleSkillSelection(CharacterClass actor, Team opponent, Team acting) {
        if (actor.getSkills().isEmpty()) {
            System.out.println("No skills available.");
            return false;
        }
        while (true) {
            System.out.print("Choose a skill (3.1, 3.2, etc.): ");
            String input = sc.nextLine().trim();
            if (input.matches("3\\.\\d+")) {
                int skillIndex = Integer.parseInt(input.substring(2)) - 1;
                if (skillIndex >= 0 && skillIndex < actor.getSkills().size()) {
                    Skill s = actor.getSkills().get(skillIndex);
                    if (actor.canUseSkill(s)) {
                        Team targetTeam = s.getDamage() < 0 ? acting : opponent;
                        CharacterClass target = selectTarget(targetTeam);
                        if (target != null) {
                            actor.useSkill(s, target, rng);
                            return true; // success
                        }
                    } else {
                        System.out.println("Cannot use " + s.getName() + " - check cooldown or stamina");
                    }
                } else {
                    System.out.println("Invalid skill number. Please choose 3.1 or 3.2.");
                }
            } else {
                System.out.println("Invalid skill selection. Please enter 3.1, 3.2, etc.");
            }
        }
    }

    private boolean handleDirectSkillSelection(CharacterClass actor, Team opponent, Team acting, String input) {
        int skillIndex = Integer.parseInt(input.substring(2)) - 1;
        if (skillIndex >= 0 && skillIndex < actor.getSkills().size()) {
            Skill s = actor.getSkills().get(skillIndex);
            if (actor.canUseSkill(s)) {
                Team targetTeam = s.getDamage() < 0 ? acting : opponent;
                CharacterClass target = selectTarget(targetTeam);
                if (target != null) {
                    actor.useSkill(s, target, rng);
                    return true; // success
                }
            } else {
                System.out.println("Cannot use " + s.getName() + " - check cooldown or stamina");
            }
        } else {
            System.out.println("Invalid skill number. Please choose 3.1 or 3.2.");
        }
        return false;
    }

    private CharacterClass selectTarget(Team opponent) {
        List<CharacterClass> alive = new ArrayList<>();
        for (int i = 0; i < opponent.getCharacters().size(); i++) {
            CharacterClass c = opponent.getCharacters().get(i);
            if (c.isAlive()) {
                alive.add(c);
            }
        }
        if (alive.isEmpty()) return null;

        System.out.println("Choose a target:");
        for (int i = 0; i < alive.size(); i++) {
            CharacterClass c = alive.get(i);
            System.out.printf("%d. %s (%s) HP: %d/%d, ST: %d/%d\n", i + 1, c.getName(), c.getType(), c.getCurrentHP(), c.getMaxHP(), c.getCurrentStamina(), c.getMaxStamina());
        }
        while (true) {
            System.out.print("> ");
            String input = sc.nextLine().trim();
            try {
                int num = Integer.parseInt(input) - 1;
                if (num >= 0 && num < alive.size()) {
                    return alive.get(num);
                } else {
                    System.out.println("Invalid target selection. Please choose 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void basicAttack(CharacterClass actor, CharacterClass target) {
        if (target == null) return;
        actor.deductStamina(10); // cost 10 stamina
        int rand = rng.nextInt(6); // 0..5
        int dmg = Math.max(1, actor.getStrength() + rand - target.getDefence());
        // if target defended, reduce damage by half (rounded down)
        if (target.isDefending()) dmg = Math.max(1, dmg/2);
        target.takeDamage(dmg);
        System.out.printf("%s attacks %s!\nStrength (%d) + Roll (%d) - Defence (%d) = %d damage.\n%s takes %d damage! (HP: %d/%d)\n", actor.getName(), target.getName(), actor.getStrength(), rand, target.getDefence(), dmg, target.getName(), dmg, target.getCurrentHP(), target.getMaxHP());
    }

    private void printStatus() {
        System.out.println("\n--- Status ---");
        playerTeam.printStatus();
        enemyTeam.printStatus();
        System.out.println("---------------\n");
    }
}
