package de.kainianer.genuine.util;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;

import org.bukkit.plugin.RegisteredServiceProvider;


public class permissionSetup {

    public Plugin plugin;
    
    public permissionSetup(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public Permission setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = this.plugin.getServer().getServicesManager().getRegistration(Permission.class);
        return rsp.getProvider();
    }

}