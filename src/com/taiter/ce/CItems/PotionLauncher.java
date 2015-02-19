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
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;



public class PotionLauncher extends CItem {
	
	int ProjectileSpeedMultiplier;

	public PotionLauncher(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("ProjectileSpeedMultiplier: 4");
		triggers.add(Trigger.INTERACT_RIGHT);
	}

	@Override
	public boolean effect(Event event, Player player) {

			int slot = player.getInventory().getHeldItemSlot();

			ItemStack potion = player.getInventory().getItem(slot + 1);
			Location loc = player.getLocation();
			if(potion != null && potion.getType().equals(Material.POTION)) {
				ThrownPotion tp = player.launchProjectile(ThrownPotion.class);
				player.getWorld().playSound(loc, Sound.EXPLODE, 1f, 10f);
				tp.setItem(potion);
				tp.setBounce(false);
				tp.setVelocity(loc.getDirection().multiply(ProjectileSpeedMultiplier));
				if(!player.getGameMode().equals(GameMode.CREATIVE)) {
					potion.setAmount(potion.getAmount() - 1);
					player.getInventory().setItem(slot + 1, potion);
					player.updateInventory();
				}
				return true;
			} else {
				player.sendMessage(ChatColor.RED + "You need a Potion in the slot to the right of the Potion Launcher!");
				player.getWorld().playEffect(loc, Effect.CLICK1, 5);
			}
		return false;
	}

	@Override
	public void initConfigEntries() {
		ProjectileSpeedMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ProjectileSpeedMultiplier"));
	}

}
