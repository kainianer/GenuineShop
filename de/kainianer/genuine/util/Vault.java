package de.kainianer.genuine.util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

    private final Plugin plugin;

    public Vault(Plugin plugin) {
        this.plugin = plugin;
    }

    public Permission setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = this.plugin.getServer().getServicesManager().getRegistration(Permission.class);
        return rsp.getProvider();
    }

    public Economy setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        return rsp.getProvider();
    }

}
