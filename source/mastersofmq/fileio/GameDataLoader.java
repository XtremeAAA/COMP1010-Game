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
 * Loads characters and skills from a JSON file.
 * Uses org.json library (add it to your classpath).
 */
public class GameDataLoader {
    private final String path;
    private final List<CharacterClass> characters = new ArrayList<>();
    private final Map<String, Skill> skillMap = new HashMap<>();

    public GameDataLoader(String path) { this.path = path; }

    public List<CharacterClass> getCharacters() { 
        return Collections.unmodifiableList(characters); 
    }

    public void load() throws Exception {
        try (InputStream is = new FileInputStream(path)) {
            JSONObject root = new JSONObject(new JSONTokener(is));
            JSONArray skills = root.optJSONArray("skills");
            if (skills != null) {
                loadSkills(skills);
                loadPrerequisites(skills);
            }
            JSONArray chars = root.optJSONArray("characters");
            JSONArray opponents = root.optJSONArray("opponents");
            if (chars != null) {
                loadCharacters(chars);
            }
            if (opponents != null) {
                loadCharacters(opponents); // reuse the same loading logic
            }
        }
    }

    private void loadSkills(JSONArray skills) {
        for (int i = 0; i < skills.length(); i++) {
            JSONObject s = skills.getJSONObject(i);
            String id = s.optString("id", "unknown");
            String name = s.optString("name", "Unknown Skill");
            int damage = s.optInt("damage", 0);
            int staminaCost = s.optInt("staminaCost", 0);
            int cooldown = s.optInt("cooldown", 0);
            Skill sk = new Skill(id, name, damage, staminaCost, cooldown);
            skillMap.put(id, sk);
        }
    }

    private void loadPrerequisites(JSONArray skills) {
        for (int i = 0; i < skills.length(); i++) {
            JSONObject s = skills.getJSONObject(i);
            String id = s.getString("id");
            Skill sk = skillMap.get(id);
            JSONArray pre = s.optJSONArray("prerequisiteSkills");
            if (pre != null) {
                for (int j = 0; j < pre.length(); j++) {
                    String pid = pre.getString(j);
                    Skill p = skillMap.get(pid);
                    if (p != null) sk.addPrerequisite(p);
                }
            }
        }
    }

    private void loadCharacters(JSONArray chars) {
        for (int i = 0; i < chars.length(); i++) {
            JSONObject c = chars.getJSONObject(i);
            String name = c.optString("name", "Unknown Character");
            String type = c.optString("type", "Unknown");
            int baseHP = c.optInt("baseHP", 50);
            int baseStamina = c.optInt("baseStamina", 50);
            int baseStrength = c.optInt("baseStrength", 5);
            int baseDefence = c.optInt("baseDefence", 5);
            int baseEndurance = c.optInt("baseEndurance", 5);
            CharacterClass cc = new CharacterClass(name, type, baseHP, baseStamina, baseStrength, baseDefence, baseEndurance);
            JSONArray skl = c.optJSONArray("skills");
            if (skl != null) {
                for (int j = 0; j < skl.length(); j++) {
                    String sid = skl.optString(j, "");
                    Skill sref = skillMap.get(sid);
                    if (sref != null) cc.addSkill(sref.clone());
                }
            }
            characters.add(cc);
        }
    }

}
