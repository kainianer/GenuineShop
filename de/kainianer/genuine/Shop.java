package de.kainianer.genuine;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import de.kainianer.genuine.books.Book;
import de.kainianer.genuine.command.CommandShop;
import de.kainianer.genuine.listener.areaJoinListener;
import de.kainianer.genuine.listener.areaLeaveListener;
import de.kainianer.genuine.util.Vault;
import de.kainianer.genuine.util.WorldGuardHook;

public class Shop extends JavaPlugin {
    
    public static Shop shop;
    public HashMap<String, Book> playerSearch = new HashMap<>();
    private Vault vault;
    public Permission perms;
    public WorldGuardPlugin wgPlugin;
    public Economy economy;
    
    @Override
    public void onEnable() {

        /*
         * Declaration 
         */
        this.vault = new Vault(this);
        shop = this;

        /*
         * Config
         */
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        /*
         * WorldGuardHook
         */
        if (this.getConfig().getBoolean("settings.bookInRegion")) {
            WorldGuardHook wgHook = new WorldGuardHook(this);
            this.wgPlugin = wgHook.getWorldGuard();
        }

        /*
         * Permissions and Chat
         */
        try {
            this.perms = vault.setupPermissions();
            System.out.println("[GenuineShop] Hooked into permissions!");
        } catch (Exception e) {
            System.out.println("[GenuineShop] Something went wrong, when trying to hook into permissions.");
        }
        try {
            this.economy = vault.setupEconomy();
            System.out.println("[GenuineShop] Hooked into economy!");
        } catch (Exception e) {
            System.out.println("[GenuineShop] Something went wrong, when trying to hook into economy system");
        }

        /*
         * Commands
         */
        this.getCommand("shop").setExecutor(new CommandShop(this));

        /*
         * Listener
         */
        if (this.getConfig().getBoolean("settings.bookInRegion")) {
            this.getServer().getPluginManager().registerEvents(new areaJoinListener(), this);
            this.getServer().getPluginManager().registerEvents(new areaLeaveListener(), this);
        }

        /*
         * Schedule Updater
         */
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
                new Runnable() {
                    @Override
                    public void run() {
                        Player[] players = Bukkit.getServer().getOnlinePlayers();
                        for (Player player : players) {
                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null) {
                                    if (itemStack.getType().equals(Material.WRITTEN_BOOK)) {
                                        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
                                        if (bookMeta.hasTitle()) {
                                            if (bookMeta.getTitle().equalsIgnoreCase(ChatColor.GOLD + "Record of offers")) {
                                                if (Shop.shop.playerSearch.containsKey(player.getName())) {
                                                    Book book = Shop.shop.playerSearch.get(player.getName());
                                                    book.updateList();
                                                    itemStack.setItemMeta(book.getMetaData());
                                                } else {
                                                    itemStack.setItemMeta(Book.getEmptyMetaData());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 0, 20);
    }

    @Override
    public void onDisable() {

    }
}
