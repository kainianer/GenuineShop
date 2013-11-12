package de.kainianer.genuine.listener;

import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import de.kainianer.genuine.Shop;
import de.kainianer.genuine.util.ShopMethods;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class areaLeaveListener implements Listener {

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent e) {
        if (e.getRegion().getId().equalsIgnoreCase(Shop.shop.getConfig().getString("settings.location"))) {
            Player player = e.getPlayer();
            player.sendMessage(ChatColor.GOLD + "Au revoir!");
            if (ShopMethods.containsBook(player.getInventory().getContents())) {
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack != null) {
                        if (itemStack.getType().equals(Material.WRITTEN_BOOK)) {
                            BookMeta meta = (BookMeta) itemStack.getItemMeta();
                            if (meta.hasTitle()) {
                                if (meta.getTitle().equalsIgnoreCase(ChatColor.GOLD + "Record of offers")) {
                                    player.getInventory().remove(itemStack);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
