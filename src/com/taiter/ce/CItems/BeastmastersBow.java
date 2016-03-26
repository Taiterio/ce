package com.taiter.ce.CItems;

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


import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.taiter.ce.EffectManager;


public class BeastmastersBow extends CItem {
	
	//Everything that is commented out is unfinished
	
//	int descriptionSize;
//	int MaximumMobStorage;
//	String placeHolder = ChatColor.GRAY + "" + ChatColor.ITALIC + "    None";
	
	int DamageMultiplication;
	int MobAppearanceChance;
	int MaximumMobs;
	
	Random rand;

	public BeastmastersBow(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
//		description.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Collected Beasts:");
//		description.add(placeHolder);
//		descriptionSize = this.description.size();
//		this.configEntries.put("MaximumMobStorage", 5);
		this.configEntries.put("MaximumMobs", 5);
		this.configEntries.put("MobAppearanceChance", 90);
		this.configEntries.put("DamageMultiplication", 5);
		rand = new Random();
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public boolean effect(Event event, Player player) {
//		  List<String> lore = e.getBow().getItemMeta().getLore();
//		  if(!lore.contains(placeHolder)) {
//			  for(int i = descriptionSize; i != 0; i--)
//				  lore.remove(i);
//			  e.getProjectile().setMetadata("ce." + this.getOriginalName(), new FixedMetadataValue(main, writeType(lore)));
//			  player.setMetadata("ce.CanUnleashBeasts", null);
//		  } else
//			  e.getProjectile().setMetadata("ce." + this.getOriginalName(), null);
		  if(event instanceof EntityDamageByEntityEvent) {
		  EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		  if(e.getDamager() != player)
			  return false;
		  Entity ent = e.getEntity();
		  Location loc = ent.getLocation();
		  World w = ent.getWorld();
			if(ent instanceof Silverfish || ent instanceof EnderDragon || ent instanceof Spider || ent instanceof Slime || ent instanceof Ghast || ent instanceof MagmaCube || ent instanceof CaveSpider || (ent instanceof Wolf && ((Wolf) ent).isAngry()) || ent instanceof PigZombie) {
					e.setDamage(e.getDamage()*DamageMultiplication);
					w.playEffect(loc, Effect.SMOKE, 50);
					w.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 50);
					EffectManager.playSound(loc, "BLOCK_PISTON_RETRACT", 1.3f, 3f);
				return true;
			} else if (ent instanceof Player) {
				for(int i = 0; i < MaximumMobs; i++) {
					if(rand.nextInt(100) < MobAppearanceChance) {
						w.spawnEntity(loc, rand.nextInt(2) == 1 ? EntityType.SPIDER : EntityType.SLIME);
						w.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 30);
						w.playEffect(loc, Effect.SMOKE, 30);
						EffectManager.playSound(loc, "BLOCK_ANVIL_BREAK", 0.3f, 0.1f);
					}
				}
			}
		}
		  return false;
	}

	@Override
	public void initConfigEntries() {
//		MaximumMobStorage = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaximumMobStorage"));
		MaximumMobs 			= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaximumMobs"));
		MobAppearanceChance 	= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MobAppearanceChance"));
		DamageMultiplication 	= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".DamageMultiplication"));
	}
	
//	private String writeType(List<String> lore) {
//		String result = "";
//		
//		for(String l : lore)
//			result += l + ";";
//		
//		return result;
//	}

}
