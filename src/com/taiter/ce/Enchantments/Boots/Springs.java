package com.taiter.ce.Enchantments.Boots;

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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Main;
import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class Springs extends CEnchantment {

	int	strength;

	public Springs(String originalName, Application app, int enchantProbability, int occurrenceChance) {
		super(originalName,  app, enchantProbability, occurrenceChance);
		configEntries.add("Strength: 4");
		triggers.add(Trigger.MOVE);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;
		Player player = event.getPlayer();
		if(Main.repeatPotionEffects)
			Tools.repeatPotionEffect(item, player, PotionEffectType.JUMP, strength+level-1, true, this);
		else {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, strength + level-1), true);
			generateCooldown(player, 100l);	
		}
		}

	@Override
	public void initConfigEntries() {
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;		
	}
}
