package me.solarbakha.solarlevel.events;

import me.solarbakha.solarlevel.SolarLevel;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import me.solarbakha.solarlevel.manager.XPManager;

import java.util.HashMap;
import java.util.Map;

public class XPGain_BlockBreak implements Listener {
    private static final HashMap<String, Integer> blockXP = new HashMap<>();

    public static void init() {
        ConfigurationSection blockXP = SolarLevel.getInstance().getConfig().getConfigurationSection("BlockXP");
        if (blockXP != null) {
            for (Map.Entry<String, Object> block : blockXP.getValues(false).entrySet()) {
                if (block.getKey().contains(":")) // this check adds support for modded blocks
                    XPGain_BlockBreak.blockXP.put(block.getKey(), (Integer) block.getValue());
                else XPGain_BlockBreak.blockXP.put("minecraft:" + block.getKey(), (Integer) block.getValue());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        new BukkitRunnable() {
            @Override
            public void run() {
                int check = SolarLevel.getCorePApi().blockLookup(block, (int) (System.currentTimeMillis() / 1000L)).size();
                if (!(check > 0)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String block_name = block.getBlockData().getAsString(true);
                            if (blockXP.containsKey(block_name)) { //todo: check if works with blocks that got extra data
                                XPManager.addXP(e.getPlayer().getUniqueId().toString(), blockXP.get(block_name));
                            }
                        }
                    }.run();
                }
            }
        }.runTaskAsynchronously(SolarLevel.getInstance());
    }
}
