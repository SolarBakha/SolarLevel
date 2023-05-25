package me.solarbakha.solarlevel;

import me.solarbakha.solarlevel.commands.solarLevelCMD;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.solarbakha.solarlevel.events.Portal;
import me.solarbakha.solarlevel.events.XPGain;
import me.solarbakha.solarlevel.events.XPGain_BlockBreak;
import me.solarbakha.solarlevel.manager.misc.Localization;
import me.solarbakha.solarlevel.manager.misc.PlaceholderAPIXP;
import me.solarbakha.solarlevel.manager.SQLite.Database;
import me.solarbakha.solarlevel.manager.SQLite.SQLite;
import me.solarbakha.solarlevel.manager.XPManager;

import java.util.Objects;


public final class SolarLevel extends JavaPlugin implements Listener {

    private static SolarLevel INSTANCE;

    private static Database DB;

    private static CoreProtectAPI CORE_API;


    @Override
    public void onEnable() {
        INSTANCE = this;

        // Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Localization.init();


        // Database
        DB = new SQLite(this);
        DB.load();
        XPManager.init();


        // Command
        PluginCommand command = getCommand("solarlevel");
        Objects.requireNonNull(command).setExecutor(new solarLevelCMD());



        // Events
        getServer().getPluginManager().registerEvents(new XPGain(), this);
        getServer().getPluginManager().registerEvents(new Portal(), this);


        // Soft dependencies
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) { //todo: improve ver check, like coreprot
            new PlaceholderAPIXP(this).register();
        } else {
            getLogger().warning("Could not find PlaceholderAPI!");

        }
        // Block break
        CORE_API = initCorePApi();
        if (CORE_API != null) {
            XPGain_BlockBreak.init();
            getServer().getPluginManager().registerEvents(new XPGain_BlockBreak(), this);
        } else getLogger().warning("Could not find CoreProtect! Block break XP functionality disabled!");
    }


    @Override
    public void onDisable() {
        XPManager.termAll();
    }


    /*
    Getters
     */

    public static me.solarbakha.solarlevel.SolarLevel getInstance() {
        return INSTANCE;
    }

    public static Database getDatabase() {
        return DB;
    }

    public static CoreProtectAPI getCorePApi() {
        return CORE_API;
    }


    private CoreProtectAPI initCorePApi() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        if (CoreProtect.APIVersion() < 9) {
            return null;
        }

        return CoreProtect;
    }

}
