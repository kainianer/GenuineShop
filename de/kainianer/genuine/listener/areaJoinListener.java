package de.kainianer.genuine.listener;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import de.kainianer.genuine.Shop;
import de.kainianer.genuine.books.Book;
import de.kainianer.genuine.util.ShopMethods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class areaJoinListener implements Listener {

    @EventHandler
    public void onRegionEnter(RegionEnterEvent e) {
        if (e.getRegion().getId().equalsIgnoreCase(Shop.shop.getConfig().getString("settings.location"))) {
            Player player = e.getPlayer();
            player.sendMessage(ChatColor.GOLD + "Bon jour! Welcome at the tradingcenter!");
            if (!ShopMethods.containsBook(player.getInventory().getContents())) {
                ItemStack istack = new ItemStack(Material.WRITTEN_BOOK);
                istack.setItemMeta(Book.getEmptyMetaData());
                player.getInventory().addItem(istack);
            }
        }
    }
}
