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



import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class Ice extends CEnchantment {

	int		SlowStrength;
	int		SlowDuration;
	int		chanceFreeze;
	int     SpecialFreezeDuration;
	int		chanceSpecialFreeze;
	boolean	specialFreeze;

	public Ice(String originalName, Application app, int enchantProbability, int occurrenceChance) {
		super(originalName,  app, enchantProbability, occurrenceChance);
		configEntries.add("SlowStrength: 5");
		configEntries.add("SlowDuration: 40");
		configEntries.add("ChanceFreeze: 60");
		configEntries.add("SpecialFreeze: true");
		configEntries.add("SpecialFreezeDuration: 60");
		configEntries.add("ChanceSpecialFreeze: 10");
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		int i = Tools.random.nextInt(100);

		if(i < chanceFreeze) {
			((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SlowDuration, SlowStrength, false), true);
			event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.DIG_SNOW, 4f, 2f);
		}
		if(specialFreeze) {
			if(i < chanceSpecialFreeze) {
				if(event.getEntity() instanceof LivingEntity) {
				LivingEntity ent = (LivingEntity) event.getEntity();

				ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SpecialFreezeDuration + 20, 10));

				final HashMap<Block, Material> list = getIgloo(ent.getLocation(), 3, (Player) event.getDamager());

				generateCooldown((Player) event.getDamager(), SpecialFreezeDuration);
				
				new BukkitRunnable() {

					@Override
					public void run() {
						for(Entry<Block, Material> b : list.entrySet()) {
							b.getKey().setType(b.getValue());
							b.getKey().removeMetadata("c.Ice", getPlugin());
						}
					}
				}.runTaskLater(getPlugin(), SpecialFreezeDuration);
				}
			}
		}
	}
	
	

private HashMap<Block, Material> getIgloo(Location start, int size, Player p) {
HashMap<Block, Material> list = new HashMap<Block, Material>();
int bx = start.getBlockX();
int by = start.getBlockY();
int bz = start.getBlockZ();

for(int x = bx-size; x <= bx+size; x++)
for(int y = by-size; y <= by+size; y++)
for(int z = bz-size; z <= bz+size; z++) {
	double distancesquared = (bx-x)*(bx-x) + ((bz-z)*(bz-z)) + ((by-y)*(by-y));
	if(distancesquared < (size*size) && distancesquared >= ((size-1)*(size-1))) {
		org.bukkit.block.Block b = new Location(start.getWorld(), x, y, z).getBlock();
		if((b.getType() == Material.AIR || (!b.getType().equals(Material.CARROT) && !b.getType().equals(Material.POTATO) && !b.getType().equals(Material.CROPS) && !b.getType().toString().contains("SIGN") && !b.getType().isSolid())) && Tools.checkWorldGuard(b.getLocation(), p, "BUILD")) {
			list.put(b, b.getType());
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
