package de.kainianer.genuine.shop;

import de.kainianer.genuine.Shop;
import de.kainianer.genuine.util.ShopMethods;
import java.io.IOException;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import de.kainianer.genuine.yaml.YamlUtil;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Offer implements Comparable<Offer> {

    private ItemStack itemStack;
    private int offer_id;
    private int offer_price;
    private int offer_amount;
    private String playerName;
    private int rows = 1;

    public Offer(ItemStack itemStack, int offer_id, int offer_price, int offer_amount, String player) {
        this.itemStack = itemStack;
        this.offer_id = offer_id;
        this.offer_price = offer_price;
        this.offer_amount = offer_amount;
        this.playerName = player;
    }

    public Offer(ItemStack itemStack, int offer_price, int offer_amount, String player) {
        this.itemStack = itemStack;
        this.offer_price = offer_price;
        this.offer_amount = offer_amount;
        this.playerName = player;
    }

    public void saveOffer() {
        try {
            YamlUtil.saveOffer(this);
        } catch (IOException e) {
        }
    }

    public void deleteOffer() {
        try {
            YamlUtil.deleteOffer(this);
        } catch (IOException e) {
        }
    }

    public boolean isAvailable() {
        return YamlUtil.isAvailable(this);
    }

    public String getItemName() {
        return this.itemStack.getType().name().toLowerCase().replace('_', ' ');
    }

    public boolean isEnchantable() {
        for (Enchantment en : Enchantment.values()) {
            if (en.canEnchantItem(new ItemStack(this.itemStack.getType()))) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnchants() {
        return this.itemStack.getEnchantments().size() > 0;
    }

    public boolean isPotion() {
        return this.itemStack.getType().equals(Material.POTION);
    }

    public String getPlayer() {
        return playerName;
    }

    public void setPlayer(String player) {
        this.playerName = player;
    }

    public int getOffer_amount() {
        return offer_amount;
    }

    public void setOffer_amount(int offer_amount) {
        this.offer_amount = offer_amount;
    }

    public int getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(int offer_price) {
        this.offer_price = offer_price;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void extendRows() {
        this.rows++;
    }

    public boolean hasHeadline() {
        return this.isPotion() || this.isEnchantable();
    }

    public String getHeadline(int i) {
        if (this.isEnchantable()) {
            return ChatColor.DARK_GRAY + "" + (i + 1) + ". " + ChatColor.DARK_GREEN + this.getItemName() + " " + this.formatDurablity();
        } else if (this.isPotion()) {
            Potion potion = Potion.fromItemStack(this.getItemStack());
            PotionType pt = potion.getType();
            return ChatColor.DARK_GRAY + "" + (i + 1) + ". " + ChatColor.DARK_GREEN + "potion - "
                    + pt.getEffectType().getName().toLowerCase().replace('_', ' ').replace("resistance", "res.").replace("instant", "").replace("invisibility", "invis.").replace("night", "").replace("increase damage", "strength").replace("regeneration", "regen.")
                    + " " + potion.getLevel();
        } else {
            return ChatColor.DARK_GRAY + "= " + ChatColor.DARK_GREEN + this.getItemName();
        }
    }

    public int getTakenRows() {
        if (this.hasEnchants()) {
            return 3 + this.getEnchantments().size();
        } else {
            return 4;
        }
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return this.itemStack.getEnchantments();
    }

    public String formatOffer(boolean ownAuctions, Player player, int listID) {
        String string = "";
        if (ownAuctions) {
            if (this.isEnchantable()) {
                if (this.getEnchantments().isEmpty()) {
                    string += ChatColor.DARK_GRAY + "* no enchantments\n";
                } else {
                    for (Enchantment ench : this.getEnchantments().keySet()) {
                        string += ChatColor.DARK_GRAY + "* " + ench.getName().replace('_', ' ').toLowerCase() + ": "
                                + ChatColor.GRAY + this.getEnchantments().get(ench) + "\n";
                    }
                    string = string.replace("damage", "dmg.").replace("protection", "pro.").replace("arrow", "arr.");
                }
                string += ChatColor.DARK_GRAY + "* " + ChatColor.GREEN + Shop.shop.economy.format(this.offer_amount * this.getOffer_price()) + "\n";
            } else if (this.isPotion()) {
                Potion potion = Potion.fromItemStack(this.itemStack);
                PotionType type = potion.getType();
                string += ChatColor.DARK_GRAY + "\n* duration: " + ChatColor.GRAY + type.getEffectType().getDurationModifier() + " min"
                        + ChatColor.DARK_GRAY + "\n* splash: " + ChatColor.GRAY + potion.isSplash()
                        + ChatColor.DARK_GRAY + "\n* " + ChatColor.GREEN + Shop.shop.economy.format(this.offer_price) + "\n";
            } else {
                string += ChatColor.DARK_GRAY + "" + listID + ". " + "* " + ChatColor.GREEN
                        + Shop.shop.economy.format(this.getOffer_amount() * this.getOffer_price())
                        + ChatColor.GRAY + " (" + Shop.shop.economy.format(this.offer_price) + " x " + this.getOffer_amount() + ")\n";
            }
        } else {
            ChatColor color = ChatColor.DARK_GRAY;
            if (this.getPlayer().equalsIgnoreCase(player.getName())) {
                color = ChatColor.RED;
            }

            if (this.isEnchantable()) {
                if (this.getEnchantments().isEmpty()) {
                    string += color + "* " + ChatColor.DARK_GRAY + "no enchantments\n";
                } else {
                    for (Enchantment ench : this.getEnchantments().keySet()) {
                        string += color + "* " + ChatColor.DARK_GRAY + ench.getName().replace('_', ' ').toLowerCase() + ": "
                                + ChatColor.GRAY + this.getEnchantments().get(ench) + "\n";
                    }
                    string = string.replace("damage", "dmg.").replace("protection", "pro.").replace("arrow", "arr.");
                }
                string += color + "* " + ChatColor.GREEN + ShopMethods.formatPrice(this.offer_price, player) + "\n";
            } else if (this.isPotion()) {
                Potion potion = Potion.fromItemStack(this.itemStack);
                PotionType type = potion.getType();
                string += color + "\n* " + ChatColor.DARK_GRAY + "duration: " + ChatColor.GRAY + type.getEffectType().getDurationModifier() + " min"
                        + color + "\n* " + ChatColor.DARK_GRAY + "splash: " + ChatColor.GRAY + potion.isSplash()
                        + color + "\n* " + ShopMethods.formatPrice(this.offer_price, player) + "\n";
            } else {
                string += ChatColor.DARK_GRAY + "" + listID + ". " + color + "* "
                        + ChatColor.GREEN + ShopMethods.formatPrice(this.offer_price * this.offer_amount, player)
                        + ChatColor.GRAY + " (" + Shop.shop.economy.format(this.offer_price) + " x " + this.getOffer_amount() + ")\n";
            }
        }
        return string;
    }

    public String formatDurablity() {
        int durability = ((this.getItemStack().getType().getMaxDurability() - this.getItemStack().getDurability()) * 100) / this.getItemStack().getType().getMaxDurability();
        if (durability > 75) {
            return ChatColor.GREEN + "" + ChatColor.BOLD + "||||\n" + ChatColor.RESET;
        } else if (durability > 50) {
            return ChatColor.GOLD + "" + ChatColor.BOLD + "|||" + ChatColor.GRAY + ChatColor.BOLD + "|\n" + ChatColor.RESET;
        } else if (durability > 25) {
            return ChatColor.RED + "" + ChatColor.BOLD + "||" + ChatColor.GRAY + ChatColor.BOLD + "||\n" + ChatColor.RESET;
        } else {
            return ChatColor.DARK_RED + "" + ChatColor.BOLD + "|" + ChatColor.GRAY + ChatColor.BOLD + "|||\n" + ChatColor.RESET;
        }
    }

    @Override
    public int compareTo(Offer offer) {
        return this.getItemStack().getType().compareTo(offer.getItemStack().getType());
    }
}
