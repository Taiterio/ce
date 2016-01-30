package com.taiter.ce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.taiter.ce.CBasic.Trigger;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.HookshotBow;
import com.taiter.ce.CItems.NecromancersStaff;
import com.taiter.ce.CItems.RocketBoots;
import com.taiter.ce.EffectManager.ParticleEffect;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;
import com.taiter.ce.Enchantments.Bow.Volley;
import com.taiter.ce.Enchantments.CEnchantment.Application;

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

public class CEventHandler {

    private static boolean stackEnchantments = Main.config.getBoolean("Global.Runecrafting.CanStackEnchantments");
    private static boolean disenchanting = Main.config.getBoolean("Global.Runecrafting.Disenchanting");
    private static boolean transform = Main.config.getBoolean("Global.Runecrafting.TransformationEffect");

    public static void handleArmor(Player toCheck, ItemStack toAdd, Boolean remove, Event event) {
        if (toAdd != null && toAdd.getType() != Material.AIR && toAdd.hasItemMeta() && toAdd.getItemMeta().hasLore())
            for (String s : toAdd.getItemMeta().getLore())
                for (CBasic c : Main.listener.wearItem) {
                    if (c instanceof CEnchantment)
                        if (EnchantManager.containsEnchantment(s, (CEnchantment) c)) {
                            int level = EnchantManager.getLevel(s);
                            HashMap<PotionEffectType, Integer> potioneffects = c.getPotionEffectsOnWear();
                            if (potioneffects.size() < 1)
                                return;
                            if (remove) {
                                for (PotionEffectType pt : potioneffects.keySet())
                                    if (toCheck.hasPotionEffect(pt))
                                        toCheck.removePotionEffect(pt);
                            } else
                                for (PotionEffectType pt : potioneffects.keySet())
                                    toCheck.addPotionEffect(new PotionEffect(pt, 600000, potioneffects.get(pt) + level - 2), true);
                        }
                }
    }

