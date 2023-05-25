package me.solarbakha.solarlevel.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import me.solarbakha.solarlevel.SolarLevel;

import java.util.HashMap;
import java.util.Map;

public class XPManager {
    private static final HashMap<String, Integer> PLAYER_LIST = new HashMap<>();
    private static final HashMap<String, Double> LEVEL_FORMULA = new HashMap<>();
    public static int MAX_XP;


    /*
    Init.
     */
    public static void init() {
        ConfigurationSection lev1 = SolarLevel.getInstance().getConfig().getConfigurationSection("LevelFormula");
        if (lev1 != null) for (Map.Entry<String, Object> entry : lev1.getValues(false).entrySet())
            LEVEL_FORMULA.put(entry.getKey(), (Double) entry.getValue());
        int maxLV = XP2Lv(Integer.MAX_VALUE);
        MAX_XP = Lv2XP(maxLV > 10 ? maxLV - maxLV % 10 : maxLV); // dunno why you'd have max lv under 10 but just in case
    }

    /*
    RAM Access
     */

    public static int getXP(String p) {
        return PLAYER_LIST.get(p);
    }

    public static void setXP(String p, int toSet) {
        PLAYER_LIST.put(p, Math.min(toSet, MAX_XP));
    }

    public static void addXP(String p, int toAdd) {
        PLAYER_LIST.put(p, Math.min(getXP(p) + toAdd, MAX_XP));
    }


    public static int getLevel(String p) {
        return XP2Lv(getXP(p));
    }

    public static void setLevel(String p, int toSet) {
        setXP(p, Lv2XP(toSet));
    }

    public static void addLevel(String p, int toAdd) {
        setLevel(p, getLevel(p) + toAdd);
    }

    /*
    Math Calculations
     */

    public static int Lv2XP(int lv) {
        double a = LEVEL_FORMULA.get("a");
        double b = LEVEL_FORMULA.get("b");
        double c = LEVEL_FORMULA.get("c");
        double pain = (a * Math.pow(lv, 3)) + (b * Math.pow(lv, 2)) + (c * lv);
        return (int) pain;
    }

    public static int XP2Lv(int xp) {
        if (xp == 0) return 0;
        double a = LEVEL_FORMULA.get("a");
        double b = LEVEL_FORMULA.get("b");
        double c = LEVEL_FORMULA.get("c");
        double quickMafs;
        if (a != 0) { // thanks satan
            double mafs1 = 27 * Math.pow(a, 2) * xp + 9 * a * b * c - 2 * Math.pow(b, 3);
            double mafs2 = 3 * a * c - Math.pow(b, 2);
            double mfas3 = Math.pow(Math.sqrt(Math.pow(mafs1, 2) + 4 * Math.pow(mafs2, 3)) + mafs1, 1.0 / 3);
            quickMafs = (mfas3 / (3 * a * Math.pow(2, (1.0 / 3)))) -
                    ((Math.pow(2, (1.0 / 3)) * (mafs2)) / (3 * a * mfas3)) -
                    b / (3 * a);
        } else if (b != 0) {
            quickMafs = (Math.sqrt(4 * b * xp + Math.pow(c, 2)) - c) / (2 * b);
        } else if (c != 0) quickMafs = xp / c;
        else quickMafs = 0.0;

        String text = String.valueOf(quickMafs);
        double len = Math.pow(10, text.length() - text.indexOf(".") - 3); // round more
        return (int) (Math.round(quickMafs * len) / len);
    }


    /*
    Database Access
     */


    public static void initXP(Player p) {
        String pid = p.getUniqueId().toString();
        if (!p.hasPlayedBefore()) XPStorage.initXP(pid);
        PLAYER_LIST.put(pid, XPStorage.getXP(pid));
        /*
        TODO: try/catch in case player's not in db for some reason??
        That would only happen if db manually edited while player's in the sv, should still be accounte for
         */

    }

    public static void saveXP(String p) {
        XPStorage.setXP(p, getXP(p));
    }

    public static void saveAll() {
        for (String p : PLAYER_LIST.keySet()) {
            saveXP(p);
        }
    }

    public static void termXP(String p) {
        saveXP(p);
        PLAYER_LIST.remove(p);
    }

    public static void termAll() {
        for (String p : PLAYER_LIST.keySet()) {
            termXP(p);
        }
        XPStorage.disable();
    }

}
