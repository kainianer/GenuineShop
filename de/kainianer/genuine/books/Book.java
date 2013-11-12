package de.kainianer.genuine.books;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import de.kainianer.genuine.indi.Indi;
import de.kainianer.genuine.shop.Offer;
import de.kainianer.genuine.yaml.YamlUtil;
import java.util.Collections;
import java.util.HashMap;

public class Book {

    public List<Offer> offerList = new ArrayList<>();
    public Material material;
    public Player owner;
    public boolean enchantable;
    public boolean ownAuction;

    public Book(Material material, Player owner) {
        this.material = material;
        this.offerList = YamlUtil.getOffersItem(material);
        this.owner = owner;
        this.enchantable = this.isEnchantable();
        this.ownAuction = false;
    }

    public Book(Player owner) {
        Indi indi = new Indi(owner.getName());
        this.offerList = indi.getOffers();
        this.owner = owner;
        this.ownAuction = true;
    }

    public final boolean isEnchantable() {
        for (Enchantment en : Enchantment.values()) {
            if (en.canEnchantItem(new ItemStack(this.material))) {
                return true;
            }
        }
        return false;
    }

    public List<Offer> getOfferList() {
        return this.offerList;
    }

    public List<Offer> getSortedOfferList() {
        Collections.sort(this.offerList);
        return this.offerList;
    }

    public boolean ownAuction() {
        return this.ownAuction;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void updateList() {
        if (this.ownAuction) {
            Indi indi = new Indi(this.owner.getName());
            this.offerList = indi.getOffers();
        } else {
            this.offerList = YamlUtil.getOffersItem(material);
        }
    }

    public ItemStack getBook() {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        stack.setItemMeta(this.getMetaData());
        return stack;
    }

    public static ItemMeta getEmptyMetaData() {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) stack.getItemMeta();
        meta.setPages(ChatColor.RED + "You haven't done any search the book can refer to.");
        meta.setTitle(ChatColor.GOLD + "Record of offers");
        return meta;
    }

    public ItemMeta getMetaData() {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) stack.getItemMeta();
        meta.setPages(this.formatBook());
        meta.setTitle(ChatColor.GOLD + "Record of offers");
        return meta;
    }

    public List<String> formatBook() {
        List<String> list = new ArrayList<>();
        List<Offer> sortedList = this.getSortedOfferList();
        if (sortedList.isEmpty()) {
            if (this.ownAuction) {
                list.add(ChatColor.GOLD + "You don't have any open offers.");
            } else {
                list.add(ChatColor.GOLD + "There aren't any open offers for that item.");
            }
            return list;
        } else {
            Page countPage = new Page(sortedList, 0, this.owner, this.ownAuction);
            Page oldPage;
            do {
                oldPage = countPage;
                list.add(countPage.formatPage());
                if (countPage.hasNextPage()) {
                    countPage = countPage.getNextPage();
                }
            } while (oldPage.hasNextPage());
            return list;
        }
    }

    public List<Offer> sortList(List<Offer> offerList) {
        HashMap<Material, List<Offer>> materialOfferMap = new HashMap<>();
        for (Offer offer : offerList) {
            if (materialOfferMap.get(offer.getItemStack().getType()) == null) {
                materialOfferMap.put(offer.getItemStack().getType(), new ArrayList<Offer>());
                materialOfferMap.get(offer.getItemStack().getType()).add(offer);
            } else {
                materialOfferMap.get(offer.getItemStack().getType()).add(offer);
            }
        }
        offerList.clear();
        for (Material mat : materialOfferMap.keySet()) {
            for (Offer offer : materialOfferMap.get(mat)) {
                offerList.add(offer);
            }
        }
        return offerList;
    }
}
