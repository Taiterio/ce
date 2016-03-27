package com.taiter.ce;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

import net.milkbowl.vault.economy.EconomyResponse;

public class CEListener implements Listener {

    HashSet<CBasic> move = new HashSet<CBasic>();
    HashSet<CBasic> interact = new HashSet<CBasic>();
    HashSet<CBasic> interactE = new HashSet<CBasic>();
    HashSet<CBasic> interactR = new HashSet<CBasic>();
    HashSet<CBasic> interactL = new HashSet<CBasic>();
    HashSet<CBasic> damageTaken = new HashSet<CBasic>();
    HashSet<CBasic> damageGiven = new HashSet<CBasic>();
    HashSet<CBasic> damageNature = new HashSet<CBasic>();
    HashSet<CBasic> shootBow = new HashSet<CBasic>();
    HashSet<CBasic> projectileThrow = new HashSet<CBasic>();
    HashSet<CBasic> projectileHit = new HashSet<CBasic>();
    HashSet<CBasic> death = new HashSet<CBasic>();
    HashSet<CBasic> blockPlaced = new HashSet<CBasic>();
    HashSet<CBasic> blockBroken = new HashSet<CBasic>();
    HashSet<CBasic> wearItem = new HashSet<CBasic>();

    private boolean useRuneCrafting = Main.plugin.getConfig().getBoolean("Global.Runecrafting.Enabled");

    /*
     * Almost all priorities are set to Monitor, the highest priority possible,
     * which allows CE to check if an event has been cancelled by any kind of
     * plugin beforehand.
     *
     * This is due to the fact that CE rarely cancels Events itself; with the
     * addition of all events ignoring cancelled events this becomes apparent.
     *
     * If it is needed for an effect to cancel an event however, the priority
     * HIGHEST is used, as cancelling the event this late fits best for not
     * interfering with other plugins too much.
     *
     */

