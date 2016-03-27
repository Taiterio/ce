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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.taiter.ce.CItems.AssassinsBlade;
import com.taiter.ce.CItems.Bandage;
import com.taiter.ce.CItems.BearTrap;
import com.taiter.ce.CItems.BeastmastersBow;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.Deathscythe;
import com.taiter.ce.CItems.DruidBoots;
import com.taiter.ce.CItems.Firecracker;
import com.taiter.ce.CItems.FireworkBattery;
import com.taiter.ce.CItems.Flamethrower;
import com.taiter.ce.CItems.HealingShovel;
import com.taiter.ce.CItems.HermesBoots;
import com.taiter.ce.CItems.HookshotBow;
import com.taiter.ce.CItems.Landmine;
import com.taiter.ce.CItems.LivefireBoots;
import com.taiter.ce.CItems.Medikit;
import com.taiter.ce.CItems.Minigun;
import com.taiter.ce.CItems.NecromancersStaff;
import com.taiter.ce.CItems.PiranhaTrap;
import com.taiter.ce.CItems.PoisonIvy;
import com.taiter.ce.CItems.PotionLauncher;
import com.taiter.ce.CItems.Powergloves;
import com.taiter.ce.CItems.PricklyBlock;
import com.taiter.ce.CItems.Pyroaxe;
import com.taiter.ce.CItems.RocketBoots;
import com.taiter.ce.CItems.ThorsAxe;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;
import com.taiter.ce.Enchantments.EnchantManager;
import com.taiter.ce.Enchantments.Global.IceAspect;

import net.milkbowl.vault.economy.Economy;

public final class Main extends JavaPlugin {

    public static Plugin plugin;
    public static FileConfiguration config;
    public static CEListener listener;
    public static CeCommand commandC;
    private static ClassLoader classLoader;

    public static Set<CItem> items;

    public static Boolean createExplosions;
    public static Boolean hasRPGItems = false;

    // The inventories for the Enchantment Menu
    public static Inventory CEMainMenu;

    public static Inventory CEEnchantmentMainMenu;
    public static Inventory CEItemMenu;
    public static Inventory CEConfigMenu;

    public static Inventory CEArmorMenu;
    public static Inventory CEBootsMenu;
    public static Inventory CEBowMenu;
    public static Inventory CEGlobalMenu;
    public static Inventory CEHelmetMenu;
    public static Inventory CEToolMenu;
    //------------------------------------------------------

    // Economy
    public static Economy econ = null;
    public static Boolean hasEconomy = false;
    public static Plugin econPl;
    //------------------------------------------------------

    // Updater
    private URL updateListURL;
    private URL updateDownloadURL;

    private String currentVersion;
    private String newVersion;
    private String newMD5;

    public Boolean hasUpdate = false;
    public Boolean hasChecked = false;
    //------------------------------------------------------

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        plugin = this;
        commandC = new CeCommand(this);
        classLoader = this.getClassLoader();

        items = new HashSet<CItem>();

        //Load/Create config
        this.saveDefaultConfig();
        config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        if (config.contains("enchantments"))
            Tools.convertOldConfig();

        // Start the listener
        initializeListener();

