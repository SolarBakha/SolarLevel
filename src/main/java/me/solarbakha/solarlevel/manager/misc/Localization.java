package me.solarbakha.solarlevel.manager.misc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.solarbakha.solarlevel.SolarLevel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Localization {
    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void init() {
        file = new File(SolarLevel.getInstance().getDataFolder(), "language.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                ConvLib.logExc("File write error: %1$s.\n".formatted(file), e);
                return;
            }
        }
        reload();
        fileConfiguration.options().copyDefaults(true);
        addDefaults();
        save();

    }

    private static void addDefaults() {  // there has to be a better way, look into CONFIGURATION
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("Command.getMessage", "%player% has %value% %type%.");
        defaults.put("Command.setMessage", "Set %value% %type% on %player%.");
        defaults.put("Command.addMessage", "Gave %value% %type% to %player%.");
        defaults.put("Command.reloadMessage", "Reloaded solarlevel configuration.");

        defaults.put("Command.usage", "Usage:\n/solarlevel <get,add,set,reload> <Player> <value if set/add> <optional: exp/levels");

        defaults.put("Errors.notEnoughArguments", "Errors.notEnoughArguments");
        defaults.put("Errors.wrongArgument", "Errors.wrongArgument");
        defaults.put("Errors.playerNotFound", "Errors.playerNotFound");

        defaults.put("PAPI_EXPBAR.length", 10);
        defaults.put("PAPI_EXPBAR.charEmpty", '-');
        defaults.put("PAPI_EXPBAR.charFull", '#');
        defaults.put("PAPI_EXPBAR.border.left", "-[");
        defaults.put("PAPI_EXPBAR.border.right", "]-");  //todo: move this to normal config maybe


        fileConfiguration.addDefaults(defaults);
    }

    public static String parseString(String path) {

        return fileConfiguration.getString(path).replace("%player%", "%1$s")
                .replace("%value%", "%2$s")
                .replace("%type%", "%3$s");
    }

    public static String getString(String path) {
        return fileConfiguration.getString(path);
    }

    public static FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public static void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            ConvLib.logExc("File write error: %1$s.\n".formatted(file), e);
        }
    }
}
