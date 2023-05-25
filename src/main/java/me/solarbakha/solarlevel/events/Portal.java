package me.solarbakha.solarlevel.events;

import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.manager.XPManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class Portal implements Listener {
    FileConfiguration XPLock = SolarLevel.getPlugin(SolarLevel.class).getConfig();

    @EventHandler
    public void onPortalEnter(PlayerPortalEvent e) {
        Location dimensionTo = e.getTo();
        int xp = XPManager.getXP(e.getPlayer().getUniqueId().toString());

        if (dimensionTo.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            if (xp < XPLock.getInt("DimensionLock.nether")) e.setCancelled(true);
        }
        if (dimensionTo.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            if (xp < XPLock.getInt("DimensionLock.end")) e.setCancelled(true);
        }
    }
}
