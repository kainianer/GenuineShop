package de.kainianer.genuine.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.kainianer.genuine.Shop;
import de.kainianer.genuine.books.Book;
import de.kainianer.genuine.shop.Offer;
import de.kainianer.genuine.util.ShopMethods;

public class CommandShop implements CommandExecutor {

    private final Shop shop;
    private final double sellFee;
    private final double createFee;

    public CommandShop(Shop shop) {
        this.shop = shop;
        this.sellFee = shop.getConfig().getDouble("settings.sellFee") / 100;
        this.createFee = shop.getConfig().getDouble("settings.createFee") / 100;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be performed ingame!");
            return true;
        } else {
            Player player = (Player) sender;
            if (this.shop.perms.has(player, "genuine.shop") || player.isOp()) {

                /*
                 * Is the player in region (when certain region has been set)
                 */
                if (this.shop.getConfig().getBoolean("settings.bookInRegion")) {
                    if (!ShopMethods.playerInRegion(player)) {
                        player.sendMessage(ChatColor.RED + "You aren't at the right spot to use that command.");
                        return true;
                    }
                }

                if (args.length == 0) {
                    if (!ShopMethods.containsBook(player.getInventory().getContents())) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(ChatColor.RED + "You don't have any empty space in your inventory!");
                            return true;
                        } else {
                            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                            book.setItemMeta(Book.getEmptyMetaData());
                            player.getInventory().addItem(book);
                            player.sendMessage(ChatColor.GOLD + "You were given a book!");
                            return true;
                        }

                    } else {
                        player.sendMessage(ChatColor.RED + "You already have a book in your inventory!");
                        return true;
                    }
                } else if (args.length < 1 && (!args[0].equalsIgnoreCase("mine") || !args[0].equalsIgnoreCase("info") || !args[0].equalsIgnoreCase("toggle") || !args[0].equalsIgnoreCase("help"))) {
                    player.sendMessage(ShopMethods.shopHelp());
                    return true;
                } else if (args[0].equalsIgnoreCase("mine")) {

                    /*
                     * Declaring new book which is put into map
                     */
                    this.shop.playerSearch.put(player.getName(), new Book(player));
                    player.sendMessage(ChatColor.GOLD + "Searching for own offers...");
                    return true;

                } else if (args[0].equalsIgnoreCase("search")) {

                    /*
                     * Has entered an id?
                     */
                    if (args.length <= 1) {
                        player.sendMessage(ChatColor.RED + "You have to enter an id!");
                        return true;
                    }

                    /*
                     * Trying to parse an id or name
                     */
                    Material material = Material.matchMaterial(args[1]);
                    if (material == null) {
                        player.sendMessage(ChatColor.RED + "Could not find item with or id " + args[1] + "!");
                        return true;
                    }

                    /*
                     * Setting last search from material
                     */
                    this.shop.playerSearch.put(player.getName(), new Book(material, player));
                    player.sendMessage(ChatColor.GOLD + "Searching for " + material.name().toLowerCase().replace('_', ' ') + "...");

                    return true;

                } else if (args[0].equalsIgnoreCase("sell")) {

                    /*
                     * Has too many auctions
                     */
                    if (ShopMethods.hasTooManyAuctions(player)) {
                        player.sendMessage(ChatColor.RED + "You have already created to many offer!");
                        return true;
                    }

                    /*
                     * Is item blacklisted?
                     */
                    if (ShopMethods.isBlacklisted(player.getItemInHand().getType())) {
                        player.sendMessage(ChatColor.RED + "This item has been blacklisted.");
                        return true;
                    }

                    /*
                     * Has an item in hand?
                     */
                    if (player.getItemInHand().getType().equals(Material.AIR)) {
                        player.sendMessage(ChatColor.RED + "You have to have an item in your hand!");
                        return true;
                    } else {

                        /*
                         * Declaring price and parsing from arguments
                         */
                        int price;
                        try {
                            price = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "You have to enter a price!");
                            return true;
                        }

                        /*
                         * Has player enough to pay fee
                         */
                        if (!shop.economy.has(player.getName(), price * createFee)) {
                            player.sendMessage(ChatColor.RED + "You don't have enough money to create the offer!");
                            return true;
                        }

                        /*
                         * Saving the offer
                         */
                        Offer offer = new Offer(player.getItemInHand(), price, player.getItemInHand().getAmount(), player.getName());
                        offer.saveOffer();
                        price = price * offer.getOffer_amount();
                        player.getInventory().setItemInHand(new ItemStack(Material.AIR));
                        this.shop.economy.withdrawPlayer(player.getName(), price * createFee);
                        player.sendMessage(ChatColor.GREEN + "Offer created!" + ChatColor.GRAY + " (" + this.shop.economy.format(price * createFee) + " taken as fee)");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("buy")) {

                    /*
                     * Player has searched for an item before
                     */
                    if (!this.shop.playerSearch.containsKey(player.getName()) || this.shop.playerSearch.get(player.getName()).ownAuction()) {
                        player.sendMessage(ChatColor.RED + "There is no search to refer to!");
                        return true;
                    }

                    /*
                     * Declaring list of offers
                     */
                    List<Offer> offers = this.shop.playerSearch.get(player.getName()).getSortedOfferList();
                    int id;

                    /*
                     * Trying to parse id from args / added subcommands for specific search
                     */
                    if (ShopMethods.isNumber(args[1])) {
                        id = Integer.parseInt(args[1]);
                    } else {
                        player.sendMessage(ChatColor.RED + "The id has to be a number!");
                        return true;
                    }

                    if (id >= offers.size()) {
                        player.sendMessage(ChatColor.RED + "The id you entered extends the number of offers!");
                        return true;
                    }

                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.RED + "There is no empty space in your inventory!");
                        return true;
                    }

                    /*
                     * Getting offer from id, getting price of whole offer amount*pricepi
                     */
                    Offer offer = offers.get(id);
                    int price = offer.getOffer_amount() * offer.getOffer_price();

                    /*
                     * Checking if offer has already been bought or cancelled
                     */
                    if (offer.isAvailable()) {
//						if(offer.getPlayer().equalsIgnoreCase(player.getName())) {
//							player.sendMessage("You can't buy your own offers!");
//							return true;
//						}

                        /*
                         * Player has enough money
                         */
                        if (this.shop.economy.has(player.getName(), price)) {

                            /*
                             * Add item to inventory and withdraw/deposit money, delete offer
                             */
                            player.getInventory().addItem(offer.getItemStack());
                            this.shop.economy.withdrawPlayer(player.getName(), price);
                            this.shop.economy.depositPlayer(offer.getPlayer(), price * sellFee);
                            player.sendMessage(ChatColor.GREEN + "You bought " + ChatColor.GRAY + offer.getOffer_amount() + " x " + offer.getItemName() + ChatColor.GREEN + " for "
                                    + ChatColor.GRAY + this.shop.economy.format(price) + ChatColor.GREEN + "!");

                            if (Bukkit.getPlayer(offer.getPlayer()).isOnline()) {
                                Bukkit.getPlayer(offer.getPlayer()).sendMessage(ChatColor.GREEN + "+ " + this.shop.economy.format(price - (price * sellFee))
                                        + ChatColor.GRAY + " -" + ChatColor.GOLD + " You sold: " + offer.getOffer_amount() + " x " + offer.getItemName());
                            }

                            offer.deleteOffer();

                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have enough money!");
                            return true;
                        }

                    } else {
                        player.sendMessage(ChatColor.RED + "Offer is no longer available!");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("cancel")) {

                    /*
                     * Declaring list of offers from player
                     */
                    Book indiBook = new Book(player);
                    List<Offer> list = indiBook.getSortedOfferList();

                    int id;
                    if (list.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "You don't have any offers!");
                        return true;
                    }

                    /*
                     * Parsing id from args
                     */
                    if (ShopMethods.isNumber(args[1])) {
                        id = Integer.parseInt(args[1]) - 1;
                    } else {
                        String subCommand = args[1];
                        if (!subCommand.isEmpty() && subCommand.equalsIgnoreCase("all")) {
                            for (Offer offer : list) {
                                if (player.getInventory().firstEmpty() != -1) {
                                    player.getInventory().addItem(offer.getItemStack());
                                    offer.deleteOffer();
                                } else {
                                    player.sendMessage(ChatColor.RED + "Some offer may haven't been cancelled.");
                                    return true;
                                }
                            }
                            player.sendMessage(ChatColor.GREEN + "All offer have been cancelled.");
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "You have to enter an id or a command.");
                            return true;
                        }
                    }

                    /*
                     * If player wants to cancel all
                     */
                    if (id >= list.size()) {
                        player.sendMessage(ChatColor.RED + "Id is higher than the amount of offers.");
                        return true;
                    }

                    /*
                     * Is available
                     */
                    if (!list.get(id).isAvailable()) {
                        player.sendMessage(ChatColor.RED + "Offer is no longer available!");
                        return true;
                    }

                    /*
                     * Adding item to inventory and cancelling offer
                     */
                    player.getInventory().addItem(list.get(id).getItemStack());
                    list.get(id).deleteOffer();
                    player.sendMessage(ChatColor.GREEN + "Offer was cancelled!");

                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    player.sendMessage(ChatColor.DARK_GRAY + "=" + ChatColor.DARK_GREEN + " GenuineShop \n" + ChatColor.WHITE + "Created by: "
                            + ChatColor.GRAY + this.shop.getDescription().getAuthors().get(0) + ChatColor.WHITE + "\n"
                            + "Version: " + ChatColor.GRAY + shop.getDescription().getVersion());
                    return true;
                } else {
                    player.sendMessage(ShopMethods.shopHelp());
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to perform this command!");
                return true;
            }
        }
    }
}