        //Load global config values
        try {
            createExplosions = Boolean.parseBoolean(Main.config.getString("Global.CreateExplosions"));
            // Get the maximum amount of Enchantments on an Item
            EnchantManager.setMaxEnchants(Integer.parseInt(config.getString("Global.Enchantments.MaximumCustomEnchantments")));
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Config error, please check the values of");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Global.CreateExplosions (Has to be true or false) and");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Global.Enchantments.MaximumCustomEnchantments (Has to be a number)");
        }

        // Set the Loreprefix
        EnchantManager.setLorePrefix(resolveEnchantmentColor());

        EnchantManager.setEnchantBookName(ChatColor.translateAlternateColorCodes('&', Main.config.getString("Global.Books.Name")));

        // Check and set up the Economy
        if (setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Vault has been detected!");
            hasEconomy = true;
        }

        if (Main.getWorldGuard() != null)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] WorldGuard has been detected!");

        if (this.getServer().getPluginManager().getPlugin("RPG_Items") != null)
            hasRPGItems = true;

        // Make the list of Enchantments
        makeLists(true, true);

        if (EnchantManager.getEnchantments().size() == 0)
            return;

        try {
            writePermissions();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        Tools.generateInventories();

        currentVersion = plugin.getDescription().getVersion();
        try {
            updateListURL = new URL("https://api.curseforge.com/servermods/files?projectIds=54406");
        } catch (MalformedURLException e) {
        }

        if (Boolean.parseBoolean(config.getString("Global.Updates.CheckOnStartup"))) {
            this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new BukkitRunnable() {

                @Override
                public void run() {
                    if (!hasChecked)
                        updateCheck();
                }
            }, Integer.parseInt(config.getString("Global.Updates.CheckDelay")));
        }

    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(plugin);
        for (CEnchantment c : EnchantManager.getEnchantments())
            if (c instanceof IceAspect)
                for (HashMap<org.bukkit.block.Block, String> list : ((IceAspect) c).IceLists)
                    ((IceAspect) c).deleteIce(list);
    }

    // UPDATER
    public void updateCheck() {
        try {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Checking for updates...");

            URLConnection connection = updateListURL.openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("User-Agent", "Custom Enchantments - Update Checker");
            connection.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);
            JSONObject newestUpdate = (JSONObject) array.get(array.size() - 1);

            newVersion = newestUpdate.get("name").toString().replace("Custom Enchantments ", "").trim();
            newMD5 = newestUpdate.get("md5").toString();

            int newLength = newVersion.length();
            int currentLength = currentVersion.length();

            double versionNew;
            double versionCurrent;

            Boolean newHasSubVersion = false;
            Boolean currentHasSubVersion = false;

            try {
                versionNew = Double.parseDouble(newVersion);
            } catch (NumberFormatException ex) {
                newHasSubVersion = true;
                versionNew = Double.parseDouble(newVersion.substring(0, newVersion.length() - 1));
            }

            try {
                versionCurrent = Double.parseDouble(currentVersion);
            } catch (NumberFormatException ex) {
                currentHasSubVersion = true;
                versionCurrent = Double.parseDouble(currentVersion.substring(0, currentVersion.length() - 1));
            }

            if ((versionNew > versionCurrent) || ((versionNew == versionCurrent) && newHasSubVersion && currentHasSubVersion
                    && ((byte) newVersion.toCharArray()[newLength - 1] > (byte) currentVersion.toCharArray()[currentLength - 1]))) {
                hasUpdate = true;
                updateDownloadURL = new URL(newestUpdate.get("downloadUrl").toString().replace("\\.", ""));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] A new update is available!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] The new version is " + ChatColor.AQUA + newVersion + ChatColor.GREEN + ".");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] You are currently using " + ChatColor.AQUA + currentVersion + ChatColor.GREEN + ".");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] You can use '/ce update applyupdate' to update automatically.");

            } else {
                hasUpdate = false;
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] You are using the latest Version of CE!");
            }
            hasChecked = true;
        } catch (IOException ioex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Failed to check for updates");
        }

    }

    public void update() {

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Updating to Version " + newVersion + " started");

        BufferedInputStream input = null;
        FileOutputStream output = null;

        try {

            Boolean notify = Boolean.parseBoolean(config.getString("Global.Updates.UpdateNotifications"));

            int updateSize = updateDownloadURL.openConnection().getContentLength();
            File file = new File(plugin.getDataFolder().getParent(), "CustomEnchantments.jar");
            input = new BufferedInputStream(updateDownloadURL.openStream());
            output = new FileOutputStream(file);

            int bufferSize = (int) Math.ceil(updateSize / 100);

            byte[] data = new byte[bufferSize];

            int downloaded = 0;
            int cRead;

            while ((cRead = input.read(data, 0, bufferSize)) != -1) {

                output.write(data, 0, cRead);

                downloaded += cRead;

                if (notify) {
                    int percentage = ((downloaded * 100) / updateSize);
                    if (percentage % 25 == 0)
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Downloaded " + percentage + "% (" + downloaded + "/" + updateSize + " Bytes).");
                }
            }

            testFile(file, bufferSize);

            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Update " + newVersion + " successfully downloaded. Restart/Reload the Server to apply changes.");
            currentVersion = newVersion;
            hasUpdate = false;

            input.close();
            output.close();

        } catch (Exception e) {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException ex) {
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Updating to Version " + newVersion + " failed");
        }
    }

    private boolean testFile(File f, int bufferSize) throws Exception {

        InputStream fis = new FileInputStream(f);

        byte[] buffer = new byte[bufferSize];
        MessageDigest md = MessageDigest.getInstance("MD5");
        int cRead;

        while ((cRead = fis.read(buffer)) != -1)
            md.update(buffer, 0, cRead);

        fis.close();

        byte[] result = md.digest();

        String md5 = "";

        for (int i = 0; i < result.length; i++)
            md5 += Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1);

        if (md5.equals(newMD5))
            return true;

        return false;
    }
    // UPDATER

    public static WorldGuardPlugin getWorldGuard() {
        Plugin worldguard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldguard != null && worldguard instanceof WorldGuardPlugin && worldguard.isEnabled())
            return (WorldGuardPlugin) worldguard;
        return null;
    }

    public void initializeListener() {
        if (listener != null)
            HandlerList.unregisterAll(listener);

        listener = new CEListener();

        // Register the events
        getServer().getPluginManager().registerEvents(listener, this);

        // Unregister unused events

        // EnchantItemEvent may be used
        if (!getConfig().getBoolean("Global.Enchantments.CEnchantmentTable"))
            EnchantItemEvent.getHandlerList().unregister(listener);
    }

    public static String resolveEnchantmentColor() {
        String color = Main.plugin.getConfig().getString("Global.Enchantments.CEnchantmentColor");
        if (color.contains(";")) {
            String[] temp = color.split(";");
            color = "";
            for (String c : temp)
                try {
                    color += ChatColor.valueOf(c.toUpperCase());
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CE] ERROR: The ChatColor '" + c
                            + "' was not found, please check the list of Bukkit ChatColors and update the ChatColor Section. The ChatColor will be ignored to ensure that CE is still working.");
                }
        } else {
            try {
                color = ChatColor.valueOf(Main.config.getString("Global.Enchantments.CEnchantmentColor").toUpperCase()).toString();
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CE] ERROR: The ChatColor '" + color
                        + "' was not found, please check the list of Bukkit ChatColors and update the ChatColor Section. The ChatColor will be ignored to ensure that CE is still working.");
            }
        }
        return color;
    }

    private void writePermissions() {
        Permission mainNode = new Permission("ce.*", "The main permission node for Custom Enchantments.", PermissionDefault.OP);

        Permission runecrafting = new Permission("ce.runecrafting", "The permission for Runecrafting.", PermissionDefault.OP);
        runecrafting.addParent(mainNode, true);

        Permission cmdNode = new Permission("ce.cmd.*", "The permission node for CE's commands.", PermissionDefault.OP);
        Permission enchNode = new Permission("ce.ench.*", "The permission node for CE's EnchantManager.getEnchantments().", PermissionDefault.OP);
        Permission itemNode = new Permission("ce.item.*", "The permission node for CE's  items.", PermissionDefault.OP);

        cmdNode.addParent(mainNode, true);
        enchNode.addParent(mainNode, true);
        itemNode.addParent(mainNode, true);

        Permission cmdMenu = new Permission("ce.cmd.menu", "The permission for the CE command 'menu'");
        Permission cmdList = new Permission("ce.cmd.reload", "The permission for the CE command 'reload'");
        Permission cmdGive = new Permission("ce.cmd.give", "The permission for the CE command 'give'");
        Permission cmdChange = new Permission("ce.cmd.change", "The permission for the CE command 'change'");
        Permission cmdEnchant = new Permission("ce.cmd.enchant", "The permission for the CE command 'enchant'");
        Permission cmdRunecraft = new Permission("ce.cmd.runecrafting", "The permission for the CE command 'runecrafting'");

        cmdMenu.addParent(cmdNode, true);
        cmdList.addParent(cmdNode, true);
        cmdGive.addParent(cmdNode, true);
        cmdChange.addParent(cmdNode, true);
        cmdEnchant.addParent(cmdNode, true);
        cmdRunecraft.addParent(cmdNode, true);

        Bukkit.getServer().getPluginManager().addPermission(mainNode);

        Bukkit.getServer().getPluginManager().addPermission(runecrafting);

        Bukkit.getServer().getPluginManager().addPermission(cmdNode);
        Bukkit.getServer().getPluginManager().addPermission(enchNode);
        Bukkit.getServer().getPluginManager().addPermission(itemNode);

        Bukkit.getServer().getPluginManager().addPermission(cmdMenu);
        Bukkit.getServer().getPluginManager().addPermission(cmdList);
        Bukkit.getServer().getPluginManager().addPermission(cmdGive);
        Bukkit.getServer().getPluginManager().addPermission(cmdChange);
        Bukkit.getServer().getPluginManager().addPermission(cmdEnchant);
        Bukkit.getServer().getPluginManager().addPermission(cmdRunecraft);

        for (CItem ci : items) {
            Permission itemTemp = new Permission("ce.item." + ci.getPermissionName(), "The permission for the CE Item '" + ci.getOriginalName() + "'.");
            itemTemp.addParent(itemNode, true);
            Bukkit.getServer().getPluginManager().addPermission(itemTemp);
        }

        for (CEnchantment ce : EnchantManager.getEnchantments()) {
            Permission enchTemp = new Permission("ce.ench." + ce.getPermissionName(), "The permission for the CE Enchantment '" + ce.getOriginalName() + "'.");
            enchTemp.addParent(enchNode, true);
            Bukkit.getServer().getPluginManager().addPermission(enchTemp);
        }

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static void makeLists(boolean finalize, boolean printSuccess) {
        long time = System.currentTimeMillis();

        // ------------Dynamic enchantment loading----------------------
        try {
            String path = plugin.getDataFolder().getAbsolutePath();
            String classSource = Bukkit.getPluginManager().getPlugin("CustomEnchantments").getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            String seperator = "\\\\";
            if (classSource.contains("/"))
                seperator = "/";
            String[] classSourceSplit = classSource.split(seperator);
            path = path.substring(0, path.length() - 18) + classSourceSplit[classSourceSplit.length - 1].replace("%20", " ");
            JarFile jar = new JarFile(path);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (!entryName.contains("$") && entryName.contains("Enchantments") && entryName.endsWith(".class")
                        && !(entryName.contains("CEnchantment") || entryName.contains("EnchantManager") || entryName.contains("GlowEnchantment")))
                    try {
                        Application app = null;
                        String className = entryName.replace(".class", "");

                        if (entryName.contains("/")) {
                            app = Application.valueOf(entryName.split("/")[4].toUpperCase());
                            className = className.replaceAll("/", ".");
                        } else if (entryName.contains("\\")) {
                            app = Application.valueOf(entryName.split("\\\\")[4].toUpperCase());
                            className = className.replaceAll("\\\\", ".");
                        }
                        EnchantManager.getEnchantments().add((CEnchantment) classLoader.loadClass(className).getDeclaredConstructor(Application.class).newInstance(app));
                    } catch (ClassNotFoundException e) {
                    } // Checked exception, should never be thrown
            }

            jar.close();
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Enchantments could not be started,");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] please make sure that you the plugins jar");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] in your plugin folder is named 'CustomEnchantments'.");
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Enchantments could not be loaded,");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] please report this error on the Bukkit page of the plugin");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] by sending the following to Taiterio via PM:");
                e.printStackTrace();
            }
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        // --------------------------------------------------------------------------------

        if (finalize)
            for (CEnchantment ce : new HashSet<CEnchantment>(EnchantManager.getEnchantments()))
                ce.finalizeEnchantment();

        if (printSuccess)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] All Enchantments have been loaded.");

        // ITEMS

        // Bow
        items.add(new Minigun("Minigun", ChatColor.AQUA, "Fires a Volley of Arrows", 0, Material.BOW));
        items.add(new BeastmastersBow("Beastmaster's Bow", ChatColor.AQUA, "Tame the wilderness;and turn nature against your foes!", 0, Material.BOW));
        items.add(new HookshotBow("Hookshot Bow", ChatColor.AQUA, "Everyone is just one hook away", 0, Material.BOW));

        // Boots
        items.add(new HermesBoots("Hermes Boots", ChatColor.GOLD, "These boots are made for walkin'", 100, Material.DIAMOND_BOOTS));
        items.add(new LivefireBoots("Livefire Boots", ChatColor.DARK_RED, "Leave a burning trail...;Because it's fun!", 0, Material.DIAMOND_BOOTS));
        items.add(new RocketBoots("Rocket Boots", ChatColor.AQUA, "Up we go!; ;WARNING: May cause dismemberment,;            death;            and explosions", 0, Material.DIAMOND_BOOTS));
        items.add(new DruidBoots("Druid Boots", ChatColor.DARK_GREEN, "Let the nature rejuvenate you!", 0, Material.DIAMOND_BOOTS));

        // Flint + Steel
        items.add(new Flamethrower("Flamethrower", ChatColor.DARK_RED, "Burn, baby, burn!", 0, Material.FLINT_AND_STEEL));

        // Stick
        items.add(new NecromancersStaff("Necromancer's Staff of Destruction", ChatColor.AQUA, "Wreak chaos everywhere,;Because why not?", 0, Material.STICK));

        // Armor
        // items.add((CItem) new Swimsuit("Scuba Helmet", ChatColor.BLUE, "Just
        // stay underwater for a while,;Take your time!", 60,
        // Material.IRON_HELMET));

        // Axe
        items.add(new ThorsAxe("Thor's Axe", ChatColor.GOLD, "Smite your enemies down with mighty thunder!;Note: Batteries not included.", 0, Material.DIAMOND_AXE));
        items.add(new Pyroaxe("Pyroaxe", ChatColor.DARK_RED, "Are your enemies burning?;Do you want to make their situation worse?;Then this is just perfect for you!", 0, Material.DIAMOND_AXE));

        // Sword
        items.add(new AssassinsBlade("Assassin's Blade", ChatColor.AQUA, "Sneak up on your enemies and hit them hard!; ;(High chance of failure against Hacked Clients)", 200, Material.GOLD_SWORD));

        // Shovel
        items.add(new HealingShovel("Healing Shovel", ChatColor.GREEN, "Smacking other people in the face;has never been healthier!", 600, Material.GOLD_SPADE));

        // Projectile
        items.add(new Firecracker("Firecracker", ChatColor.DARK_RED, "Makes every situation a good situation!", 0, Material.SNOW_BALL));

        // Block
        items.add(new FireworkBattery("Firework-Battery", ChatColor.DARK_RED, "Make the sky shine bright with colors!", 0, Material.REDSTONE_BLOCK));

        // Mines
        items.add(new BearTrap("Bear Trap", ChatColor.GRAY, "Just hope that it does not contain bears...", 0, Material.IRON_PLATE));
        items.add(new PiranhaTrap("Piranha Trap", ChatColor.GRAY, "Who came up with this?", 0, Material.WOOD_PLATE));
        items.add(new PoisonIvy("Poison Ivy", ChatColor.DARK_GREEN, "If you're too cheap to afford ladders,;just take this, it'll work just fine!", 0, Material.VINE));
        items.add(new PricklyBlock("Prickly Block", ChatColor.LIGHT_PURPLE, "Just build a labyrinth out of these,;people will love you for it!", 0, Material.SAND));
        items.add(new Landmine("Landmine", ChatColor.GRAY, "Just don't trigger it yourself, please.", 0, Material.GOLD_PLATE));

        // Any
        items.add(new Powergloves("Powergloves", ChatColor.AQUA, "Throw all your problems away!", 500, Material.QUARTZ));
        items.add(new Medikit("Medikit", ChatColor.GREEN, "Treats most of your ailments,;it even has a box of juice!", 2000, Material.NETHER_STALK));
        items.add(new Bandage("Bandage", ChatColor.GREEN, "It has little hearts on it,;so you know it's good", 1000, Material.PAPER));
        items.add(new Deathscythe("Deathscythe", ChatColor.DARK_GRAY, "An ancient evil lies within...", 400, Material.GOLD_HOE));
        items.add(new PotionLauncher("Potion Launcher", ChatColor.DARK_GRAY,
                "Instructions: Put potion into the righthand slot;                of the potion launcher,;                aim and fire!; ;Manufactured by " + ChatColor.MAGIC + "Taiterio", 20,
                Material.HOPPER));
        //

        if (finalize)
            for (CItem ci : items)
                ci.finalizeItem();

        if (printSuccess)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] All Items have been loaded.");

        deleteInactive();

        if (printSuccess)
            if (Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")))
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Took " + (System.currentTimeMillis() - time) + "ms to initialize Custom Enchantments.");

    }

    private static void deleteInactive() {
        Set<CEnchantment> e = new LinkedHashSet<CEnchantment>(EnchantManager.getEnchantments());
        Set<CItem> i = new LinkedHashSet<CItem>(items);
        for (CEnchantment ce : e) {
            if (!Boolean.parseBoolean(config.getString("Enchantments." + ce.getOriginalName() + ".Enabled"))) {
                EnchantManager.getEnchantments().remove(ce);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Enchantment " + ce.getOriginalName() + " is disabled in the config.");
            }
        }
        for (CItem ci : i) {
            if (!Boolean.parseBoolean(config.getString("Items." + ci.getOriginalName() + ".Enabled"))) {
                items.remove(ci);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Item " + ci.getOriginalName() + " is disabled in the config.");
            }
        }
        Tools.resolveLists();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ce") || cmd.getName().equalsIgnoreCase("customenchantments")) {
            String result = commandC.processCommand(sender, args);
            if (result != "")
                sender.sendMessage(result);
            return true;
        }
        return false;
    }

}
