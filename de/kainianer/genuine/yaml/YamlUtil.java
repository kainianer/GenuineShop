package de.kainianer.genuine.yaml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import de.kainianer.genuine.Shop;
import de.kainianer.genuine.indi.Indi;
import de.kainianer.genuine.shop.Offer;

public class YamlUtil {

    public static void saveOffer(Offer offer) throws IOException {

        /*
         * Declaration
         */
        File shop = new File(Shop.shop.getDataFolder(), "/shop/shop.yml");
        FileConfiguration offers = YamlConfiguration.loadConfiguration(shop);
        int counter = offers.getInt("counter") + 1;
//		int counter = 0;
//		for(@SuppressWarnings("unused") String string : offers.getConfigurationSection("auction").getKeys(false)) {
//			counter++;
//		}

        File indiFile = new File(Shop.shop.getDataFolder(), "/shop/indi.yml");
        FileConfiguration indip = YamlConfiguration.loadConfiguration(indiFile);

        /*
         * Setting attributes
         */
        offers.set("counter", counter);
        offers.set("auction." + counter + ".itemStack", offer.getItemStack());
        offers.set("auction." + counter + ".price", offer.getOffer_price());
        offers.set("auction." + counter + ".amount", offer.getOffer_amount());
        offers.set("auction." + counter + ".seller", offer.getPlayer());

        Indi indi = new Indi(offer.getPlayer(), indip.getIntegerList(offer.getPlayer()));
        indi.getOfferList().add(counter);
        indip.set(indi.getName(), indi.getOfferList());

        /*
         * Saving to new file
         */
        offers.save(shop);
        indip.save(indiFile);
    }

    public static void deleteOffer(Offer offer) throws IOException {

        /*
         * Declaration
         */
        File shop = new File(Shop.shop.getDataFolder(), "/shop/shop.yml");
        FileConfiguration offers = YamlConfiguration.loadConfiguration(shop);

        File indiFile = new File(Shop.shop.getDataFolder(), "/shop/indi.yml");
        FileConfiguration indip = YamlConfiguration.loadConfiguration(indiFile);

        /*
         * Setting attributes
         */
        offers.set("auction." + offer.getOffer_id(), null);
        List<Integer> list = indip.getIntegerList(offer.getPlayer());

        /*
         * Removing from list
         */
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == offer.getOffer_id()) {
                list.remove(i);
            }
        }
        indip.set(offer.getPlayer(), list);

        /*
         * Saving to new file
         */
        offers.save(shop);
        indip.save(indiFile);
    }

    public static Offer readOffer(int offerid) {

        /*
         * Declaration
         */
        File shop = new File(Shop.shop.getDataFolder(), "/shop/shop.yml");
        FileConfiguration offers = YamlConfiguration.loadConfiguration(shop);

        /*
         * Getting attributes
         */
        ItemStack itemStack = offers.getItemStack("auction." + offerid + ".itemStack");
        int offer_price = offers.getInt("auction." + offerid + ".price");
        int offer_amount = offers.getInt("auction." + offerid + ".amount");
        String player = offers.getString("auction." + offerid + ".seller");

        /*
         * Returning offer got from attributes
         */
        return new Offer(itemStack, offerid, offer_price, offer_amount, player);
    }

    public static Indi getIndi(String name) {

        /*
         * Declaration
         */
        File indiFile = new File(Shop.shop.getDataFolder(), "/shop/indi.yml");
        FileConfiguration indip = YamlConfiguration.loadConfiguration(indiFile);

        /*
         * Getting attributes
         */
        List<Integer> list = indip.getIntegerList(name);

        /*
         * Returning indi got from attributes
         */
        return new Indi(name, list);
    }

    public static List<Offer> getOffersOfList(List<Integer> listInteger) {

        /*
         * Getting Offer-list from Integer-list of Indi
         */
        List<Offer> list = new ArrayList<Offer>();
        for (int i = 0; i < listInteger.size(); i++) {
            list.add(YamlUtil.readOffer(listInteger.get(i)));
        }

        /*
         * Returning list
         */
        return list;
    }

    public static List<Offer> getOffersItem(Material material) {

        /*
         * Declaration
         */
        File shop = new File(Shop.shop.getDataFolder(), "/shop/shop.yml");
        FileConfiguration offers = YamlConfiguration.loadConfiguration(shop);
        int counter = offers.getInt("counter");

        /*
         * Getting list
         */
        List<Offer> list = new ArrayList<Offer>();
        for (int i = 1; i <= counter; i++) {
            if (offers.contains("auction." + i)) {
                Offer offer = YamlUtil.readOffer(i);
                if (offer.getItemStack().getType().name().equalsIgnoreCase(material.name())) {
                    list.add(YamlUtil.readOffer(i));
                }
            }
        }

        /*
         * Returning list
         */
        return list;
    }

    public static boolean isAvailable(Offer offer) {

        /*
         * Declaration
         */
        File shop = new File(Shop.shop.getDataFolder(), "/shop/shop.yml");
        FileConfiguration offers = YamlConfiguration.loadConfiguration(shop);

        return offers.contains("auction." + String.valueOf(offer.getOffer_id()));
    }
}
