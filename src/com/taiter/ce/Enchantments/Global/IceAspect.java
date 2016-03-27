package com.taiter.ce.Enchantments.Global;

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
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;

public class IceAspect extends CEnchantment {

    int SlowStrength;
    int SlowDuration;
    int chanceFreeze;
    int SpecialFreezeDuration;
    int chanceSpecialFreeze;
    boolean specialFreeze;

    public List<HashMap<Block, String>> IceLists = new ArrayList<HashMap<Block, String>>();

    public IceAspect(Application app) {
        super(app);
        configEntries.put("SlowStrength", 5);
        configEntries.put("SlowDuration", 40);
        configEntries.put("ChanceFreeze", 60);
        configEntries.put("SpecialFreeze", true);
        configEntries.put("SpecialFreezeDuration", 60);
        configEntries.put("ChanceSpecialFreeze", 10);
        triggers.add(Trigger.DAMAGE_GIVEN);
        triggers.add(Trigger.SHOOT_BOW);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        int i = Tools.random.nextInt(100);

        if (i < chanceFreeze) {
            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SlowDuration, SlowStrength, false), true);
            EffectManager.playSound(event.getEntity().getLocation(), "BLOCK_DIG_SNOW", 0.6f, 2f);
        }
        if (specialFreeze) {
            if (i < chanceSpecialFreeze) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity ent = (LivingEntity) event.getEntity();
                    Player p = null;

                    if (event.getDamager() instanceof Player)
                        p = (Player) event.getDamager();
                    else
                        p = (Player) ((Projectile) event.getDamager()).getShooter();

                    ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SpecialFreezeDuration + 20, 10));
                    final HashMap<Block, String> list = getIgloo(ent.getLocation(), 3, p);

                    generateCooldown(p, SpecialFreezeDuration);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            deleteIce(list);
                            IceLists.remove(list);
                        }
                    }.runTaskLater(getPlugin(), SpecialFreezeDuration);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void deleteIce(HashMap<Block, String> list) {
        for (Entry<Block, String> b : list.entrySet()) {
            b.getKey().setType(Material.getMaterial(b.getValue().split(" ")[0]));
            b.getKey().setData((byte) Integer.parseInt(b.getValue().split(" ")[1]));
            b.getKey().removeMetadata("ce.Ice", getPlugin());
        }
        IceLists.remove(list);
    }

    @SuppressWarnings("deprecation")
    private HashMap<Block, String> getIgloo(Location start, int size, Player p) {
        HashMap<Block, String> list = new HashMap<Block, String>();
        int bx = start.getBlockX();
        int by = start.getBlockY();
        int bz = start.getBlockZ();

        for (int x = bx - size; x <= bx + size; x++)
            for (int y = by - 1; y <= by + size; y++)
                for (int z = bz - size; z <= bz + size; z++) {
                    double distancesquared = (bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y));
                    if (distancesquared < (size * size) && distancesquared >= ((size - 1) * (size - 1))) {
                        org.bukkit.block.Block b = new Location(start.getWorld(), x, y, z).getBlock();
                        if ((b.getType() == Material.AIR || (!b.getType().equals(Material.CARROT) && !b.getType().equals(Material.POTATO) && !b.getType().equals(Material.CROPS)
                                && !b.getType().toString().contains("SIGN") && !b.getType().isSolid())) && Tools.checkWorldGuard(b.getLocation(), p, "PVP", false)) {
                            list.put(b, b.getType().toString() + " " + b.getData());
                            b.setType(Material.ICE);
                            b.setMetadata("ce.Ice", new FixedMetadataValue(getPlugin(), null));
                        }
                    }
                }
        return list;
    }

    @Override
    public void initConfigEntries() {
        SlowStrength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SlowStrength"));
        SlowDuration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SlowDuration"));
        chanceFreeze = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceFreeze"));
        SpecialFreezeDuration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SpecialFreezeDuration"));
        chanceSpecialFreeze = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceSpecialFreeze"));
        specialFreeze = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".SpecialFreeze"));
    }
}
