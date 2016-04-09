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
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.Swimsuit;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class CeCommand {

    private Main main;
    private Boolean confirmUpdate = false;

    public CeCommand(Main m) {
        this.main = m;
    }

    @SuppressWarnings("deprecation")
    public String processCommand(CommandSender sender, String[] args) {

        String Success = ChatColor.GREEN + "";
        String Error = ChatColor.RED + "";
        String usageError = Error + "Correct Usage: /ce ";

        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.";

        if (args.length >= 1) {

            String name = args[0].toLowerCase();

            if (name.equals("reload")) {

                requiredPermission += "reload";
                if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                    return Error + "You do not have permission to use this command.";

                Main.plugin.reloadConfig();
                Main.config = Main.plugin.getConfig();

                EnchantManager.getEnchantments().clear();
                Main.items.clear();
                main.initializeListener();

                Main.makeLists(true, false);

                //Get the maximum amount of Enchantments on an Item
                EnchantManager.setMaxEnchants(Integer.parseInt(Main.config.getString("Global.Enchantments.MaximumCustomEnchantments")));

                //Set the Loreprefix
                EnchantManager.setLorePrefix(Main.resolveEnchantmentColor());

                EnchantManager.setEnchantBookName(ChatColor.translateAlternateColorCodes('&', Main.config.getString("Global.Books.Name")));

                Tools.generateInventories();

                return Success + "The Custom Enchantments config has been reloaded successfully.";

            } else if (name.startsWith("u")) {
                if (sender.equals(Bukkit.getConsoleSender())) {

                    usageError += "update <check/applyupdate>";

                    if (args.length >= 2) {

                        String toDo = args[1].toLowerCase();

                        if (toDo.startsWith("c")) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    main.updateCheck();
                                }
                            }.runTaskLater(Main.plugin, 1l);
                            return "";
                        } else if (toDo.equals("applyupdate")) {
                            if (!main.hasChecked)
                                return Error + "You need to check for an update first using '/ce update check'.";
                            if (main.hasUpdate) {
                                if (!confirmUpdate) {
                                    confirmUpdate = true;
                                    sender.sendMessage(ChatColor.AQUA + "Rerun the command to confirm the update (This expires in 5 Minutes).");
                                    Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new BukkitRunnable() {

                                        @Override
                                        public void run() {
                                            if (confirmUpdate)
                                                confirmUpdate = false;
                                        }
                                    }, 6000l);
                                    return "";
                                } else {
                                    Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new BukkitRunnable() {

                                        @Override
                                        public void run() {
                                            main.update();
                                        }
                                    }, 1l);
                                    return "";
                                }
                            } else {
                                Error += "You are already using the latest version of CE.";
                                return Error;
                            }
                        } else {
                            return usageError;
                        }
                    } else {
                        return usageError;
                    }
                } else {
                    Error += "This command can only be run via Console";
                    return Error;
                }
            } else if (name.startsWith("g")) {

                requiredPermission += "give";
                if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                    return Error + "You do not have permission to use this command.";

                usageError += "give <Player> <Material> <Enchantment:Level/Item> [Enchantment:Level] ...";
                if (args.length >= 4) {

                    Player target = null;

                    for (Player ps : Bukkit.getOnlinePlayers())
                        if (ps.getName().equalsIgnoreCase(args[1]))
                            target = ps;

                    if (target == null) {
                        Error += "The Player '" + args[1] + "' was not found.";
                        return Error;
                    }

                    if (target.getInventory().firstEmpty() < 0) {
                        Error += "The Inventory of Player '" + args[1] + "' is full.";
                        return Error;
                    }

                    Material mat = null;

                    try {
                        mat = Material.getMaterial(Integer.parseInt(args[2]));
                    } catch (Exception e) {
                    }

                    if (mat == null) {

                        try {
                            mat = Material.valueOf(args[2].toUpperCase());
                        } catch (Exception e) {
                            Error += "The Material '" + args[2] + "' was not found.";
                            return Error;
                        }
                    }

                    String fullString = args[3];
                    if (args.length > 4)
                        for (int i = 4; i < args.length; i++)
                            fullString += " " + args[i];

                    fullString = fullString.toLowerCase();

                    CItem custom = null;
                    List<String> enchants = new ArrayList<String>();
                    List<String> cEnchants = new ArrayList<String>();

                    for (CItem ci : Main.items) {
                        if (fullString.contains(ci.getOriginalName().toLowerCase())) {
                            custom = ci;
                            fullString.replace(ci.getOriginalName().toLowerCase(), "");
                        } else if (fullString.contains(ci.getOriginalName().replace(" ", "").toLowerCase())) {
                            custom = ci;
                            fullString.replace(ci.getOriginalName().replace(" ", "").toLowerCase(), "");
                        } else if (fullString.contains(ci.getDisplayName().toLowerCase())) {
                            custom = ci;
                            fullString.replace(ci.getDisplayName().toLowerCase(), "");
                        } else if (fullString.contains(ci.getDisplayName().replace(" ", "").toLowerCase())) {
                            custom = ci;
                            fullString.replace(ci.getDisplayName().replace(" ", "").toLowerCase(), "");
                        }
                    }

                    for (int i = 0; i < fullString.split(" ").length; i++) {
                        for (CEnchantment ce : EnchantManager.getEnchantments()) {
                            int level = 0;
                            int index = 0;
                            int endIndex = 0;
                            String enchName = "";
                            if (fullString.contains(ce.getOriginalName().toLowerCase())) {
                                enchName = ce.getOriginalName().toLowerCase();
                                index = fullString.indexOf(enchName);
                                endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level

                                if (endIndex <= fullString.length())
                                    enchName = fullString.substring(index, endIndex);
                                else {
                                    endIndex = index + enchName.length() + 1;
                                    if (endIndex <= fullString.length())
                                        enchName = fullString.substring(index, endIndex);
                                }

                                if (enchName.endsWith(" "))
                                    enchName = fullString.substring(index, endIndex - 1);

                                if (enchName.contains(":")) {
                                    String[] finalName = enchName.split(":");
                                    try {
                                        level = Integer.parseInt(finalName[1]);
                                    } catch (Exception e) {
                                    }
                                    enchName = finalName[0];
                                }
                                fullString = fullString.replace(enchName, "");
                                cEnchants.add(ce.getDisplayName() + " " + level);

                            } else if (fullString.contains(ce.getOriginalName().replace(" ", "").toLowerCase())) {

                                enchName = ce.getOriginalName().replace(" ", "").toLowerCase();
                                index = fullString.indexOf(enchName);
                                endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level

                                if (endIndex <= fullString.length())
                                    enchName = fullString.substring(index, endIndex);
                                else {
                                    endIndex = index + enchName.length() + 1;
                                    if (endIndex <= fullString.length())
                                        enchName = fullString.substring(index, endIndex);
                                }

                                if (enchName.endsWith(" "))
                                    enchName = fullString.substring(index, endIndex - 1);

                                if (enchName.contains(":")) {
                                    String[] finalName = enchName.split(":");
                                    try {
                                        level = Integer.parseInt(finalName[1]);
                                    } catch (Exception e) {
                                    }
                                    enchName = finalName[0];
                                }
                                fullString = fullString.replace(enchName, "");
                                cEnchants.add(ce.getDisplayName() + " " + level);

                            } else if (fullString.contains(ce.getDisplayName().toLowerCase())) {

                                enchName = ce.getDisplayName().toLowerCase();
                                index = fullString.indexOf(enchName);
                                endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level

                                if (endIndex <= fullString.length())
                                    enchName = fullString.substring(index, endIndex);
                                else {
                                    endIndex = index + enchName.length() + 1;
                                    if (endIndex <= fullString.length())
                                        enchName = fullString.substring(index, endIndex);
                                }

                                if (enchName.endsWith(" "))
                                    enchName = fullString.substring(index, endIndex - 1);

                                if (enchName.contains(":")) {
                                    String[] finalName = enchName.split(":");
                                    try {
                                        level = Integer.parseInt(finalName[1]);
                                    } catch (Exception e) {
                                    }
                                    enchName = finalName[0];
                                }
                                fullString = fullString.replace(enchName, "");
                                cEnchants.add(ce.getDisplayName() + " " + level);

                            } else if (fullString.contains(ce.getDisplayName().replace(" ", "").toLowerCase())) {

                                enchName = ce.getDisplayName().replace(" ", "").toLowerCase();
                                index = fullString.indexOf(enchName);
                                endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level

                                if (endIndex <= fullString.length())
                                    enchName = fullString.substring(index, endIndex);
                                else {
                                    endIndex = index + enchName.length() + 1;
                                    if (endIndex <= fullString.length())
                                        enchName = fullString.substring(index, endIndex);
                                }

                                if (enchName.endsWith(" "))
                                    enchName = fullString.substring(index, endIndex - 1);

                                if (enchName.contains(":")) {
                                    String[] finalName = enchName.split(":");
                                    try {
                                        level = Integer.parseInt(finalName[1]);
                                    } catch (Exception ex) {
                                    }
                                    enchName = finalName[0];
                                }
                                fullString = fullString.replace(enchName, "");
                                cEnchants.add(ce.getDisplayName() + " " + level);

                            }
                        }
                        for (Enchantment e : Enchantment.values()) {

                            int level = 0;
                            int index = 0;
                            int endIndex = 0;
                            String enchName = e.getName().toLowerCase();

                            if (fullString.contains(enchName)) {

                                index = fullString.indexOf(enchName);
                                endIndex = index + enchName.length() + 3; // Making a substring from index - endIndex returns the enchantment's name with the level

                                if (endIndex <= fullString.length())
                                    enchName = fullString.substring(index, endIndex);
                                else {
                                    endIndex = index + enchName.length() + 2;
                                    if (endIndex <= fullString.length())
                                        enchName = fullString.substring(index, endIndex);
                                }

                                if (enchName.endsWith(" "))
                                    enchName = fullString.substring(index, endIndex - 1);

                                if (enchName.contains(":")) {
                                    String[] finalName = enchName.split(":");
                                    try {
                                        level = Integer.parseInt(finalName[1]);
                                    } catch (Exception ex) {
                                    }
                                    enchName = finalName[0];
                                }
                                fullString = fullString.replace(enchName, "");
                                enchants.add(e.getName() + " " + level);
                            }
                        }
                    }

                    ItemStack newItem = new ItemStack(mat);
                    ItemMeta im = newItem.getItemMeta();
                    String targetNotification = ChatColor.GOLD + "";

                    if (custom != null) {
                        if (Tools.checkPermission(custom, target)) {

                            im.setDisplayName(custom.getDisplayName());
                            im.setLore(custom.getDescription());
                            newItem.setItemMeta(im);
                            if (custom instanceof Swimsuit) {//TODO:REPLACE

                                int count = 0;

                                for (ItemStack i : target.getInventory())
                                    if (i == null || i.getType().equals(Material.AIR))
                                        count++;

                                if (count < 4) {
                                    Error += "The Inventory of Player '" + args[1] + "' is full.";
                                    return Error;
                                }

                                ItemStack cp = newItem.clone();
                                ItemStack le = newItem.clone();
                                ItemStack bo = newItem.clone();

                                String[] parts = ((Swimsuit) custom).parts;

                                cp.setType(Material.IRON_CHESTPLATE);
                                le.setType(Material.IRON_LEGGINGS);
                                bo.setType(Material.IRON_BOOTS);

                                im.setDisplayName(parts[1]);
                                cp.setItemMeta(im);
                                im.setDisplayName(parts[2]);
                                le.setItemMeta(im);
                                im.setDisplayName(parts[3]);
                                bo.setItemMeta(im);

                                target.getInventory().addItem(newItem);
                                target.getInventory().addItem(cp);
                                target.getInventory().addItem(le);
                                target.getInventory().addItem(bo);
                            }
                            Success += "The enchanted Item was given to Player " + target.getName() + ".";
                            targetNotification += "You have received an enchanted item from " + sender.getName() + "!";
                        } else {
                            Error += target.getName() + " does not have the permission to use the item " + custom.getOriginalName() + ".";
                            return Error;
                        }
                    }

                    if (!enchants.isEmpty()) {
                        for (String e : enchants) {
                            String[] enchALvl = e.split(" ");
                            Enchantment ench = Enchantment.getByName(enchALvl[0]);
                            int level = 1;
                            try {
                                level = Integer.parseInt(enchALvl[1]);
                            } catch (Exception ex) {
                            }
                            newItem.addUnsafeEnchantment(ench, level);
                        }
                        if (Success.length() < 10) {
                            Success += "The enchanted Item was successfully given to Player " + target.getName() + ".";
                            targetNotification += "You have received an enchanted item from " + sender.getName() + "!";
                        }
                    }
                    if (!cEnchants.isEmpty()) {
                        HashMap<CEnchantment, Integer> list = new HashMap<CEnchantment, Integer>();
                        for (String e : cEnchants) {
                            String[] split = e.split(" ");
                            list.put(EnchantManager.getEnchantment(e), Integer.parseInt(split[split.length - 1]));
                        }

                        if (newItem.getType().equals(Material.BOOK))
                            newItem = EnchantManager.getEnchantBook(list);
                        else
                            newItem = EnchantManager.addEnchantments(newItem, list);
                        if (Success.length() < 10) {
                            Success += "The enchanted Item was successfully given to Player " + target.getName() + ".";
                            targetNotification += "You have received an enchanted item from " + sender.getName() + "!";
                        }
                    }
                    if (Success.length() > 10) {
                        target.getInventory().addItem(newItem);
                        target.sendMessage(targetNotification);
                        return Success;
                    } else
                        return Error + "No enchantments or items were found to be applied.";

                } else
                    return usageError;
            }

            if (sender instanceof Player) {

                Player p = (Player) sender;

                if (name.startsWith("rune")) {
                    requiredPermission += "runecrafting";
                    if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                        return Error + "You do not have permission to use this command.";

                    Inventory inv = Bukkit.createInventory(p, InventoryType.FURNACE,
                            ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba");
                    inv.setContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });

                    p.openInventory(inv);
                    return "";
                } else if (name.startsWith("l")) {
                    requiredPermission += "list";
                    if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                        return Error + "You do not have permission to use this command.";

                    usageError += "list <Items/Enchantments>";
                    if (args.length >= 2) {
                        String toList = args[1].toLowerCase();
                        if (toList.startsWith("i")) {
                            p.sendMessage(ChatColor.GOLD + "-------------Item List-------------");
                            for (CItem ci : Main.items)
                                if (p.isOp() || Tools.checkPermission(ci, p))
                                    p.sendMessage("   " + ci.getDisplayName());
                            p.sendMessage(ChatColor.GOLD + "-----------------------------------");
                            return "";
                        } else if (toList.startsWith("e")) {
                            p.sendMessage(ChatColor.GOLD + "----------Enchantment List-----------");
                            for (CEnchantment ce : EnchantManager.getEnchantments())
                                if (p.isOp() || Tools.checkPermission(ce, p))
                                    p.sendMessage("   " + ce.getDisplayName());
                            p.sendMessage(ChatColor.GOLD + "------------------------------------");
                            return "";
                        } else
                            return usageError;
                    } else
                        return usageError;

                } else if (name.equals("remove")) {
                    requiredPermission += "remove";
                    if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                        return Error + "You do not have permission to use this command.";
                    ItemStack item = ((Player) sender).getItemInHand();
                    if (item == null || item.getType().equals(Material.AIR)) {
                        return Error + "You are not holding an item!";
                    }
                    ItemMeta im = item.getItemMeta();
                    if (!im.hasLore()) {
                        return Error + "Your item does not have any enchantments!";
                    }
                    List<String> lore = im.getLore();
                    if (args.length >= 2) {
                        CEnchantment ce = EnchantManager.getEnchantment(args[1]);
                        if (ce == null)
                            return Error + "The enchantment " + args[1] + " does not exist!";
                        for (String s : im.getLore())
                            if (EnchantManager.containsEnchantment(s, ce)) {
                                lore.remove(s);
                                im.setLore(lore);
                                item.setItemMeta(im);
                                return Success + "Removed the enchantment " + ce.getDisplayName() + ChatColor.GREEN + "!";
                            }
                    } else {
                        for (String s : im.getLore())
                            if (EnchantManager.containsEnchantment(s))
                                lore.remove(s);
                    }
                    im.setLore(lore);
                    item.setItemMeta(im);
                    return Success + "Removed all custom enchantments.";
                } else if (name.startsWith("m")) {
                    requiredPermission += "menu";
                    if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                        return Error + "You do not have permission to use this command.";

                    p.openInventory(Main.CEMainMenu);
                    return "";
                }

                ItemStack item = p.getItemInHand();

                if (name.startsWith("i") || name.startsWith("e")) {

                    requiredPermission += "enchant";
                    if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                        return Error + "You do not have permission to use this command.";

                    usageError += (name.startsWith("e") ? "enchant [Required Material] <Enchantment> <Level>" : "item <Item>");
                    if (args.length >= 2) {

                        String customName = args[1];
                        Material test = null;

                        int start = 2;

                        if (Material.getMaterial(customName) != null)
                            test = Material.getMaterial(customName);
                        else
                            try {
                                int material = Integer.parseInt(customName);
                                if (Material.getMaterial(material) != null)
                                    test = Material.getMaterial(material);
                            } catch (NumberFormatException ex) {
                            }

                        if (test != null) {
                            if (p.getItemInHand().getType() != test)
                                return Error + "You do not have the right material to enchant this!";
                            start++;
                            customName = args[2];
                        }

                        int level = 1;

                        if (name.startsWith("e")) {
                            if (item.getType().equals(Material.AIR))
                                return Error + "You are not holding an item in your hand";
                            try {
                                level = Integer.parseInt(args[args.length - 1]);
                            } catch (Exception e) {
                            }
                        } else {
                            level = 0;
                        }

                        if (level < 0)
                            level *= -1;

                        if (level > 10)
                            level = 0;

                        if (args.length > start)
                            for (int i = start; i < (level == 0 ? args.length : args.length - 1); i++)
                                customName += " " + args[i];

                        CBasic custom = null;

                        if (name.startsWith("e")) {
                            for (CEnchantment ce : EnchantManager.getEnchantments())
                                if (ce.getOriginalName().equalsIgnoreCase(customName) || ChatColor.stripColor(ce.getDisplayName()).equalsIgnoreCase(customName)
                                        || ce.getOriginalName().replace(" ", "").equalsIgnoreCase(customName)
                                        || ChatColor.stripColor(ce.getDisplayName()).replace(" ", "").equalsIgnoreCase(customName)) {
                                    custom = ce;
                                    if (ce.getEnchantmentMaxLevel() < level) {
                                        level = ce.getEnchantmentMaxLevel();
                                    }
                                }
                        } else {
                            for (CItem ci : Main.items)
                                if (ci.getOriginalName().equalsIgnoreCase(customName) || ChatColor.stripColor(ci.getDisplayName()).equalsIgnoreCase(customName)
                                        || ci.getOriginalName().replace(" ", "").equalsIgnoreCase(customName)
                                        || ChatColor.stripColor(ci.getDisplayName()).replace(" ", "").equalsIgnoreCase(customName)) {
                                    custom = ci;
                                }

                            if (custom == null) {
                                Error += "The item '" + customName + "' does not exist.";
                                return Error;
                            }
                        }
                        if (custom == null) {
                            Enchantment ench = null;
                            try {
                                ench = Enchantment.getById(Integer.parseInt(customName));
                            } catch (Exception e) {
                                try {
                                    ench = Enchantment.getByName(customName);
                                } catch (Exception ex) {
                                }
                            }

                            if (ench != null)
                                if (item.containsEnchantment(ench)) {
                                    int newLevel = item.getEnchantmentLevel(ench) + level;
                                    item.removeEnchantment(ench);
                                    item.addUnsafeEnchantment(ench, newLevel);
                                    return Success + "You have succesfully increased the item's level of " + ench.getName() + " by " + level + ".";
                                } else {
                                    item.addUnsafeEnchantment(ench, level);
                                    return Success + "You have succesfully enchanted your item with " + ench.getName() + " level " + level + ".";
                                }

                            Error += "The enchantment '" + customName + "' does not exist.";
                            return Error;

                        }

                        if (item.getType().equals(Material.BOOK) && custom instanceof CEnchantment) {
                            p.setItemInHand(EnchantManager.getEnchantBook((CEnchantment) custom, level));

                            return Success + "You have created an enchanted book with '" + custom.getDisplayName() + ChatColor.GREEN + "' level " + level + "!";
                        }

                        if (!Tools.checkPermission(custom, p)) {
                            Error += "You do not have permission to use '" + customName + "'.";
                            return Error;
                        }

                        List<String> lore = new ArrayList<String>();

                        ItemMeta im = item.getItemMeta();

                        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                            lore = item.getItemMeta().getLore();
                            if (custom instanceof CEnchantment) {
                                if (EnchantManager.containsEnchantment(lore, (CEnchantment) custom))
                                    for (int i = 0; i < lore.size(); i++) {
                                        if (EnchantManager.containsEnchantment(lore.get(i), (CEnchantment) custom)) {
                                            int newLevel = EnchantManager.getLevel(lore.get(i)) + level;
                                            int maxLevel = ((CEnchantment) custom).getEnchantmentMaxLevel();
                                            if (EnchantManager.getLevel(lore.get(i)) == ((CEnchantment) custom).getEnchantmentMaxLevel())
                                                return Error + "You already have the maximum level of this enchantment!";
                                            if (newLevel > maxLevel)
                                                newLevel = maxLevel;
                                            lore.set(i, custom.getDisplayName() + " " + EnchantManager.intToLevel(newLevel));
                                            im.setLore(lore);
                                            item.setItemMeta(im);
                                            p.setItemInHand(item);
                                            return (Success + "You have increased your item's level of " + custom.getDisplayName() + ChatColor.GREEN
                                                    + (newLevel == maxLevel ? " to " + maxLevel : " by " + level) + "!");
                                        }
                                    }
                                int number = EnchantManager.getMaxEnchants();
                                if (number > 0) {
                                    for (String s : lore)
                                        if (EnchantManager.containsEnchantment(s)) {
                                            number--;
                                            if (number <= 0)
                                                return (Error + "You already have the maximum number of Enchantments on your item!");
                                        }
                                }

                            }
                        }

                        if (custom instanceof CEnchantment) {
                            p.setItemInHand(EnchantManager.addEnchant(item, (CEnchantment) custom, level));
                            Success += "You have enchanted your item with '" + custom.getDisplayName() + ChatColor.GREEN + "' level " + level + "!";
                        } else if (custom instanceof CItem) {
                            ItemStack newItem = new ItemStack(((CItem) custom).getMaterial());
                            ItemMeta newIm = newItem.getItemMeta();
                            newIm.setDisplayName(custom.getDisplayName());
                            newIm.setLore(((CItem) custom).getDescription());
                            newItem.setItemMeta(newIm);
                            if (custom instanceof Swimsuit) {//TODO:REPLACE

                                int count = 0;

                                for (ItemStack i : p.getInventory())
                                    if (i == null || i.getType().equals(Material.AIR))
                                        count++;

                                if (count < 4) {
                                    Error += "Your inventory is full.";
                                    return Error;
                                }

                                ItemStack cp = newItem.clone();
                                ItemStack le = newItem.clone();
                                ItemStack bo = newItem.clone();

                                String[] parts = ((Swimsuit) custom).parts;

                                cp.setType(Material.IRON_CHESTPLATE);
                                le.setType(Material.IRON_LEGGINGS);
                                bo.setType(Material.IRON_BOOTS);

                                im.setDisplayName(parts[1]);
                                cp.setItemMeta(im);
                                im.setDisplayName(parts[2]);
                                le.setItemMeta(im);
                                im.setDisplayName(parts[3]);
                                bo.setItemMeta(im);

                                p.getInventory().addItem(newItem);
                                p.getInventory().addItem(cp);
                                p.getInventory().addItem(le);
                                p.getInventory().addItem(bo);
                            } else {
                                if (p.getInventory().firstEmpty() == -1) {
                                    return Error + "Your inventory is full!";
                                } else
                                    p.getInventory().addItem(newItem);
                            }

                            Success += "You have created the item '" + custom.getDisplayName() + ChatColor.GREEN + "'!";
                        }
                        return Success;

                    } else
                        return usageError;

                    // Changes item's name/lore, color, etc
                }

                if (item.getType() != Material.AIR) {
                    if (name.startsWith("c")) {

                        requiredPermission += "change";
                        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp())
                            return Error + "You do not have permission to use this command.";

                        usageError += "change <name/lore> <color/set/add/reset> [New Value]";

                        if (args.length == 3) {
                            if (args[2].toLowerCase().startsWith("r")) {
                                ItemMeta im = item.getItemMeta();
                                im.setLore(new ArrayList<String>());
                                item.setItemMeta(im);
                                return Success + "You have successfully reset the item's lore!";
                            }
                        } else if (args.length >= 4) {

                            String toChange = args[1].toLowerCase();
                            String option = args[2].toLowerCase();

                            if (toChange.startsWith("n")) {

                                ItemMeta im = item.getItemMeta();

                                if (option.startsWith("s")) {

                                    String toSet = "";

                                    for (int i = 3; i < args.length - 1; i++) {
                                        toSet += args[i] + " ";
                                    }

                                    toSet += args[args.length - 1];

                                    toSet = ChatColor.translateAlternateColorCodes('&', toSet);

                                    im.setDisplayName(toSet);
                                    item.setItemMeta(im);
                                    return Success + "You have successfully set the item's Name!";

                                }

                                if (item.hasItemMeta() && im.hasDisplayName()) {

                                    if (option.startsWith("c")) {

                                        String test = args[3].toUpperCase();
                                        try {
                                            test = ChatColor.valueOf(test) + "";
                                        } catch (IllegalArgumentException e) {
                                            if (test.contains("&"))
                                                test = ChatColor.translateAlternateColorCodes('&', test);
                                            else
                                                return Error + "The Color " + args[3] + " could not be found.";
                                        }

                                        im.setDisplayName(test + ChatColor.stripColor(im.getDisplayName()));
                                        item.setItemMeta(im);
                                        return Success + "You have successfully changed the item's Color!";

                                    } else if (option.startsWith("a")) {

                                        String toSet = "";

                                        for (int i = 3; i < args.length - 1; i++) {
                                            toSet += args[i] + " ";
                                        }

                                        toSet += args[args.length - 1];

                                        im.setDisplayName(im.getDisplayName() + " " + toSet);
                                        item.setItemMeta(im);
                                        return Success + "You have successfully changed the item's Name!";

                                    } else if (option.startsWith("r")) {

                                        im.setDisplayName(null);
                                        item.setItemMeta(im);
                                        return Success + "You have successfully reset the item's Name!";

                                    }

                                } else {

                                    return Error + "Your item does not have a name to be changed, use '/ce change name set' first.";

                                }
                                return usageError;

                            } else if (toChange.startsWith("l")) {

                                ItemMeta im = item.getItemMeta();

                                if (option.startsWith("s")) {

                                    List<String> lore = new ArrayList<String>();

                                    String toSet = "";

                                    for (int i = 3; i < args.length - 1; i++)
                                        toSet += args[i] + " ";
                                    toSet += args[args.length - 1];

                                    lore.add(toSet);

                                    im.setLore(lore);
                                    item.setItemMeta(im);
                                    return Success + "You have successfully set the item's lore!";

                                }

                                if (item.hasItemMeta() || item.getItemMeta().hasLore()) {

                                    List<String> lore = im.getLore();

                                    if (option.startsWith("c")) {

                                        if (ChatColor.valueOf(args[3].toUpperCase()) != null) {

                                            List<String> l = new ArrayList<String>();

                                            for (String i : lore) {
                                                l.add(ChatColor.valueOf(args[3].toUpperCase()) + "" + ChatColor.stripColor(i));
                                            }

                                            im.setLore(l);
                                            item.setItemMeta(im);
                                            return Success + "You have successfully changed the color of the item's lore!";

                                        }

                                        return Error + "The Color " + args[3] + " could not be found.";

                                    } else if (option.startsWith("a")) {

                                        String toSet = "";

                                        for (int i = 3; i < args.length - 1; i++) {
                                            toSet += args[i] + " ";
                                        }

                                        toSet += args[args.length - 1];

                                        lore.add(toSet);
                                        im.setLore(lore);
                                        item.setItemMeta(im);
                                        return Success + "You have successfully added the new line to the lore!";

                                    }

                                } else {

                                    return Error + "Your item does not have a lore to be changed, use '/ce change lore set' first.";

                                }

                            }

                        }

                        return usageError;

                    }

                } else {

                    return Error + "You are not holding an item in your hand";

                }

            } else {

                return Error + "This command can only be used by players";

            }

        }

        usageError += "<Reload/List/Remove/Enchant/Menu/Change/Give/Update>";
        return usageError;

    }

}
