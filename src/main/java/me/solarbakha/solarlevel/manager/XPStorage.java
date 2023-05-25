package me.solarbakha.solarlevel.manager;

import org.bukkit.scheduler.BukkitRunnable;
import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.manager.misc.ConvLib;


public class XPStorage {


    public static void setXP(String p, int xp) {
        SolarLevel.getDatabase().executeStatement(String.format("UPDATE levels SET xp = '%2$s' WHERE player = '%1$s';", p, xp));
    }

    public static int getXP(String p) {
        return (int) SolarLevel.getDatabase().queryValue(p, "xp");
    }


    public static void initXP(String p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                SolarLevel.getDatabase().executeStatement(String.format("INSERT OR IGNORE INTO levels VALUES ('%s', '0');", p));
            }
        }.runTaskAsynchronously(SolarLevel.getInstance());
    }

    public static void disable(){
        ConvLib.log("Closing database connection...");
        SolarLevel.getDatabase().closeConnection();
    }
}
