package com.taiter.ce.CItems;

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
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.CBasic;
import com.taiter.ce.Main;
import com.taiter.ce.Tools;

public abstract class CItem extends CBasic {

    private enum ItemType {
        NORMAL, ARMORSET, MINE
    };

    private boolean hasRetriedConfig = false;

    private Material itemMaterial;
    private List<String> description;
    private long cooldownTime;
    private ChatColor itemColor;
    private ItemType itemType;
    private double cost;

    protected void addToDescription(String toAdd) {
        this.description.add(toAdd);
    }

    public Material getMaterial() {
        return this.itemMaterial;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public long getCooldown() {
        return this.cooldownTime;
    }

    public ChatColor getItemColor() {
        return this.itemColor;
    }

    public double getCost() {
        return cost;
    }

    public abstract boolean effect(Event event, Player owner); // The boolean represents whether the cooldown is to be applied or not

    public abstract void initConfigEntries();

    public CItem(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
        this.typeString = "Items";
        this.itemMaterial = mat;
        this.originalName = originalName;
        this.permissionName = originalName.replace(" ", "").replace("'", "");
        this.description = new ArrayList<String>(Arrays.asList(lDescription.split(";")));
        
        this.configEntries.put("Enabled", true);
        this.configEntries.put("DisplayName", originalName);
        this.configEntries.put("Color", color.name());
        this.configEntries.put("Description", lDescription);
        this.configEntries.put("Cooldown", lCooldown);
        this.configEntries.put("Cost", 0);
    }

    // Cooldown

    public boolean getHasCooldown(Player p) {
        if (cooldown.contains(p))
            return true;
        return false;
    }

    public void generateCooldown(final Player p, long cooldownT) {
        if (cooldownT != 0) {
            cooldown.add(p);

            new BukkitRunnable() {
                @Override
                public void run() {
                    cooldown.remove(p);
                }
            }.runTaskLater(main, cooldownT);
        }
    }

    // Locks

    protected void addLock(Player p) {
        lockList.add(p);
    }

    protected void removeLock(Player p) {
        lockList.remove(p);
    }

    // Value initiation

    public void finalizeItem() {

        if (!getConfig().contains("Items." + getOriginalName()))
            Tools.writeConfigEntries(this);

        try {
            this.itemColor = ChatColor.valueOf(Main.config.getString("Items." + getOriginalName() + ".Color").toUpperCase());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] The color of the Custom Item '" + getOriginalName() + "' is invalid, please check the official Bukkit list of ChatColors.");
            this.itemColor = ChatColor.AQUA;
        }
        this.displayName = itemColor + Main.config.getString("Items." + getOriginalName() + ".DisplayName");
        this.description = new ArrayList<String>(Arrays.asList(Main.config.getString("Items." + getOriginalName() + ".Description").split(";")));
        this.cooldownTime = Long.parseLong(Main.config.getString("Items." + getOriginalName() + ".Cooldown"));
        this.cost = Double.parseDouble(Main.config.getString("Items." + getOriginalName() + ".Cost"));

        for (String s : description)
            description.set(description.indexOf(s), ChatColor.GRAY + "" + ChatColor.ITALIC + s);

        //If the item has a special line, this whitespace is required
        this.description.add("");

        try {
            for (String entry : this.configEntries.keySet()) {
                    if (!getConfig().contains("Items." + getOriginalName() + "." + entry)) {
                        Tools.writeConfigEntries(this);
                        break;
                    }
            }
            initConfigEntries();
        } catch (Exception e) {
            if (!hasRetriedConfig) {
                Tools.writeConfigEntries(this);
                hasRetriedConfig = true;
                finalizeItem();
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] WARNING: Could not configurate the CE-Item '" + getOriginalName() + "',");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "     		 please check the config for any errors, the item is now disabled. ");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "     	Explicit error: " + e.getMessage());
                Main.items.remove(this);
            }
        }

        // Add an empty line to the end if the last line is not already one
        // This is just for stylistic purposes
        if (this.description.get(description.size() - 1).length() > 0)
            this.description.add("");
    }

}
