package mastersofmq.fileio;

import mastersofmq.model.Skill;
import mastersofmq.model.CharacterClass;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Loads game data from JSON configuration files.
 * Handles loading and parsing of:
 * - Character definitions
 * - Skill definitions
 * - Skill prerequisite relationships
 * - Enemy/opponent templates
 * 
 * JSON Format:
 * {
 *   "skills": [
 *     {"id": "skill_id", "name": "Skill Name", "damage": X, "staminaCost": Y, "cooldown": Z}
 *   ],
 *   "characters": [
 *     {"name": "Name", "type": "Type", "hp": X, "stamina": Y, "str": A, "def": B, "end": C}
 *   ]
 * }
 * 
 * Uses org.json library for parsing (must be in classpath).
 * 
 * Access Control:
 * - Public class to allow data loading from main game
 * - Private fields to store loaded data securely
 * - Private methods for internal data processing
 * - Public methods for accessing loaded game data
 */
public class GameDataLoader {
    private final String path; // Path to the JSON data file
    private final List<CharacterClass> characters = new ArrayList<>(); // List of loaded character templates
    private final Map<String, Skill> skillMap = new HashMap<>(); // Map of skill IDs to skill objects

    public GameDataLoader(String path) { // Constructor to set the data file path
        this.path = path;
    }

    public List<CharacterClass> getCharacters() {
        return Collections.unmodifiableList(characters); // Returns an unmodifiable view of the loaded characters
    }

    public void load() throws Exception { // Loads and parses the JSON data file
        try (InputStream is = new FileInputStream(path)) { // Opens the file input stream
            JSONObject root = new JSONObject(new JSONTokener(is)); // Parses the JSON content
            JSONArray skills = root.optJSONArray("skills"); // Retrieves the skills array
            if (skills != null) {
                loadSkills(skills);
                loadPrerequisites(skills);
            }
            JSONArray chars = root.optJSONArray("characters"); // Retrieves the characters array
            JSONArray opponents = root.optJSONArray("opponents"); // Retrieves the opponents array
            if (chars != null) {
                loadCharacters(chars);
            }
            if (opponents != null) {
                loadCharacters(opponents);
            }
        }
    }

    private void loadSkills(JSONArray skills) { // Loads skill definitions into skillMap
        for (int i = 0; i < skills.length(); i++) {
            JSONObject s = skills.getJSONObject(i); // Get skill JSON object
            String id = s.optString("id", "unknown"); // Gets the skill ID
            String name = s.optString("name", "Unknown Skill"); // Gets the skill name
            int damage = s.optInt("damage", 0); // Gets the skill damage
            int staminaCost = s.optInt("staminaCost", 0); // Gets the skill stamina cost
            int cooldown = s.optInt("cooldown", 0); // Gets the skill cooldown
            Skill sk = new Skill(id, name, damage, staminaCost, cooldown); // Creates the Skill object
            skillMap.put(id, sk); // Stores the skill in the map
        }
    }

    private void loadPrerequisites(JSONArray skills) { // Loads skill prerequisite relationships
        for (int i = 0; i < skills.length(); i++) {
            JSONObject s = skills.getJSONObject(i); // Get skill JSON object
            String id = s.getString("id"); // Gets the skill ID
            Skill sk = skillMap.get(id); // Retrieves the Skill object from the map
            JSONArray pre = s.optJSONArray("prerequisiteSkills"); // Gets the prerequisite skills array
            if (pre != null) { // If there are prerequisites
                for (int j = 0; j < pre.length(); j++) { // For each prerequisite
                    String pid = pre.getString(j); // Gets the prerequisite skill ID
                    Skill p = skillMap.get(pid); // Retrieves the prerequisite Skill object
                    if (p != null) sk.addPrerequisite(p); // Adds the prerequisite to the skill
                }
            }
        }
    }

    private void loadCharacters(JSONArray chars) {// Loads character definitions into characters list
        for (int i = 0; i < chars.length(); i++) {
            JSONObject c = chars.getJSONObject(i); // Get character JSON object
            String name = c.optString("name", "Unknown Character"); // Gets the character name
            String type = c.optString("type", "Unknown"); // Gets the character type
            int baseHP = c.optInt("baseHP", 50); // Gets the base HP
            int baseStamina = c.optInt("baseStamina", 50); // Gets the base Stamina
            int baseStrength = c.optInt("baseStrength", 5); // Gets the base Strength
            int baseDefence = c.optInt("baseDefence", 5); // Gets the base Defence
            int baseEndurance = c.optInt("baseEndurance", 5); // Gets the base Endurance
            CharacterClass cc = new CharacterClass(name, type, baseHP, baseStamina, baseStrength, baseDefence, baseEndurance); // Creates the CharacterClass object
            JSONArray skl = c.optJSONArray("skills"); // Gets the skills array
            if (skl != null) { // If there are skills
                for (int j = 0; j < skl.length(); j++) {
                    String sid = skl.optString(j, ""); // Gets the skill ID
                    Skill sref = skillMap.get(sid); // Retrieves the Skill object from the map
                    if (sref != null) cc.addSkill(sref.clone()); // Adds a clone of the skill to the character
                }
            }
            characters.add(cc); // Adds the character to the list
        }
    }

}
