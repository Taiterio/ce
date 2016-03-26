package com.taiter.ce.Enchantments.Tool;

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



import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Enchantments.CEnchantment;



public class Quickening extends CEnchantment {

	int Strength;
	int Duration;

	
	public Quickening(Application app) {
		super(app);		
		configEntries.put("Strength", 3);
		configEntries.put("Duration", 40);
		triggers.add(Trigger.BLOCK_BROKEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player player = event.getPlayer();
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Duration, Strength+level-1), false);
	}
	
	@Override
	public void initConfigEntries() {
		Strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;
		Duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
	}
	
}
