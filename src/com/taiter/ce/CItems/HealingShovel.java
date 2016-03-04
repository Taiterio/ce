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


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


public class HealingShovel extends CItem {

	int Heal;
	
	public HealingShovel(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("Heal: 4");
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public boolean effect(Event event, Player player) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		if(e.getDamager() == player && e.getEntity() instanceof Player) {
		Player damaged = (Player) e.getEntity();
		e.setDamage(0);
		damaged.getWorld().playSound(
				damaged.getLocation(),
				Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
		short currentDur = player.getInventory().getItemInMainHand()
				.getDurability();

		if (((Damageable) damaged).getHealth() + Heal <= ((Damageable) damaged).getMaxHealth()) {
			damaged.setHealth(((Damageable) damaged).getHealth()
					+ Heal);
		} else {
			damaged.setHealth(((Damageable) damaged).getMaxHealth());
		}

			if (currentDur + Heal < player.getInventory().getItemInMainHand().getType().getMaxDurability()) {
			player.getInventory().getItemInMainHand()
					.setDurability(
							(short) (currentDur + Heal));
			} else {
			player.getInventory().setItemInMainHand(new ItemStack(
					Material.AIR, 1));
			player.getWorld().playSound(
					player.getLocation(),
					Sound.ENTITY_ITEM_BREAK, 0.1f, 0f);
			}
			return true;
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		
		Heal = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Heal"));

	}

}
