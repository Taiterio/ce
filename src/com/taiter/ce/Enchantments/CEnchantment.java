package com.taiter.ce.Enchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.CBasic;
import com.taiter.ce.Main;
import com.taiter.ce.Tools;

public abstract class CEnchantment extends CBasic {

    static public enum Application {
        ARMOR,
        GLOBAL,
        BOW,
        BOOTS,
        HELMET,
        TOOL
    }

    private static int amountGlobal = -1;
    private static int amountBow = 0;
    private static int amountTool = 0;
    private static int amountArmor = 0;
    private static int amountHelmet = 0;
    private static int amountBoots = 0;

    Application app;
    double enchantProbability;
    int enchantmentMaxLevel;
    int occurrenceChance;
    List<Integer> runecraftCostLevel;
    List<Double> runecraftCostMoney;
    List<Double> costPerLevel;
    private boolean hasRetriedConfig;

    public Application getApplication() {
        return this.app;
    }

    public double getEnchantProbability() {
        return this.enchantProbability;
    }

    public int getEnchantmentMaxLevel() {
        if (this.enchantmentMaxLevel == -1)
            return 1;
        return this.enchantmentMaxLevel;
    }

    public int getOccurrenceChance() {
        return this.occurrenceChance;
    }

    public int getRunecraftCostLevel(int level) {
        return runecraftCostLevel.get(level - 1);
    }

    public double getRunecraftCostMoney(int level) {
        return runecraftCostMoney.get(level - 1);
    }

    public double getCost(int level) {
        return costPerLevel.get(level - 1);
    }

    public CEnchantment(Application app) {
        this.typeString = "Enchantment";
        this.app = app;

        this.originalName = this.getClass().getSimpleName();
        this.permissionName = this.originalName;
        char[] nameChars = originalName.toCharArray();

        for (int i = 3; i < nameChars.length; i++) { //Go through the classname, start at the third char (Minimum enchantment length) and check for an uppercase letter
            if (Character.isUpperCase(nameChars[i]))
                this.originalName = originalName.substring(0, i) + " " + originalName.substring(i, nameChars.length);
        }

        this.occurrenceChance = 100;
        this.costPerLevel = new ArrayList<Double>(Arrays.asList(0d, 0d, 0d, 0d, 0d));
        this.runecraftCostLevel = new ArrayList<Integer>();
        this.runecraftCostMoney = new ArrayList<Double>();

        this.configEntries.put("Enabled", true);
        this.configEntries.put("DisplayName", originalName);
        this.configEntries.put("EnchantmentMaxLevel", 5);
        this.configEntries.put("OccurrenceChance", 100);
        this.configEntries.put("Cost", costPerLevel);
        this.configEntries.put("RunecraftingCost", Arrays.asList("0LVL&0$", "0LVL&0$", "0LVL&0$", "0LVL&0$", "0LVL&0$"));
    }

    public boolean getHasCooldown(Player p) {
        if (cooldown.contains(p))
            return true;
        return false;
    }

