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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public abstract class CBasic {

    static public enum Trigger {
        INTERACT,
        INTERACT_ENTITY,
        INTERACT_LEFT,
        INTERACT_RIGHT,
        MOVE,
        DAMAGE_GIVEN,
        DAMAGE_TAKEN,
        DAMAGE_NATURE, //Falldamage, damage by cactus, etc.
        BLOCK_PLACED,
        BLOCK_BROKEN,
        SHOOT_BOW,
        PROJECTILE_THROWN,
        PROJECTILE_HIT,
        WEAR_ITEM,
        DEATH
    }

    protected Plugin main = Main.plugin;

    protected HashSet<Player> cooldown = new HashSet<Player>();
    protected HashSet<Player> lockList = new HashSet<Player>();
    protected HashSet<Trigger> triggers = new HashSet<Trigger>();

    protected String displayName;
    protected String originalName;
    protected String typeString;

    protected HashMap<PotionEffectType, Integer> potionsOnWear = new HashMap<PotionEffectType, Integer>();

    protected List<String> configEntries = new ArrayList<String>(Arrays.asList(new String[] { "Enabled: true" }));

    public Plugin getPlugin() {
        return this.main;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public HashSet<Trigger> getTriggers() {
        return this.triggers;
    }

    public FileConfiguration getConfig() {
        return Main.config;
    }

    public String getType() {
        return this.typeString;
    }

    public HashMap<PotionEffectType, Integer> getPotionEffectsOnWear() {
        return this.potionsOnWear;
    }

    public abstract double getCost();

}
