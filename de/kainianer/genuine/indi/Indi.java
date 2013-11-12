package de.kainianer.genuine.indi;

import java.util.List;

import de.kainianer.genuine.Shop;
import de.kainianer.genuine.shop.Offer;
import de.kainianer.genuine.yaml.YamlUtil;

public class Indi {

    private final List<Integer> offerList;
    private final String name;
    private int offerAmount;

    public Indi(String name) {
        this.name = name;
        this.offerList = YamlUtil.getIndi(this.name).getOfferList();
        this.offerAmount = offerList.size();
    }

    public Indi(String name, List<Integer> offerList) {
        this.name = name;
        this.offerList = offerList;
    }

    public List<Integer> getOfferList() {
        return offerList;
    }

    public String getName() {
        return name;
    }

    public int getOfferAmount() {
        return this.offerAmount;
    }

    public List<Offer> getOffers() {
        return YamlUtil.getOffersOfList(this.offerList);
    }

    public boolean hasTooManyAuctions() {
        return Shop.shop.getConfig().getInt("settings.maxAuctions") <= this.getOfferAmount();
    }

    /*
     * Economy
     */
    public boolean hasEnough(double amount) {
        return Shop.shop.economy.has(this.getName(), amount);
    }

    public void removeMoney(double amount) {
        Shop.shop.economy.bankWithdraw(this.getName(), amount);
    }

    public void addMoney(double amount) {
        Shop.shop.economy.bankDeposit(this.getName(), amount);
    }

}
