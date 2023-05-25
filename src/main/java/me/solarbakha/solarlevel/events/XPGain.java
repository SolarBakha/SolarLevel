package me.solarbakha.solarlevel.events;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.manager.XPManager;

import java.util.Objects;


public class XPGain implements Listener {
    FileConfiguration XPGain = SolarLevel.getPlugin(SolarLevel.class).getConfig();

    /*
    XP EVENTS
     */

    @EventHandler
    public void onMonsterKill(EntityDeathEvent e) {
        Entity ent = e.getEntity().getKiller();
        if (ent instanceof Player pk) {
            String pkID = pk.getUniqueId().toString();
            if (e.getEntity() instanceof Player) {
                XPManager.addXP(pkID, XPGain.getInt("ExperienceGain.player_slay"));
            } else {
                XPManager.addXP(pkID, XPGain.getInt("ExperienceGain.monster_slay"));
            }
        }
    }

    @EventHandler
    public void onNewBiomeEnter(PlayerAdvancementCriterionGrantEvent e) {
        String p = e.getPlayer().getUniqueId().toString();
        Component advObj = Objects.requireNonNull(e.getAdvancement().getDisplay()).title();
        String advName = PlainTextComponentSerializer.plainText().serialize(advObj);
        if (advName.equals("Adventuring Time")) {
            XPManager.addXP(p, XPGain.getInt("ExperienceGain.first_biome_enter"));
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        String p = e.getPlayer().getUniqueId().toString();
        XPManager.addXP(p, XPGain.getInt("ExperienceGain.fish"));
    }

    @EventHandler
    public void onPlayerLv(PlayerLevelChangeEvent e) {
        String p = e.getPlayer().getUniqueId().toString();
        if (e.getNewLevel() > e.getOldLevel()) {  //ignore player death, TODO: test if actually even triggers on death
            XPManager.addXP(p, XPGain.getInt("ExperienceGain.player_level_up") * (e.getNewLevel() - e.getOldLevel()));
        }
    }

    @EventHandler
    public void onPlayerEnchant(EnchantItemEvent e) {
        String p = e.getEnchanter().getUniqueId().toString();
        XPManager.addXP(p, XPGain.getInt("ExperienceGain.enchant"));
    }

    /*
    DATABASE EVENTS
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        XPManager.initXP(e.getPlayer());
    }


    @EventHandler
    public void onWorldSave(WorldSaveEvent e) {
        XPManager.saveAll();
    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        XPManager.termXP(e.getPlayer().getUniqueId().toString());
    }


}
