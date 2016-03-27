package com.taiter.ce.Enchantments.Bow;

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



import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Bombardment extends CEnchantment {

	int	Volume;
	int TNTAmount;

	public Bombardment(Application app) {
		super(app);		
		configEntries.put("BaseTNTAmount", 3);
		configEntries.put("Volume", 1);
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@SuppressWarnings("deprecation")
    @Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Entity target = event.getEntity();

		final World world = target.getWorld();
		Vector vec = new Vector(0, -5, 0);
		Location spawnLocation = new Location(world, target.getLocation().getX(), 255, target.getLocation().getZ());
		final FallingBlock b = world.spawnFallingBlock(spawnLocation, 46, (byte) 0x0);
		b.setVelocity(vec);

		new BukkitRunnable() {

			Location	l	= b.getLocation();

			@Override
			public void run() {
				l = b.getLocation();
				if(b.isDead()) {
					l.getBlock().setType(Material.AIR);
					for(int i = 0; i <= TNTAmount + level; i++) {
						TNTPrimed tnt = world.spawn(l, TNTPrimed.class);
						tnt.setFuseTicks(0);
						if(!Main.createExplosions)
							tnt.setMetadata("ce.explosive", new FixedMetadataValue(getPlugin(), null));
					}
					this.cancel();
				}
				
				EffectManager.playSound(l, "ENTITY_ENDERDRAGON_GROWL", Volume, 2f);
			}
		}.runTaskTimer(getPlugin(), 0l, 5l);
		}
	}

	@Override
	public void initConfigEntries() {
		Volume = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Volume"));
		TNTAmount = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".BaseTNTAmount"));
	}

}
