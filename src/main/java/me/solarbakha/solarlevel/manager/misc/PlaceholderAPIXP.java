package me.solarbakha.solarlevel.manager.misc;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.manager.XPManager;

public class PlaceholderAPIXP extends PlaceholderExpansion {
    private SolarLevel plugin; // This would be the plugin your expansion depends on

    public PlaceholderAPIXP(SolarLevel plugin) {
        this.plugin = plugin;
    }


    @Override
    public @NotNull String getAuthor() {
        return "sha1";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "SolarLevel";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    @NotNull
    public String getRequiredPlugin() {
        return "SolarLevel";
    }

    @Override
    public boolean canRegister() {
        return (plugin = (SolarLevel) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {

        if (params.equalsIgnoreCase("exp")) {
            return p == null ? null : String.valueOf(XPManager.getXP(p.getUniqueId().toString()));
        }

        if (params.equalsIgnoreCase("level")) {
            return p == null ? null : String.valueOf(XPManager.getLevel(p.getUniqueId().toString()));
        }

        if (params.equalsIgnoreCase("next")) {
            return p == null ? null : String.valueOf(XPManager.Lv2XP(XPManager.getLevel(p.getUniqueId().toString()) + 1));
        }

        if (params.equalsIgnoreCase("bar")) {
            if (p == null) return null;
            String pid = p.getUniqueId().toString();
            //bar related vars
            int barLength = Localization.getFileConfiguration().getInt("PAPI_EXPBAR.length");
            String barCharEmpty = Localization.getString("PAPI_EXPBAR.charEmpty");
            String xpBar = Localization.getString("PAPI_EXPBAR.border.left") + barCharEmpty.repeat(barLength) +
                    Localization.getString("PAPI_EXPBAR.border.right");

            if (XPManager.getXP(pid) < XPManager.MAX_XP) {
                float currentXP = XPManager.getXP(pid) - XPManager.Lv2XP(XPManager.getLevel(pid));
                float nextXP = XPManager.Lv2XP(XPManager.getLevel(p.getUniqueId().toString()) + 1) - XPManager.Lv2XP(XPManager.getLevel(pid));
                for (int i = 0; i < Math.round(currentXP / nextXP * 100 / barLength); i++) {
                    xpBar = xpBar.replaceFirst(barCharEmpty, Localization.getString("PAPI_EXPBAR.charFull"));
                }
            } else xpBar = xpBar.replaceAll(barCharEmpty, Localization.getString("PAPI_EXPBAR.charFull"));
            return xpBar;
        }

        return null;
    }
}

