package de.kainianer.genuine.util;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import de.kainianer.genuine.Shop;
import de.kainianer.genuine.indi.Indi;
import de.kainianer.genuine.shop.Offer;
import java.util.ArrayList;

public class ShopMethods {

    /*
     * Declaration of Attributes
     */
    private static final ChatColor green = ChatColor.DARK_GREEN;
    private static final ChatColor darkGreen = ChatColor.GOLD;
    private static final ChatColor yellow = ChatColor.YELLOW;

    /*
     * Stringbuilder
     */
    public static String stringBuilder(String[] string) {
        String endString = "";
        for (int i = 1; i < string.length; i++) {
            endString = endString + string[i] + " ";
        }
        return endString;
    }

    public static String stringBuilderArgs(String[] string) {
        String endString = "";
        for (String string1 : string) {
            endString = endString + string1 + " ";
        }
        return endString;
    }

    /*
     * Shophelp
     */
    public static String shopHelp() {
        String string = darkGreen + "-------- " + ChatColor.WHITE + "Help for GenuineShop" + darkGreen + " --------\n"
                + green + "- /shop mine" + yellow + " {PAGE}\n"
                + green + "- /shop sell" + yellow + " {PRICE_PER_ITEM}\n"
                + green + "- /shop search" + yellow + " {ITEM_NAME / ITEM_ID} {PAGE}\n"
                + green + "- /shop buy" + yellow + " {ID_FROM_SEARCH}\n"
                + green + "- /shop cancel" + yellow + " {ID_FROM_YOUR_AUCTION}\n"
                + green + "- /shop toggle\n"
                + green + "- /shop help\n"
                + green + "- /shop info";
        return string;
    }

    /*
     * Formatting Search/Offer
     */
    public static String formatPrice(double doubleValue, Player player) {

        /*
         * If player has enough money of doubleValue
         */
        if (Shop.shop.economy.has(player.getName(), doubleValue)) {
            return ChatColor.GREEN + Shop.shop.economy.format(doubleValue);
        } else {
            return ChatColor.RED + Shop.shop.economy.format(doubleValue);
        }
    }

    public static boolean allFromOnePlayer(Player player, List<Offer> list) {

        /*
         * Checking if all offers from list are from one player
         */
        for (Offer offer : list) {
            if (!offer.getPlayer().equalsIgnoreCase(player.getName())) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasTooManyAuctions(Player player) {
        return new Indi(player.getName()).hasTooManyAuctions();
    }

    public static boolean isBlacklisted(Material material) {

        /*
         * Getting list of banned items
         */
        List<Integer> blackList = Shop.shop.getConfig().getIntegerList("banned.items");
        for (Integer integer : blackList) {

            /*
             * Creating material from id
             */
            Material materialListed = Material.matchMaterial(String.valueOf(integer));

            /*
             * Returning true if item is blacklisted
             */
            if (materialListed.equals(material)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsBook(ItemStack[] inventoryContents) {
        for (ItemStack itemStack : inventoryContents) {
            if (itemStack != null) {
                if (itemStack.getType().equals(Material.WRITTEN_BOOK)) {
                    BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
                    if (bookMeta.hasTitle()) {
                        if (bookMeta.getTitle().equalsIgnoreCase(ChatColor.GOLD + "Record of offers")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean playerInRegion(Player player) {
        ApplicableRegionSet set = Shop.shop.wgPlugin.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
        String regionName = Shop.shop.getConfig().getString("settings.location");
        for (ProtectedRegion pRegion : set) {
            if (pRegion.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }
        return false;
    }

    public static List<Offer> greaterThan(List<Offer> offerList, int price) {
        List<Offer> offers = new ArrayList<>();
        for (Offer offer : offerList) {
            if (offer.getOffer_price() > price) {
                offers.add(offer);
            }
        }
        return offers;
    }

    public static List<Offer> lessThan(List<Offer> offerList, int price) {
        List<Offer> offers = new ArrayList<>();
        for (Offer offer : offerList) {
            if (offer.getOffer_price() > price) {
                offers.add(offer);
            }
        }
        return offers;
    }

    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