    public void generateCooldown(final Player p, long time) {
        cooldown.add(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldown.remove(p);
            }
        }.runTaskLater(main, time);
    }

    private void writeEnchantmentAmounts() {
        amountGlobal = 0;
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == Application.GLOBAL) {
                amountGlobal++;
                if (ce.triggers.contains(Trigger.SHOOT_BOW))
                    amountBow++;
            } else if (ce.getApplication() == Application.BOW)
                amountBow++;
            else if (ce.getApplication() == Application.TOOL)
                amountTool++;
            else if (ce.getApplication() == Application.BOOTS)
                amountBoots++;
            else if (ce.getApplication() == Application.HELMET)
                amountHelmet++;
            else if (ce.getApplication() == Application.ARMOR) {
                amountArmor++;
                amountHelmet++;
                amountBoots++;
            }
    }

    private double getEnchantmentProbability() {
        double enchantmentAmount = 0; //Amount of possible custom enchantments that COULD be applied with this enchantments application
        if (this.app == Application.ARMOR)
            enchantmentAmount = amountArmor;
        else if (this.app == Application.HELMET)
            enchantmentAmount = amountHelmet;
        else if (this.app == Application.BOOTS)
            enchantmentAmount = amountBoots;
        else if (this.app == Application.TOOL)
            enchantmentAmount = amountTool;
        else if (this.app == Application.BOW)
            enchantmentAmount = amountBow;
        else
            enchantmentAmount = amountGlobal;
        enchantmentAmount = 100 / enchantmentAmount;
        return enchantmentAmount;
    }

    public void finalizeEnchantment() {
        if (amountGlobal < 0)
            writeEnchantmentAmounts();
        double enchantmentProbability = getEnchantmentProbability();
        this.configEntries.put("EnchantmentProbability", enchantmentProbability);

        if (!getConfig().contains("Enchantments." + getOriginalName()))
            Tools.writeConfigEntries(this);

        try {
            this.displayName = EnchantManager.getLorePrefix() + ChatColor.translateAlternateColorCodes('&', Main.config.getString("Enchantments." + getOriginalName() + ".DisplayName"));
            if (!Boolean.parseBoolean(getConfig().getString("Global.Enchantments.UseCustomEnchantmentProbability")))
                this.enchantProbability = enchantmentProbability;
            else
                this.enchantProbability = Double.parseDouble(Main.config.getString("Enchantments." + getOriginalName() + ".EnchantmentProbability"));

            if (this.enchantmentMaxLevel != -1)
                this.enchantmentMaxLevel = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".EnchantmentMaxLevel"));
            else
                this.enchantmentMaxLevel = 1;
            this.occurrenceChance = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".OccurrenceChance"));

            this.costPerLevel = getConfig().getDoubleList("Enchantments." + getOriginalName() + ".Cost");

            if (costPerLevel.isEmpty()) {
                double cost = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".Cost"));
                costPerLevel = new ArrayList<Double>();
                for (int i = 0; i < this.enchantmentMaxLevel; i++) {
                    costPerLevel.add(cost);
                }
            } else if (costPerLevel.size() > this.enchantmentMaxLevel) {
                costPerLevel = costPerLevel.subList(0, enchantmentMaxLevel);
                this.getConfig().set("Enchantments." + getOriginalName() + ".Cost", costPerLevel);
                main.saveConfig();
                main.reloadConfig();
            }

            List<String> list = getConfig().getStringList("Enchantments." + getOriginalName() + ".RunecraftingCost");

            if (list.isEmpty()) {
                String cost = getConfig().getString("Enchantments." + getOriginalName() + ".RunecraftingCost");
                if (cost == null)
                    cost = "0LVL&0$";
                for (int i = 0; i < this.enchantmentMaxLevel; i++)
                    list.add(cost);
            }

            if (list.size() > this.enchantmentMaxLevel) {
                list = list.subList(0, enchantmentMaxLevel);
                this.getConfig().set("Enchantments." + getOriginalName() + ".RunecraftingCost", list);
                main.saveConfig();
                main.reloadConfig();
            }

            for (String rcCost : list) {
                String[] runecraftCost = rcCost.trim().split("&");
                if (runecraftCost[0].contains("$")) {
                    try {
                        this.runecraftCostMoney.add(Double.parseDouble(runecraftCost[0].replace("$", "")));
                    } catch (NumberFormatException ex) {
                        this.runecraftCostMoney.add(-1d);
                    }
                } else if (runecraftCost[0].contains("LVL")) {
                    try {
                        this.runecraftCostLevel.add(Integer.parseInt(runecraftCost[0].replace("LVL", "")));
                    } catch (NumberFormatException ex) {
                        this.runecraftCostLevel.add(-1);
                    }
                }

                if (runecraftCost.length == 2) {
                    if (runecraftCost[1].contains("$")) {
                        try {
                            this.runecraftCostMoney.add(Double.parseDouble(runecraftCost[1].replace("$", "")));
                        } catch (NumberFormatException ex) {
                            this.runecraftCostMoney.add(-1d);
                        }
                    } else if (runecraftCost[1].contains("LVL")) {
                        try {
                            this.runecraftCostLevel.add(Integer.parseInt(runecraftCost[1].replace("LVL", "")));
                        } catch (NumberFormatException ex) {
                            this.runecraftCostLevel.add(-1);
                        }
                    }
                }
            }

            for (String entry : this.configEntries.keySet()) {
                if (!getConfig().contains("Enchantments." + getOriginalName() + "." + entry)) {
                    Tools.writeConfigEntries(this);
                    break;
                }
            }
            initConfigEntries();
        } catch (Exception e) {
            if (!hasRetriedConfig) {
                Tools.writeConfigEntries(this);
                hasRetriedConfig = true;
                finalizeEnchantment();
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] WARNING: Could not configurate the CE '" + getOriginalName() + "',");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE]          please check the config for any errors, the enchantment is now disabled. ");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] 	  Explicit error:");
                for (StackTraceElement exc : e.getStackTrace())
                    Bukkit.getConsoleSender().sendMessage(exc.toString());
                EnchantManager.getEnchantments().remove(this);
            }
        }
    }

    protected void resetMaxLevel() {
        this.enchantmentMaxLevel = -1;
        this.configEntries.remove("EnchantmentMaxLevel");
    }

    public abstract void effect(Event event, ItemStack triggerItem, int level);

    public abstract void initConfigEntries();

}
