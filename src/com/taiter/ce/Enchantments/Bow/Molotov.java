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



import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.taiter.ce.Enchantments.CEnchantment;



public class Molotov extends CEnchantment {


	public Molotov(Application app) {
		super(app);		
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@SuppressWarnings("deprecation")
    @Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Entity target = event.getEntity();

		World world = target.getWorld();
		world.playEffect(target.getLocation(), Effect.POTION_BREAK, 10);
		double boundaries = 0.1*level;
		for(double x = boundaries; x >= -boundaries; x-=0.1)
			for(double z = boundaries; z >= -boundaries; z-=0.1) {
				FallingBlock b = world.spawnFallingBlock(target.getLocation(), Material.FIRE.getId(), (byte) 0x0);
				b.setVelocity(new Vector(x, 0.1, z));
				b.setDropItem(false);
			}
		}
	}

	@Override
	public void initConfigEntries() {
	}

}
