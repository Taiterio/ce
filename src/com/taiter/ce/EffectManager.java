package com.taiter.ce;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

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

public class EffectManager {

    //The particle enum as of 1.9
    public enum ParticleEffect {
        EXPLOSION_NORMAL,
        EXPLOSION_LARGE,
        EXPLOSION_HUGE,
        FIREWORKS_SPARK,
        WATER_BUBBLE,
        WATER_SPLASH,
        WATER_WAKE,
        SUSPENDED,
        SUSPENDED_DEPTH,
        CRIT,
        CRIT_MAGIC,
        SMOKE_NORMAL,
        SMOKE_LARGE,
        SPELL,
        SPELL_INSTANT,
        SPELL_MOB,
        SPELL_MOB_AMBIENT,
        SPELL_WITCH,
        DRIP_WATER,
        DRIP_LAVA,
        VILLAGER_ANGRY,
        VILLAGER_HAPPY,
        TOWN_AURA,
        NOTE,
        PORTAL,
        ENCHANTMENT_TABLE,
        FLAME,
        LAVA,
        FOOTSTEP,
        CLOUD,
        REDSTONE,
        SNOWBALL,
        SNOW_SHOVEL,
        SLIME,
        HEART,
        BARRIER,
        ITEM_CRACK,
        BLOCK_CRACK,
        BLOCK_DUST,
        WATER_DROP,
        ITEM_TAKE,
        MOB_APPEARANCE,
        //1.9 Only
        END_ROD,
        DAMAGE_INDICATOR,
        SWEEP_ATTACK
    }

    private static Constructor<?> effectConstructor;
    private static Object[] particles;

    public EffectManager() {
        try {
            effectConstructor = ReflectionHelper.getEffectPacketConstructor();
            particles = (Object[]) ReflectionHelper.loadEnumParticleValues();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Could not load particle effects. ERROR:");
            e.printStackTrace();

        }
    }

    public static void playSound(Location loc, String sound, float volume, float pitch) {
        Sound s;

        try {
            s = Sound.valueOf(sound);
        } catch (IllegalArgumentException ex) {
            try {
                //Try to resolve the 1.8 Sounds
                s = Sound.valueOf(sound.substring(sound.indexOf("_") + 1, sound.length()).replace("_AMBIENT", "").replace("GENERIC_", "").replace("EXPERIENCE_", "").replace("PLAYER_", ""));
            } catch (IllegalArgumentException ex2) {
                return;
            }
        }

        loc.getWorld().playSound(loc, s, volume, pitch);
    }

    public static void sendBlockEffect(List<Player> targets, Location loc, Vector offset, int blockID, float speed, int amount, byte data) {
        Object packet = null;
        try {
            packet = effectConstructor.newInstance(particles[5], true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) offset.getX(), (float) offset.getY(), (float) offset.getZ(),
                    speed, amount, new int[] { blockID, data });
        } catch (Exception e) {
        }

        if (packet != null)
            for (Player p : targets)
                ReflectionHelper.sendPacket(p, packet);
    }

    public static void sendEffect(List<Player> targets, ParticleEffect particle, Location loc, float speed, int amount) {
        sendEffect(targets, particle, loc, new Vector(Math.random(), Math.random(), Math.random()), speed, amount);
    }

    public static void sendEffect(List<Player> targets, ParticleEffect particle, Location loc, Vector offset, float speed, int amount) {
        Object packet = null;

        try {
            packet = effectConstructor.newInstance(particles[particle.ordinal()], true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) offset.getX(), (float) offset.getY(),
                    (float) offset.getZ(), speed, amount, null);
        } catch (Exception e) {
        }

        if (packet != null)
            for (Player p : targets)
                ReflectionHelper.sendPacket(p, packet);
    }

}
