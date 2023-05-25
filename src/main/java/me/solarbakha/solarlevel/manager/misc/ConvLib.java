package me.solarbakha.solarlevel.manager.misc;

import me.solarbakha.solarlevel.SolarLevel;

import java.util.logging.Level;

public class ConvLib {

    /*
    Logger functions
     */

    public static void log(String msg){
        SolarLevel.getInstance().getLogger().info(msg);
    }
    public static void logWarn(String msg){
        SolarLevel.getInstance().getLogger().warning(msg);
    }

    public static void logExc(String msg, Throwable thrown) {
        SolarLevel.getInstance().getLogger().log(Level.SEVERE, msg, thrown);
    }
}
