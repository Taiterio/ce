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



import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class BountyHunter extends CEnchantment {
	
	int MaximumBounty;
	long Cooldown;
	double ChanceEmerald;
	double ChanceDiamond;
	double ChanceGold;
	double ChanceIron;

	public BountyHunter(Application app) {
		super(app);		
		this.configEntries.put("MaximumBounty", 8);
		this.configEntries.put("Cooldown", 600);
		this.configEntries.put("ChanceEmerald", 2.5);
		this.configEntries.put("ChanceDiamond", 5);
		this.configEntries.put("ChanceGold", 15);
		this.configEntries.put("ChanceIron", 27.5);
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Entity target = event.getEntity();
		
		if(!(target instanceof Player))
			return;
		
		Player p = (Player) ((Projectile) event.getDamager()).getShooter();
		
		Material bountyDrop = getBounty();
		
		for(int i = 10; i>0; i--) {
			p.getWorld().playEffect(p.getLocation(), Effect.COLOURED_DUST, 10);
			p.getWorld().playEffect(target.getLocation(), Effect.COLOURED_DUST, 10);
		}
		
		p.getInventory().addItem(new ItemStack(bountyDrop, Tools.random.nextInt(MaximumBounty+level)+1));
		p.sendMessage(ChatColor.GOLD + "You have collected a bounty on " + target.getName() + "!");
		this.generateCooldown(p, Cooldown);
		
		}
	}
	
	private Material getBounty() {
		double rand = Tools.random.nextDouble() *100;
		double currentChance = ChanceEmerald;
		
		if(rand < currentChance)
			return Material.EMERALD;
		currentChance += ChanceDiamond;
		
		if(rand < currentChance)
			return Material.DIAMOND;
		currentChance += ChanceGold;
		
		if(rand < currentChance)
			return Material.GOLD_INGOT;
		currentChance += ChanceIron;
		
		if(rand < currentChance)
			return Material.IRON_INGOT;
		return Material.COAL;
	}

	@Override
	public void initConfigEntries() {
		MaximumBounty = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".MaximumBounty"));
		Cooldown	  = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));
		ChanceEmerald = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceEmerald"));
		ChanceDiamond = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceDiamond"));
		ChanceGold 	  = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceGold"));
		ChanceIron    = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceIron"));
	}

}
