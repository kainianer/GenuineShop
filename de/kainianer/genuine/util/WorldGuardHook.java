package de.kainianer.genuine.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;

public class WorldGuardHook {

    private final Plugin plugin;

    public WorldGuardHook(Plugin plugin) {
        this.plugin = plugin;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin wgPlugin = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) wgPlugin;
    }

}