    // Inventory menu
    // PREVENTION of taking items out
    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMenuPrevention(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getTitle().startsWith("CE"))
            event.setCancelled(true);
        else if (useRuneCrafting)
            if (event.getView().getTopInventory().getName().equals(
                    ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba")) {
                CEventHandler.updateRunecraftingInventory(event.getInventory());
                return;
            }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMenuPrevention(InventoryCreativeEvent event) {
        if (event.getView().getTopInventory().getTitle().startsWith("CE"))
            event.setCancelled(true);
    }
    // PREVENTION

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getName()
                .equals(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba")) {
            ItemStack[] contents = event.getInventory().getContents();
            HumanEntity p = event.getPlayer();
            Location loc = p.getLocation().add(0, 1.25, 0);
            Vector velocity = loc.getDirection().multiply(0.25);
            if (contents[0] != null && !contents[0].getType().equals(Material.AIR))
                if (p.getInventory().firstEmpty() == -1)
                    p.getWorld().dropItem(loc, contents[0]).setVelocity(velocity);
                else
                    p.getInventory().addItem(contents[0]);
            if (contents[1] != null && !contents[1].getType().equals(Material.AIR))
                if (p.getInventory().firstEmpty() == -1)
                    p.getWorld().dropItem(loc, contents[1]).setVelocity(velocity);
                else
                    p.getInventory().addItem(contents[1]);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMenu(final InventoryClickEvent event) {
        if (event.getSlot() == -999 || event.getSlot() == -1) //No inventory was clicked
            return;
        
        if (useRuneCrafting)
            if (event.getView().getTopInventory().getName().equals(
                    ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba")) {
                CEventHandler.handleRunecrafting(event);
                return;
            }

        //Block custom enchantment books being used in anvils
        if (event.getView().getTopInventory().getType().equals(InventoryType.ANVIL)) {
            ItemStack toTest = event.getCurrentItem();
            if (!event.getClick().toString().contains("SHIFT"))
                if (event.getRawSlot() <= 1)
                    toTest = event.getCursor();
                else
                    return;
            if (toTest != null && !toTest.getType().equals(Material.AIR) && toTest.hasItemMeta()) {
                if (EnchantManager.isEnchantmentBook(toTest))
                    event.getWhoClicked().sendMessage(ChatColor.RED + "The book is being repulsed by the Anvil");
                else if (EnchantManager.hasEnchantments(toTest))
                    event.getWhoClicked().sendMessage(ChatColor.RED + "The item is being repulsed by the Anvil");
                else
                    return;
                event.setCancelled(true);
            }
            return;
        }

        // -------Armor wear handling--------
        if (event.getView().getTopInventory().getType().equals(InventoryType.CRAFTING) || event.getView().getTopInventory().getType().equals(InventoryType.CREATIVE))
            if (event.getSlotType() == SlotType.ARMOR && event.getClick() != ClickType.DOUBLE_CLICK) {
                CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCurrentItem(), true, event);
                CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCursor(), false, event);
                if (event.getCursor() == null)
                    CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCurrentItem(), false, event);

            } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                ItemStack current = event.getCurrentItem();
                String typeS = current.getType().toString();
                PlayerInventory inv = event.getWhoClicked().getInventory();
                if ((typeS.endsWith("HELMET") && inv.getHelmet() == null) || (typeS.endsWith("CHESTPLATE") && inv.getChestplate() == null) || (typeS.endsWith("LEGGINGS") && inv.getLeggings() == null)
                        || (typeS.endsWith("BOOTS") && inv.getBoots() == null))
                    CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCurrentItem(), false, event);
            }
        // ---------------------------------

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;

        if (event.getView().getTopInventory().getTitle().startsWith("CE")) {
            Inventory topInv = event.getView().getTopInventory();
            final Player p = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            event.setCancelled(true);

            // This is the back-button, located in the very last spot of each
            // inventory
            if ((event.getRawSlot() == topInv.getSize() - 1)) {
                p.closeInventory();
                p.openInventory(Tools.getPreviousInventory(topInv.getTitle()));
                return;
            }

            if (event.getRawSlot() < topInv.getSize()) {

                // Opens the clicked Enchantments inventory and loads the
                // permissions if needed
                if (topInv.getTitle().equals(Tools.prefix + "Enchantments")) {
                    p.closeInventory();
                    p.openInventory(Tools.getEnchantmentMenu(p, clickedItem.getItemMeta().getDisplayName()));
                    return;
                }

                // Opens the item inventory and loads the permissions if needed
                if (topInv.getTitle().equals(Tools.prefix + "Main Menu"))
                    if (event.getRawSlot() == 4) {
                        p.closeInventory();
                        p.openInventory(Tools.getItemMenu(p));
                        return;
                    } else if (event.getRawSlot() == 6) {
                        if (p.hasPermission("ce.*") || p.hasPermission("ce.runecrafting")) {
                            p.closeInventory();
                            p.openInventory(Tools.getNextInventory(clickedItem.getItemMeta().getDisplayName()));
                            return;
                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have permission to use this!");
                            return;
                        }
                    }

                // These are the specific menus, clicking one of them will lead
                // to the enchanting menu, which needs to be 'notified' of the
                // enchantment to give and it's cost
                if (topInv.getTitle().equals(Tools.prefix + "Global") || topInv.getTitle().equals(Tools.prefix + "Bow") || topInv.getTitle().equals(Tools.prefix + "Armor")
                        || topInv.getTitle().equals(Tools.prefix + "Helmet") || topInv.getTitle().equals(Tools.prefix + "Boots") || topInv.getTitle().equals(Tools.prefix + "Tool"))
                    if (p.isOp() || Tools.checkPermission(EnchantManager.getEnchantment(clickedItem.getItemMeta().getDisplayName()), p)) {
                        Inventory levelMenu = Bukkit.createInventory(p, 9, Tools.prefix + "Level selection");

                        ItemStack backButton = new ItemStack(Material.NETHER_STAR);

                        ItemMeta tempMeta = backButton.getItemMeta();
                        List<String> tempLore = new ArrayList<String>();

                        tempMeta.setDisplayName(ChatColor.AQUA + "Back");
                        backButton.setItemMeta(tempMeta);

                        levelMenu.setItem(8, backButton);

                        tempMeta = clickedItem.getItemMeta();
                        String enchName = tempMeta.getDisplayName();
                        CEnchantment ce = EnchantManager.getEnchantment(enchName + " I");

                        for (int i = 1; i <= ce.getEnchantmentMaxLevel(); i++) {
                            if (i > 5)
                                break;
                            
                            double cost = ce.getCost(i);
                            tempLore.clear();
                            if (Main.hasEconomy && cost > 0) {
                                tempLore.add("");
                                tempLore.add(ChatColor.GRAY + "Cost: " + ChatColor.WHITE + ChatColor.BOLD + cost);
                            }
                            
                            ItemStack newItem = clickedItem.clone();
                            String fullName = enchName + " " + EnchantManager.intToLevel(i);
                            tempMeta.setDisplayName(fullName);
                            tempMeta.setLore(tempLore);
                            newItem.setItemMeta(tempMeta);
                            levelMenu.setItem(i - 1, newItem);
                        }
                        p.closeInventory();
                        p.openInventory(levelMenu);
                        return;
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have permission to buy this Enchantment.");
                        return;
                    }

                if (topInv.getTitle().equals(Tools.prefix + "Items")) {
                    CItem ci = Tools.getItemByDisplayname(clickedItem.getItemMeta().getDisplayName());
                    if (!p.hasPermission("ce.item.*") && !p.hasPermission("ce.item." + ci.getPermissionName())) {
                        p.sendMessage(ChatColor.RED + "You do not have permission to buy this Item!");
                        return;
                    }

                    if (p.getInventory().firstEmpty() != -1) {
                        double cost = ci.getCost();
                        //Check cost
                        if (Main.hasEconomy && !p.isOp() && cost > 0) {
                            if (Main.econ.getBalance(p.getName()) >= cost) {
                                EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                                if (ecr.transactionSuccess()) {
                                    p.sendMessage(ChatColor.GREEN + "Purchased " + clickedItem.getItemMeta().getDisplayName() + "" + ChatColor.GREEN + " for " + ChatColor.WHITE + cost + "  "
                                            + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()) + ChatColor.GREEN + "!");
                                    ItemMeta im = clickedItem.getItemMeta();
                                    im.setLore(ci.getDescription());
                                    clickedItem.setItemMeta(im);
                                } else {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An economy error has occured:");
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + ecr.errorMessage);
                                    p.closeInventory();
                                    return;
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "You do not have enough money to buy this!");
                                return;
                            }
                        } else {
                            p.sendMessage(ChatColor.GREEN + "Created " + clickedItem.getItemMeta().getDisplayName() + "" + ChatColor.GREEN + "!");
                        }

                        p.getInventory().addItem(clickedItem);
                        p.closeInventory();
                        return;
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough space in your inventory!");
                        return;
                    }
                }

                if (topInv.getTitle().equals(Tools.prefix + "Level selection")) {
                    String enchantmentName = clickedItem.getItemMeta().getDisplayName();
                    CEnchantment ce = EnchantManager.getEnchantment(enchantmentName);
                    int level = EnchantManager.getLevel(enchantmentName);
                    double cost = ce.getCost(level);

                    if (p.getInventory().firstEmpty() != -1) {
                        //Check cost
                        if (Main.hasEconomy && !p.isOp() && cost > 0) {
                            if (Main.econ.getBalance(p.getName()) >= cost) {
                                EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                                if (ecr.transactionSuccess()) {
                                    p.sendMessage(ChatColor.GREEN + "Purchased " + clickedItem.getItemMeta().getDisplayName() + "" + ChatColor.GREEN + " for " + ChatColor.WHITE + cost + " "
                                            + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()) + ChatColor.GREEN + "!");
                                } else {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An economy error has occured:");
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + ecr.errorMessage);
                                    p.closeInventory();
                                    return;
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "You do not have enough money to buy this!");
                                return;
                            }
                        } else {
                            p.sendMessage(ChatColor.GREEN + "Created " + clickedItem.getItemMeta().getDisplayName() + "" + ChatColor.GREEN + "!");
                        }

                        p.getInventory().addItem(EnchantManager.getEnchantBook(ce, level));
                        p.closeInventory();
                        return;
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have enough space in your inventory!");
                        return;
                    }
                }
            }

            if (event.getRawSlot() < topInv.getSize()) {

                p.closeInventory();
                try {
                    p.openInventory(Tools.getNextInventory(clickedItem.getItemMeta().getDisplayName()));
                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "This feature is not yet implemented.");
                }

            }

        }

    }

    // ENTITIES: org.bukkit.event.entity

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void antiArrowSpam(ProjectileHitEvent event) {

        // Destroys the Arrows of the Minigun
        if (event.getEntityType().equals(EntityType.ARROW)) {
            Arrow arrow = (Arrow) event.getEntity();
            ProjectileSource shooter = arrow.getShooter();
            if (shooter instanceof Player)
                if (arrow.hasMetadata("ce.minigunarrow"))
                    if (((Player) shooter).getGameMode().equals(GameMode.CREATIVE))
                        arrow.remove();
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {

        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();
        

        // Block self damaging (Enderpearls cause weird behavior)
        if (damager.getUniqueId().equals(damaged.getUniqueId()))
            return;

        if (damaged instanceof Player)
            CEventHandler.handleEvent((Player) damaged, e, damageTaken);

        if (damager instanceof Player)
            CEventHandler.handleEvent((Player) damager, e, damageGiven);
        else if (damager instanceof Arrow)
            if (damager.hasMetadata("ce.bow.item") || damager.hasMetadata("ce.bow.enchantment"))
                CEventHandler.handleBows((Player) ((Projectile) damager).getShooter(), e);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityDamageEvent(EntityDamageEvent e) {

        Entity damaged = e.getEntity();

        if (damaged instanceof Player) {

            CEventHandler.handleEvent((Player) damaged, e, damageNature);

            if (damaged.hasMetadata("ce.springs")) {
                e.setCancelled(true);
                Vector vel = damaged.getVelocity();
                vel.setY((vel.getY() * -0.75) > 1 ? vel.getY() * -0.75 : 0);
                damaged.setVelocity(vel);
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityExplodeEvent(EntityExplodeEvent e) {

        if (e.getEntity() != null && e.getEntity().hasMetadata("ce.explosive")) {
            e.getEntity().remove();
            e.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityShootBowEvent(EntityShootBowEvent e) {

        Entity shooter = e.getEntity();

        if (shooter instanceof Player)
            CEventHandler.handleEvent((Player) shooter, e, shootBow);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileHitEvent(ProjectileHitEvent e) {

        ProjectileSource shooter = e.getEntity().getShooter();

        if (shooter instanceof Player) {
            if (e.getEntity().hasMetadata("ce.projectile.item")) {
                CItem ci = Tools.getItemByOriginalname(e.getEntity().getMetadata("ce.projectile.item").get(0).asString());
                if (ci != null)
                    ci.effect(e, (Player) shooter);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileLaunchEvent(ProjectileLaunchEvent e) {

        ProjectileSource shooter = e.getEntity().getShooter();

        if (shooter instanceof Player)
            CEventHandler.handleEvent((Player) shooter, e, projectileThrow);

    }

    // PLAYER

    @EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("ce.Volley")) {
            event.getItem().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        CEventHandler.handleEvent(p, e, interact);

        if (e.getAction().toString().startsWith("LEFT"))
            CEventHandler.handleEvent(p, e, interactL);
        else if (e.getAction().toString().startsWith("RIGHT")) {
            CEventHandler.handleEvent(p, e, interactR);

            //Check for runecrafting
            if (useRuneCrafting)
                if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.ANVIL)) {
                    ItemStack i = p.getItemInHand();
                    if (EnchantManager.hasEnchantments(i) || EnchantManager.isEnchantmentBook(i)) {
                        if (!p.hasPermission("ce.*") && !p.hasPermission("ce.runecrafting"))
                            return;
                        e.setCancelled(true);
                        p.setItemInHand(new ItemStack(Material.AIR));
                        Inventory einv = Bukkit.createInventory(p, InventoryType.FURNACE, ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE
                                + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba");
                        einv.setContents(new ItemStack[] { new ItemStack(Material.AIR), i, new ItemStack(Material.AIR) });
                        p.openInventory(einv);
                        return;
                    }
                }

            // Check if the player has put armor on by rightclicking
            if (p.getItemInHand().getType() != Material.AIR) {
                ItemStack i = p.getItemInHand();
                String mat = i.getType().toString();
                PlayerInventory inv = p.getInventory();
                if ((mat.endsWith("BOOTS") && inv.getBoots() == null) || (mat.endsWith("LEGGINGS") && inv.getLeggings() == null) || (mat.endsWith("CHESTPLATE") && inv.getChestplate() == null)
                        || (mat.endsWith("HELMET") && inv.getHelmet() == null))
                    CEventHandler.handleArmor(p, e.getItem(), false, e);
            }
        }

        // Sign shop
        if (e.getClickedBlock() != null && e.getClickedBlock().getType().toString().contains("SIGN"))
            if (((Sign) e.getClickedBlock().getState()).getLine(0).equals("[CustomEnchant]")) {
                if (Main.hasEconomy)
                    if (p.getItemInHand().getType() != Material.AIR) {
                        Sign sign = ((Sign) e.getClickedBlock().getState());
                        CEnchantment ce = EnchantManager.getEnchantment(sign.getLine(1));
                        if (ce == null)
                            EnchantManager.getEnchantment(sign.getLine(1));
                        if (ce == null)
                            for (CEnchantment ceT : EnchantManager.getEnchantments())
                                if (EnchantManager.containsEnchantment(sign.getLine(1), ceT))
                                    ce = ceT;
                        if (ce == null)
                            return;

                        ItemStack inHand = p.getItemInHand();
                        if (!Tools.isApplicable(inHand, ce)) {
                            p.sendMessage(ChatColor.RED + "This enchantment can not be applied to this item.");
                            return;
                        }

                        int cost = 0;
                        try {
                            cost = Integer.parseInt(sign.getLine(3).replaceAll("\\D+", ""));
                        } catch (NumberFormatException ex) {
                            return;
                        }

                        List<String> lore = new ArrayList<String>();
                        ItemMeta im = inHand.getItemMeta();

                        if (inHand.getItemMeta().hasLore()) {
                            lore = inHand.getItemMeta().getLore();

                            if (EnchantManager.getEnchantments(lore).size() == EnchantManager.getMaxEnchants()) {
                                p.sendMessage(ChatColor.RED + "You already have the maximum amount of enchantments!");
                                return;
                            }

                            for (int i = 0; i < lore.size(); i++)
                                if (EnchantManager.containsEnchantment(lore.get(i), ce)) {
                                    int newLevel = EnchantManager.getLevel(lore.get(i)) + 1;
                                    if (newLevel <= ce.getEnchantmentMaxLevel()) {
                                        if (Main.econ.getBalance(p.getName()) >= cost) {
                                            EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                                            if (ecr.transactionSuccess()) {
                                                p.sendMessage(ChatColor.GREEN + "Upgraded enchantment " + ce.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.WHITE + cost + " "
                                                        + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()) + ChatColor.GREEN + ".");
                                            } else {
                                                p.sendMessage(ChatColor.RED + "An economy error has occured:");
                                                p.sendMessage(ChatColor.RED + ecr.errorMessage);
                                                return;
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "You do not have enough money to buy this!");
                                            return;
                                        }
                                        lore.set(i, ce.getDisplayName() + " " + EnchantManager.intToLevel(newLevel));
                                        im.setLore(lore);
                                        inHand.setItemMeta(im);
                                        return;
                                    } else {
                                        p.sendMessage(ChatColor.RED + "You already have the maximum level of this enchantment!");
                                        return;
                                    }
                                }
                        }

                        if (Main.econ.getBalance(p.getName()) >= cost) {
                            EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                            if (ecr.transactionSuccess())
                                p.sendMessage(ChatColor.GREEN + "Bought enchantment " + ce.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.WHITE + cost + " "
                                        + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()) + ChatColor.GREEN + ".");
                            else {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An economy error has occured:");
                                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + ecr.errorMessage);
                                return;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have enough money to buy this!");
                            return;
                        }

                        lore.add(ce.getDisplayName() + " I");
                        im.setLore(lore);
                        inHand.setItemMeta(im);
                        if (!inHand.containsEnchantment(EnchantManager.getGlowEnchantment()))
                            inHand.addUnsafeEnchantment(EnchantManager.getGlowEnchantment(), 0);
                        return;

                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have an item in your hand.");
                        return;
                    }
            }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent e) {

        CEventHandler.handleEvent(e.getPlayer(), e, interactE);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerDeathEvent(PlayerDeathEvent e) {

        CEventHandler.handleEvent(e.getEntity(), e, death);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerMoveEvent(PlayerMoveEvent e) {

        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {

            CEventHandler.handleEvent(e.getPlayer(), e, move);
            CEventHandler.handleMines(e.getPlayer(), e);

        }

    }

    // Check if armor broke for potion effect enchantments
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerItemBreakEvent(PlayerItemBreakEvent e) {

        for (ItemStack i : e.getPlayer().getInventory().getArmorContents())
            if (i != null && i.getType() != Material.AIR)
                if (i.getAmount() == 0)
                    CEventHandler.handleArmor(e.getPlayer(), e.getBrokenItem(), true, e);

    }

    // BLOCKS

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BlockPlaceEvent(BlockPlaceEvent e) {

        CEventHandler.handleEvent(e.getPlayer(), e, blockPlaced);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BlockBreakEvent(BlockBreakEvent e) {

        if (e.getBlock().hasMetadata("ce.Ice"))
            e.setCancelled(true);

        CEventHandler.handleEvent(e.getPlayer(), e, blockBroken);
        if (e.getBlock().hasMetadata("ce.mine")) {
            Block b = e.getBlock();
            b.removeMetadata("ce.mine", Main.plugin);
            Block[] blocks = { b.getRelative(0, 1, 0), b.getRelative(1, 0, 0), b.getRelative(-1, 0, 0), b.getRelative(0, 0, 1), b.getRelative(0, 0, -1) };

            for (Block block : blocks) {
                if (block.hasMetadata("ce.mine.secondary")) {
                    String[] s = block.getMetadata("ce.mine.secondary").get(0).asString().split(" ");
                    Location loc = new Location(e.getPlayer().getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                    Location blockLoc = b.getLocation();
                    if (loc.getBlockX() == blockLoc.getBlockX() && loc.getBlockY() == blockLoc.getBlockY() && loc.getBlockZ() == blockLoc.getBlockZ())
                        block.removeMetadata("ce.mine.secondary", Main.plugin);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BlockFromToEvent(BlockFromToEvent e) {

        if (e.getBlock().hasMetadata("ce.Ice"))
            e.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void SignChangeEvent(SignChangeEvent e) {
        if (e.getLine(0).equals("[CustomEnchant]"))
            if (!e.getPlayer().isOp())
                e.setCancelled(true);
            else {
                String ench = e.getLine(1);
                CEnchantment ce = EnchantManager.getEnchantment(ench);
                if (ce == null)
                    for (CEnchantment ceT : EnchantManager.getEnchantments())
                        if (EnchantManager.containsEnchantment(ench, ceT))
                            ce = ceT;
                if (ce == null) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Could not find Custom Enchantment " + ench + ".");
                    e.setCancelled(true);
                    return;
                }
                if (Main.hasEconomy)
                    try {
                        Integer.parseInt(e.getLine(3).replaceAll("\\D+", ""));
                    } catch (NumberFormatException ex) {
                        e.getPlayer().sendMessage(ChatColor.RED + "The cost you entered is invalid.");
                        e.setCancelled(true);
                        return;
                    }
                else
                    e.getPlayer().sendMessage(ChatColor.GRAY + "You are not using a compatible economy plugin, so the cost will not be used.");
                e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully created a sign shop for the enchantment " + ench + ".");
            }
    }

    // ENCHANTMENT

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EnchantItemEvent(EnchantItemEvent e) {
        if (e.getExpLevelCost() == 30)
            if (Tools.random.nextInt(100) < (Float.parseFloat(Main.config.getString("Global.Enchantments.CEnchantingProbability"))))
                CEventHandler.handleEnchanting(e);
    }

}
