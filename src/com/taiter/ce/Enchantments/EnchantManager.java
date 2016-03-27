package com.taiter.ce.Enchantments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.taiter.ce.Main;

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

public class EnchantManager {

    private static Set<CEnchantment> enchantments = new LinkedHashSet<CEnchantment>();
    private static Enchantment glowEnchantment;
    private static int maxEnchants = -1;

    private static String lorePrefix;
    private static String enchantBookName;

    static {
        //Load the glow enchantment
        glowEnchantment = registerGlowEnchantment();
    }

    @SuppressWarnings("deprecation")
    private static Enchantment registerGlowEnchantment() {
        int id = Main.config.getInt("Global.Enchantments.GlowEnchantmentID");
        Enchantment glow = Enchantment.getById(id);
        if (glow != null)
            if (glow.getName().equals("Custom Enchantment"))
                return glow;
            else
                id = Enchantment.values()[Enchantment.values().length - 1].getId() + 1;

        Boolean forced = false;
        if (!Enchantment.isAcceptingRegistrations()) //Allow new enchantments to be registered again
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
                forced = true;
            } catch (Exception ex) {
            }

        try {
            glow = new GlowEnchantment(100);
            Enchantment.registerEnchantment(glow);
        } catch (Exception ex) {
        }

        if (forced) //Revert change
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.set(null, false);
                f.setAccessible(false);
            } catch (Exception ex) {
            }
        return glow;
    }

    public static ItemStack addEnchant(ItemStack item, CEnchantment ce) {
        return addEnchant(item, ce, 1);
    }

    public static ItemStack addEnchant(ItemStack item, CEnchantment ce, int level) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = im.getLore();
            if (maxEnchants < enchantments.size()) {
                int counter = maxEnchants;
                for (String s : lore)
                    if (containsEnchantment(s)) {
                        counter--;
                        if (counter <= 0) {
                            return item;
                        }
                    }
            }
        }
        if (level > ce.getEnchantmentMaxLevel())
            level = ce.getEnchantmentMaxLevel();
        lore.add(ce.getDisplayName() + " " + intToLevel(level));
        im.setLore(lore);
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 0);
        return item;
    }

    public static ItemStack addEnchantments(ItemStack item, HashMap<CEnchantment, Integer> list) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = im.getLore();
            if (maxEnchants < enchantments.size()) {
                int counter = maxEnchants - list.size();
                for (String s : lore)
                    if (containsEnchantment(s)) {
                        counter--;
                        if (counter <= 0) {
                            return item;
                        }
                    }
            }
        }
        for (CEnchantment ce : list.keySet()) {
            int level = list.get(ce);
            if (level > ce.getEnchantmentMaxLevel())
                level = ce.getEnchantmentMaxLevel();
            lore.add(ce.getDisplayName() + " " + intToLevel(level));
        }
        im.setLore(lore);
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 0);
        return item;
    }

    public static boolean hasEnchant(ItemStack item, CEnchantment ce) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        for (String s : lore)
            if (s.startsWith(ce.getDisplayName()) || s.startsWith(lorePrefix + ce.getOriginalName()))
                return true;
        return false;
    }

    public static void removeEnchant(ItemStack item, CEnchantment ce) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return;
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        for (String s : lore)
            if (s.startsWith(ce.getDisplayName()) || s.startsWith(ce.getOriginalName())) {
                lore.remove(s);
                im.setLore(lore);
                item.setItemMeta(im);
                if (item.getEnchantments().containsKey(glowEnchantment))
                    item.removeEnchantment(glowEnchantment);
                return;
            }
    }

    /**
     * Retrieves an enchantment by its original name. Assumes that the given String is not colored and equals the name
     * of an enchantment.
     * 
     * @param originalName
     *            The original name of the enchantment to retrieve
     * @return The enchantment specified by originalName
     */
    public static CEnchantment getInternalEnchantment(String originalName) {
        for (CEnchantment ce : enchantments) {
            if (ce.getOriginalName().equals(originalName))
                return ce;
        }
        return null;
    }

    public static CEnchantment getEnchantment(String name) {
        if (name.length() > 3)
            for (CEnchantment ce : enchantments) {
                String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                name = ChatColor.stripColor(name).toLowerCase();
                if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                    String[] split = name.split(" ");
                    if (split.length == enchantment.split(" ").length + 1) {
                        name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                        if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                            return ce;
                    } else {
                        if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                            return ce;
                    }
                }
            }
        return null;
    }

    public static Set<CEnchantment> getEnchantments() {
        return enchantments;
    }

    public static Set<CEnchantment> getEnchantments(List<String> lore) {
        Set<CEnchantment> list = new LinkedHashSet<CEnchantment>();
        if (lore != null)
            for (String name : lore)
                if (name.length() > 3)
                    for (CEnchantment ce : enchantments) {
                        String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                        name = ChatColor.stripColor(name).toLowerCase();
                        if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                            String[] split = name.split(" ");
                            name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                            if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                                list.add(ce);
                        }
                    }
        return list;
    }

    public static HashMap<CEnchantment, Integer> getEnchantmentLevels(List<String> lore) {
        HashMap<CEnchantment, Integer> list = new HashMap<CEnchantment, Integer>();
        if (lore != null)
            for (String name : lore)
                if (name.length() > 3)
                    for (CEnchantment ce : enchantments) {
                        String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                        name = ChatColor.stripColor(name).toLowerCase();
                        if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                            String[] split = name.split(" ");
                            name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                            if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                                list.put(ce, levelToInt(split[split.length - 1]));
                        }
                    }
        return list;
    }

    public static Boolean hasEnchantments(ItemStack toTest) {
        if (toTest != null)
            if (toTest.hasItemMeta() && toTest.getItemMeta().hasLore())
                for (String s : toTest.getItemMeta().getLore())
                    if (containsEnchantment(s))
                        return true;
        return false;
    }

    public static boolean isEnchantmentBook(ItemStack i) {
        if (i != null && i.getType().equals(Material.ENCHANTED_BOOK))
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(enchantBookName))
                return true;
        return false;
    }

    public static boolean isEnchantable(String mat) {
        if (mat.contains("HELMET") || mat.contains("CHESTPLATE") || mat.contains("LEGGINGS") || mat.contains("BOOTS") || mat.contains("SWORD") || mat.contains("PICKAXE") || mat.contains("AXE")
                || mat.contains("SPADE") || mat.contains("HOE") || mat.equals("BOW"))
            return true;
        if ((Main.config.getBoolean("Global.Runecrafting.Disenchanting") && mat.equals("BOOK"))
                || ((Main.config.getBoolean("Global.Runecrafting.CanStackEnchantments") && mat.equals("ENCHANTED_BOOK"))))
            return true;
        return false;
    }

    public static Boolean containsEnchantment(List<String> toTest) {
        for (String s : toTest)
            if (containsEnchantment(s))
                return true;
        return false;
    }

    public static Boolean containsEnchantment(String toTest) {
        for (CEnchantment ce : enchantments) {
            if (containsEnchantment(toTest, ce))
                return true;
        }
        return false;
    }

    public static Boolean containsEnchantment(List<String> toTest, CEnchantment ce) {
        for (String s : toTest)
            if (containsEnchantment(s, ce))
                return true;
        return false;
    }

    public static Boolean containsEnchantment(String toTest, CEnchantment ce) {
        if (toTest.startsWith(ChatColor.YELLOW + "" + ChatColor.ITALIC + "\""))
            toTest = lorePrefix + ChatColor.stripColor(toTest.replace("\"", ""));
        String next = "";
        if (toTest.startsWith(lorePrefix + ce.getOriginalName()))
            next = lorePrefix + ce.getOriginalName();
        if (toTest.startsWith(ce.getDisplayName()))
            next = ce.getDisplayName();
        if (next.isEmpty())
            return false;
        String nextTest = toTest.replace(next, "");

        if (nextTest.startsWith(" ") || nextTest.isEmpty())
            return true;
        return false;
    }

    public static String getLorePrefix() {
        return lorePrefix;
    }

    public static int getMaxEnchants() {
        return maxEnchants;
    }

    public static Enchantment getGlowEnchantment() {
        return glowEnchantment;
    }

    /*
     * This returns the enchantment level of the CE identified by checkEnchant
     */
    public static int getLevel(String checkEnchant) {
        int level = 1;
        if (checkEnchant.contains(" ")) {
            String[] splitName = checkEnchant.split(" ");
            String possibleLevel = splitName[splitName.length - 1];
            level = levelToInt(possibleLevel);
        }
        return level;
    }

    public static String intToLevel(int i) {
        String level = "I";

        if (i == 2)
            level = "II";
        else if (i == 3)
            level = "III";
        else if (i == 4)
            level = "IV";
        else if (i == 5)
            level = "V";
        else if (i == 6)
            level = "VI";
        else if (i == 7)
            level = "VII";
        else if (i == 8)
            level = "VIII";
        else if (i == 9)
            level = "IX";
        else if (i == 10)
            level = "X";

        return level;
    }

    public static int levelToInt(String level) {
        level = level.toUpperCase();
        int intLevel = 1;

        if (level.equals("II"))
            intLevel = 2;
        else if (level.equals("III"))
            intLevel = 3;
        else if (level.equals("IV"))
            intLevel = 4;
        else if (level.equals("V"))
            intLevel = 5;
        else if (level.equals("VI"))
            intLevel = 6;
        else if (level.equals("VII"))
            intLevel = 7;
        else if (level.equals("VIII"))
            intLevel = 8;
        else if (level.equals("IX"))
            intLevel = 9;
        else if (level.equals("X"))
            intLevel = 10;

        return intLevel;
    }

    public static String getEnchantBookName() {
        return enchantBookName;
    }

    public static ItemStack getEnchantBook(CEnchantment ce) {
        return getEnchantBook(ce, 1);
    }

    public static ItemStack getEnchantBook(CEnchantment ce, int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = item.getItemMeta();
        im.setLore(Arrays.asList(new String[] { lorePrefix + ce.getDisplayName() + " " + intToLevel(level) }));
        im.setDisplayName(enchantBookName);
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 0);
        return item;
    }

    public static ItemStack getEnchantBook(HashMap<CEnchantment, Integer> list) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        for (CEnchantment ce : list.keySet()) {
            lore.add(lorePrefix + ce.getDisplayName() + " " + intToLevel(list.get(ce)));
        }
        im.setLore(lore);
        im.setDisplayName(enchantBookName);
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 0);
        return item;
    }

    public static void setLorePrefix(String newPrefix) {
        lorePrefix = newPrefix;
    }

    public static void setMaxEnchants(int newMax) {
        maxEnchants = newMax;
    }

    public static void setEnchantBookName(String newName) {
        enchantBookName = newName;
    }
}