    public static void handleEvent(Player toCheck, Event e, HashSet<CBasic> list) {

        long time = System.currentTimeMillis();

        for (ItemStack i : toCheck.getInventory().getArmorContents())
            if (i.getType() != Material.AIR)
                handleEventMain(toCheck, i, e, list);
        handleEventMain(toCheck, toCheck.getItemInHand(), e, list);

        if (Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")) && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogEvents"))) {
            long timeF = (System.currentTimeMillis() - time);
            if (timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[CE] Event " + e.getEventName() + " took " + timeF + "ms to process CE-Events.");
        }
    }

    public static void handleEnchanting(EnchantItemEvent e) {

        Player p = e.getEnchanter();
        ItemStack i = e.getItem();

        if (i == null)
            return;

        if (i.getType().equals(Material.BOOK)) {

            List<CEnchantment> list = new ArrayList<CEnchantment>();
            for (CEnchantment ce : EnchantManager.getEnchantments())
                if (Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) && Tools.checkPermission(ce, p))
                    list.add(ce);

            if (list.size() == 0)
                return;

            e.setCancelled(true);
            CEnchantment ce = list.get(Tools.random.nextInt(list.size()));
            int level = Tools.random.nextInt(ce.getEnchantmentMaxLevel()) + 1;
            ItemStack book = EnchantManager.getEnchantBook(ce, level);
            e.getInventory().remove(0);
            e.getInventory().setItem(0, book);
            if (!p.getGameMode().equals(GameMode.CREATIVE))
                p.setLevel(p.getLevel() - 30);
            return;
        }

        HashSet<CEnchantment> list = Tools.getEnchantList(Tools.getApplicationByMaterial(i.getType()), p);

        if(i.getType().toString().endsWith("_AXE"))
            list.addAll(Tools.getEnchantList(Application.GLOBAL));
        
        if (list.isEmpty())
            return;

        ItemMeta im = i.getItemMeta();
        List<String> lore = new ArrayList<String>();

        if (im.hasLore()) {
            lore = im.getLore();
            if (EnchantManager.containsEnchantment(lore))
                return;
        }

        int numberOfEnchantments = Tools.random.nextInt(4);
        int maxTries = 10;
        int appliedEnchantments = 0;

        if (EnchantManager.getMaxEnchants() < numberOfEnchantments)
            numberOfEnchantments = EnchantManager.getMaxEnchants();

        if (list.size() < numberOfEnchantments)
            numberOfEnchantments = list.size();

        while (numberOfEnchantments > 0 && maxTries >= 0) {
            for (CEnchantment ce : list) {
                if (numberOfEnchantments < 0)
                    break;
                else if (Tools.random.nextInt(100) < ce.getEnchantProbability()) {
                    if (!lore.isEmpty()) {
                        Boolean hasFound = false;
                        for (String s : lore)
                            if (s.startsWith(ce.getDisplayName()) || ChatColor.stripColor(s).startsWith(ce.getOriginalName()))
                                hasFound = true;
                        if (hasFound)
                            continue;
                    }

                    int level = ce.getEnchantmentMaxLevel() - 1;
                    if (level > 0)
                        level = Tools.random.nextInt(ce.getEnchantmentMaxLevel()) + 1;
                    else
                        level = 1;
                    lore.add(ce.getDisplayName() + " " + EnchantManager.intToLevel(level));
                    appliedEnchantments++;
                    numberOfEnchantments--;

                }
            }
            maxTries--;
        }

        if (appliedEnchantments == 0)
            return;

        im.setLore(lore);
        i.setItemMeta(im);

        p.getWorld().playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);

    }

    public static void handleMines(Player toCheck, PlayerMoveEvent e) {

        Block b = toCheck.getLocation().getBlock();

        if (b.hasMetadata("ce.mine") || b.hasMetadata("ce.mine.secondary")) {

            String locString = b.getX() + " " + b.getY() + " " + b.getZ();

            if (b.hasMetadata("ce.mine.secondary")) {
                locString = b.getMetadata("ce.mine.secondary").get(0).asString();
                String[] s = locString.split(" ");
                b = new Location(toCheck.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])).getBlock();
            }

            if (b.getType().equals(Material.AIR)) {
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
            toCheck.setMetadata("ce.mine", new FixedMetadataValue(Main.plugin, locString));
            if (b.hasMetadata("ce.mine"))
                Tools.getItemByOriginalname(b.getMetadata("ce.mine").get(0).asString()).effect(e, toCheck);
        }

    }

    public static void handleBows(Player toCheck, EntityDamageByEntityEvent e) {
        if (e.getDamager().hasMetadata("ce.bow.item")) {
            Tools.getItemByOriginalname(e.getDamager().getMetadata("ce.bow.item").get(0).asString()).effect(e, toCheck);
            e.getDamager().removeMetadata("ce.bow.item", Main.plugin);
        }

        if (e.getDamager().hasMetadata("ce.bow.enchantment")) {
            String[] enchantments = e.getDamager().getMetadata("ce.bow.enchantment").get(0).asString().split(" ; ");
            for (String ench : enchantments) {
                String[] enchantment = ench.split(" : ");
                CEnchantment ce = EnchantManager.getEnchantment(enchantment[0]);
                ce.effect(e, toCheck.getItemInHand(), Integer.parseInt(enchantment[1]));
            }
            e.getDamager().removeMetadata("ce.bow.enchantment", Main.plugin);
        }
    }

    public static void handleEventMain(Player toCheck, ItemStack i, Event e, HashSet<CBasic> list) {
        if (i.hasItemMeta()) {
            ItemMeta im = i.getItemMeta();
            if (!list.isEmpty()) {

                Boolean checkLore = im.hasLore();
                Boolean checkName = im.hasDisplayName();

                int volleyLevel = -1; // Level to let Volley have its effect
                                      // after all other bow enchantments

                List<String> lore = im.getLore();
                String name = im.getDisplayName();

                for (CBasic cb : list) {
                    if (checkLore)
                        if (cb instanceof CEnchantment) {
                            CEnchantment ce = (CEnchantment) cb;

                            for (String s : lore)
                                if (Tools.isApplicable(i, ce)) {

                                    if (EnchantManager.containsEnchantment(s, ce)) {

                                        int level = EnchantManager.getLevel(s);

                                        if (!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || Tools.checkPermission(ce, toCheck))
                                            if (!ce.lockList.contains(toCheck))
                                                if (!ce.getHasCooldown(toCheck))
                                                    try {
                                                        long time = System.currentTimeMillis();
                                                        if (Tools.random.nextDouble() * 100 <= ce.getOccurrenceChance()) {
                                                            // BOWS
                                                            if (e instanceof EntityShootBowEvent) {
                                                                String enchantments = ce.getOriginalName() + " : " + level;
                                                                if (((EntityShootBowEvent) e).getProjectile().hasMetadata("ce.bow.enchantment"))
                                                                    enchantments += " ; " + ((EntityShootBowEvent) e).getProjectile().getMetadata("ce.bow.enchantment").get(0).asString();
                                                                ((EntityShootBowEvent) e).getProjectile().setMetadata("ce.bow.enchantment", new FixedMetadataValue(Main.plugin, enchantments));
                                                                if (ce instanceof Volley) {
                                                                    volleyLevel = level;
                                                                    continue;
                                                                }
                                                            }
                                                            // BOWS

                                                            if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) e).getDamager() instanceof Player
                                                                    && ce.triggers.contains(Trigger.SHOOT_BOW)
                                                                    && ((Player) ((EntityDamageByEntityEvent) e).getDamager()).getItemInHand().getType().equals(Material.BOW))
                                                                continue;

                                                            ce.effect(e, i, level);
                                                        }
                                                        if (Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled"))
                                                                && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogEnchantments"))) {
                                                            long timeF = (System.currentTimeMillis() - time);
                                                            if (timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
                                                                Bukkit.getConsoleSender().sendMessage("[CE] Event " + e.getEventName() + " took " + timeF + "ms to process " + ce.getDisplayName()
                                                                        + ChatColor.RESET + "(" + ce.getOriginalName() + ").");
                                                        }
                                                    } catch (Exception ex) {
                                                        if (!(ex instanceof ClassCastException))
                                                            for (StackTraceElement element : ex.getStackTrace()) {
                                                                String className = element.getClassName();
                                                                if (className.contains("com.taiter.ce")) {
                                                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] An error occurred in " + element.getFileName() + " on line "
                                                                            + element.getLineNumber() + ": " + ex.getCause());
                                                                    break;
                                                                }
                                                            }
                                                    }
                                    }
                                }
                        }
                    if (checkName && checkLore)
                        if (cb instanceof CItem) {
                            CItem ci = (CItem) cb;
                            if (name.equals(ci.getDisplayName())) {
                                if (ci instanceof RocketBoots || ci instanceof NecromancersStaff || ci instanceof HookshotBow || lore.equals(ci.getDescription()))
                                    if (!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || Tools.checkPermission(ci, toCheck))
                                        if (!ci.getHasCooldown(toCheck))
                                            if (!ci.lockList.contains(toCheck)) {
                                                if (e instanceof PlayerMoveEvent && (ci.getOriginalName().equals("Landmine") || ci.getOriginalName().equals("Bear Trap")
                                                        || ci.getOriginalName().equals("Piranha Trap") || ci.getOriginalName().equals("Poison Ivy") || ci.getOriginalName().equals("Prickly Block")))
                                                    return;
                                                try {
                                                    if (e instanceof EntityShootBowEvent)
                                                        ((EntityShootBowEvent) e).getProjectile().setMetadata("ce.bow.item", new FixedMetadataValue(Main.plugin, ci.getOriginalName()));
                                                    if (e instanceof ProjectileLaunchEvent)
                                                        ((ProjectileLaunchEvent) e).getEntity().setMetadata("ce.projectile.item", new FixedMetadataValue(Main.plugin, ci.getOriginalName()));
                                                    long time = System.currentTimeMillis();

                                                    if (ci.effect(e, toCheck))
                                                        ci.generateCooldown(toCheck, ci.getCooldown());
                                                    if (Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled"))
                                                            && Boolean.parseBoolean(Main.config.getString("Global.Logging.LogItems"))) {
                                                        long timeF = (System.currentTimeMillis() - time);
                                                        if (timeF > Integer.parseInt(Main.config.getString("Global.Logging.MinimumMsForLog")))
                                                            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[CE] Event " + e.getEventName() + " took " + timeF + "ms to process "
                                                                    + ci.getDisplayName() + " (" + ci.getOriginalName() + ")" + ChatColor.RESET + ".");
                                                    }
                                                } catch (Exception ex) {
                                                    if (!(ex instanceof ClassCastException))
                                                        for (StackTraceElement element : ex.getStackTrace()) {
                                                            String className = element.getClassName();
                                                            if (className.contains("com.taiter.ce")) {
                                                                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] An error occurred in " + element.getFileName() + " on line "
                                                                        + element.getLineNumber() + ": " + ex.getCause());
                                                                break;
                                                            }
                                                        }
                                                }
                                            }
                                return; // Stops going through the list of items  as it is not needed anymore
                            }
                        }
                }

                if (volleyLevel >= 0) {
                    for (CBasic cb : list)
                        if (cb instanceof Volley)
                            ((Volley) cb).effect(e, i, volleyLevel);
                }

            }
        }
    }

    public static void handleRunecrafting(final InventoryClickEvent event) {
        if (event.getClickedInventory().getName()
                .equals(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba")) {
            final Inventory inv = event.getClickedInventory();

            ItemStack item = event.getCursor();
            ItemStack current = event.getCurrentItem();

            event.setCancelled(true);

            if (event.getClick().isShiftClick()) {
                if (event.getSlot() != 2) {
                    if (event.getView().getBottomInventory().firstEmpty() != -1) {
                        event.getView().getBottomInventory().addItem(event.getCurrentItem());
                        event.setCurrentItem(new ItemStack(Material.AIR));
                        updateRunecraftingInventory(inv);
                    }
                }
                return;
            }

            switch (event.getSlot()) {
            case 0:
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                    event.getWhoClicked().setItemOnCursor(item.clone());
                    if (current != null && !current.getType().equals(Material.AIR))
                        event.getWhoClicked().getInventory().addItem(current);
                    item.setAmount(1);
                } else {
                    event.getWhoClicked().setItemOnCursor(current);
                }
                inv.setItem(0, item);
                updateRunecraftingInventory(inv);
                break;
            case 1:
                inv.setItem(1, item);
                event.getWhoClicked().setItemOnCursor(current);
                updateRunecraftingInventory(inv);
                break;
            case 2:
                final ItemStack result = inv.getItem(2);
                if (result != null && !result.getType().equals(Material.AIR)) {
                    if (result.hasItemMeta() && result.getItemMeta().hasDisplayName()
                            && (result.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "Transforming...")
                                    || result.getItemMeta().getDisplayName().equals(ChatColor.DARK_RED + "Incompatible Enchantment")))
                        return;

                    final Player p = (Player) event.getWhoClicked();
                    p.getWorld().playSound(p.getLocation(), Sound.ANVIL_USE, 1f, 10f);
                    p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 2f);

                    inv.clear();

                    if (!result.getType().equals(Material.ENCHANTED_BOOK) && transform) {
                        final ItemStack transformation = new ItemStack(Material.POTATO);
                        ItemMeta im = transformation.getItemMeta();
                        im.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "Transforming...");
                        transformation.setItemMeta(im);

                        final List<Player> targets = new ArrayList<Player>();
                        targets.add(p);
                        for (Entity e : p.getNearbyEntities(30, 30, 30))
                            if (e instanceof Player)
                                targets.add((Player) e);

                        new BukkitRunnable() {

                            int counter = 50;

                            Material[] mats = Material.values();

                            @Override
                            public void run() {
                                if (counter <= 0) {
                                    inv.setItem(2, new ItemStack(Material.AIR));
                                    p.setItemOnCursor(result);
                                    this.cancel();
                                    return;
                                }
                                EffectManager.sendEffect(targets, ParticleEffect.SPELL_MOB, p.getLocation(), new Vector(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1), 1, 100);
                                transformation.setType(mats[Tools.random.nextInt(mats.length - 1)]);
                                inv.setItem(2, transformation);
                                counter--;
                            }
                        }.runTaskTimer(Main.plugin, 0, 2);
                    } else {
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                event.getWhoClicked().setItemOnCursor(result);
                            }
                        }.runTaskLater(Main.plugin, 1);
                    }
                }
                break;
            }

        } else {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                Inventory top = event.getView().getTopInventory();
                ItemStack current = event.getCurrentItem().clone();
                ItemStack topItem = current.clone();
                topItem.setAmount(1);
                
                if (EnchantManager.isEnchantmentBook(current) || EnchantManager.hasEnchantments(current)) {
                    if(current.getAmount() > 1) {
                        current.setAmount(current.getAmount()-1);
                    } else if(current.getAmount() == 1) {
                        current.setType(Material.AIR);
                    }
                    if (top.getItem(1) == null || top.getItem(1).getType().equals(Material.AIR)) {
                        top.setItem(1, topItem);
                        event.setCurrentItem(current);
                        updateRunecraftingInventory(top);
                    } else if (top.getItem(0) == null || top.getItem(0).getType().equals(Material.AIR)) {
                        top.setItem(0, topItem);
                        event.setCurrentItem(current);
                        updateRunecraftingInventory(top);
                    }
                } else if (EnchantManager.isEnchantable(current.getType().toString())) {
                    if(current.getAmount() > 1) {
                        current.setAmount(current.getAmount()-1);
                    } else if(current.getAmount() == 1) {
                        current.setType(Material.AIR);
                    }
                    if ((top.getItem(0) == null || top.getItem(0).getType().equals(Material.AIR))) {
                        top.setItem(0, topItem);
                        event.setCurrentItem(current);
                        updateRunecraftingInventory(top);
                    }
                }
            }
        }
    }

    public static void updateRunecraftingInventory(final Inventory inv) {
        new BukkitRunnable() {

            @Override
            public void run() {
                ItemStack top = inv.getItem(0);
                ItemStack bot = inv.getItem(1);

                if (top == null || bot == null || top.getType().equals(Material.AIR) || bot.getType().equals(Material.AIR)) {
                    inv.setItem(2, new ItemStack(Material.AIR));
                    return;
                }

                if (disenchanting && top.getType().equals(Material.BOOK) && !bot.getType().equals(Material.ENCHANTED_BOOK)) {
                    inv.setItem(2, EnchantManager.getEnchantBook(EnchantManager.getEnchantmentLevels(bot.getItemMeta().getLore())));
                    return;
                }
                
                if(top.getType().equals(Material.BOOK)) {
                    return;
                }

                if (EnchantManager.isEnchantmentBook(bot)) {
                    ItemStack item = top.clone();
                    HashMap<CEnchantment, Integer> list = EnchantManager.getEnchantmentLevels(bot.getItemMeta().getLore());

                    if (stackEnchantments) {
                        if (EnchantManager.isEnchantmentBook(top)) {
                            HashMap<CEnchantment, Integer> topList = EnchantManager.getEnchantmentLevels(top.getItemMeta().getLore());
                            for (CEnchantment ce : topList.keySet())
                                if (list.containsKey(ce)) {
                                    int newLevel = list.get(ce) + topList.get(ce);
                                    if (newLevel > ce.getEnchantmentMaxLevel())
                                        newLevel = ce.getEnchantmentMaxLevel();
                                    list.replace(ce, newLevel);
                                } else {
                                    if (list.size() < EnchantManager.getMaxEnchants())
                                        list.put(ce, topList.get(ce));
                                    else
                                        break;
                                }
                            inv.setItem(2, EnchantManager.getEnchantBook(list));
                            return;
                        }
                    } else if (EnchantManager.hasEnchantments(top) || EnchantManager.isEnchantmentBook(top)) {
                        return;
                    }

                    for (CEnchantment ce : list.keySet())
                        if (Tools.isApplicable(item, ce))
                            item = EnchantManager.addEnchant(item, ce, list.get(ce));
                    if (item.getEnchantments().size() > 0) {
                        inv.setItem(2, item);
                    } else {
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(ChatColor.DARK_RED + "Incompatible Enchantment");
                        item.setItemMeta(im);
                        inv.setItem(2, item);
                    }
                    return;
                }
                inv.setItem(2, new ItemStack(Material.AIR));
            }
        }.runTaskLater(Main.plugin, 2);
    }
}
