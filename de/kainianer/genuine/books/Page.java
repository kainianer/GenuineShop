package de.kainianer.genuine.books;

import de.kainianer.genuine.shop.Offer;
import java.util.List;
import org.bukkit.entity.Player;

public class Page {

    private final List<Offer> offerList;
    private final int startID;
    private final Player owner;
    private final boolean ownAuction;
    private final int maxOffers;

    public Page(List<Offer> offerList, int startID, Player owner, boolean ownAuction) {
        this.offerList = offerList;
        this.startID = startID;
        this.owner = owner;
        this.ownAuction = ownAuction;
        this.maxOffers = this.calculate(this.offerList, this.startID);
    }

    public boolean hasNextPage() {
        return this.offerList.size() > this.getLastID();
    }

    public List<Offer> getOfferList() {
        return this.offerList;
    }

    public Page getNextPage() {
        return new Page(this.offerList, this.getLastID(), this.owner, this.ownAuction);
    }

    public String formatPage() {
        String string = "";
        int max = this.getLastID();
        if (this.offerList.size() < max) {
            max = this.offerList.size();
        }
        for (int i = this.startID; i < max; i++) {
            Offer offer = this.offerList.get(i);
            if (i == this.startID) {
                if (offer.hasHeadline()) {
                    string += offer.getHeadline(i);
                } else {
                    string += offer.getHeadline(i) + "\n";
                }
            } else {
                if (offer.hasHeadline() || !(offer.getItemName().equalsIgnoreCase(offerList.get(i - 1).getItemName()))) {
                    if (offer.hasHeadline()) {
                        string += "\n" + offer.getHeadline(i);
                    } else {
                        string += "\n" + offer.getHeadline(i) + "\n";
                    }
                }
            }
            string += offer.formatOffer(this.ownAuction, this.owner, i + 1);

        }
        return string;
    }

    public int getLastID() {
        return this.startID + this.maxOffers;
    }

    private int calcMaxOffers() {
        int i = this.startID;

        while (i < this.offerList.size() && !this.offerList.get(i).isPotion() && !this.offerList.get(i).isEnchantable()) {
            i++;
        }

        if (i > this.startID + 5) {

            int j = 2;

            for (int k = this.startID + 1; k < i; k++) {
                Offer offer = this.offerList.get(k);
                if (offer.getItemName().equalsIgnoreCase(this.offerList.get(i - 1).getItemName())) {
                    j++;
                } else {
                    j += 3;
                }
            }

            return j;

        } else if (i == this.startID) {
            int rows = 14;
            while (i < this.offerList.size() && rows > 0) {
                rows = rows - this.getOfferList().get(i).getTakenRows();
                i++;
            }
            if (rows < 0) {
                return i - this.startID - 1;
            } else {
                return i - this.startID;
            }
        } else {
            return i - this.startID;
        }
    }

    private int calculate(List<Offer> offerList, int start) {
        int j = 0;
        for (int i = 14; i >= 0 && start + j < offerList.size(); j++) {
            Offer offer = offerList.get(start + j);
            if (offer.isEnchantable() || offer.isPotion()) {
                if (offer.isEnchantable()) {
                    if (offer.hasEnchants()) {
                        i = i - 2 - offer.getEnchantments().size();
                    } else {
                        i = i - 3;
                    }
                } else {
                    i = i - 3;
                }
            } else {
                if (j == start) {
                    i += -2;
                } else {
                    if (offerList.get(start + j).getItemName().equalsIgnoreCase(offerList.get(start + j - 1).getItemName())) {
                        i += -1;
                    } else {
                        i += -2;
                    }
                }
            }

            if (i < 0) {
                return j - 1;
            }
        }
        return j;
    }
}
