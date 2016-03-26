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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;



public class Swimsuit extends CItem {

	int	DamageBoostLevel;
	int	SpeedBoostLevel;
	public String[] parts = {
			ChatColor.BLUE + "Scuba Mask",
			ChatColor.BLUE + "Upper Swimsuit",
			ChatColor.BLUE + "Lower Swimsuit",
			ChatColor.BLUE + "Flippers"
	};

	public Swimsuit(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		configEntries.put("DamageBoostLevel", 4);
		configEntries.put("SpeedBoostLevel", 4);
		triggers.add(Trigger.MOVE);
	}// TODO: Use the Helmet as the main item

	@Override
	public boolean effect(Event event, final Player player) {

		if(player.getLocation().getBlock().getType().equals(Material.WATER) || player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
			if((player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().hasItemMeta() && player.getEquipment().getHelmet().getItemMeta().hasDisplayName() && player.getEquipment().getHelmet().getItemMeta().getDisplayName().equals(parts[0])) || (player.getEquipment().getChestplate() != null && player.getEquipment().getChestplate().hasItemMeta() && player.getEquipment().getChestplate().getItemMeta().hasDisplayName() && player.getEquipment().getChestplate().getItemMeta().getDisplayName().equals(parts[1])) || (player.getEquipment().getLeggings() != null && player.getEquipment().getLeggings().hasItemMeta() && player.getEquipment().getLeggings().getItemMeta().hasDisplayName() && player.getEquipment().getLeggings().getItemMeta().getDisplayName().equals(parts[2])) || (player.getEquipment().getBoots() != null && player.getEquipment().getBoots().hasItemMeta() && player.getEquipment().getBoots().getItemMeta().hasDisplayName() && player.getEquipment().getBoots().getItemMeta().getDisplayName().equals(parts[3]))) {

				addLock(player);

				new BukkitRunnable() {

					@Override
					public void run() {

						if(player.getLocation().getBlock().getType().equals(Material.WATER) || player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
							if((player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().hasItemMeta() && player.getEquipment().getHelmet().getItemMeta().hasDisplayName() && player.getEquipment().getHelmet().getItemMeta().getDisplayName().equals(parts[0])) || (player.getEquipment().getChestplate() != null && player.getEquipment().getChestplate().hasItemMeta() && player.getEquipment().getChestplate().getItemMeta().hasDisplayName() && player.getEquipment().getChestplate().getItemMeta().getDisplayName().equals(parts[1])) || (player.getEquipment().getLeggings() != null && player.getEquipment().getLeggings().hasItemMeta() && player.getEquipment().getLeggings().getItemMeta().hasDisplayName() && player.getEquipment().getLeggings().getItemMeta().getDisplayName().equals(parts[2])) || (player.getEquipment().getBoots() != null && player.getEquipment().getBoots().hasItemMeta() && player.getEquipment().getBoots().getItemMeta().hasDisplayName() && player.getEquipment().getBoots().getItemMeta().getDisplayName().equals(parts[3]))) {
								player.setRemainingAir(player.getMaximumAir());
								player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, SpeedBoostLevel), true);
								player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, DamageBoostLevel), true);

							} else {
								removeLock(player);
							}

						} else {
							removeLock(player);
						}
					}
				}.runTaskTimer(main, 0l, 80l);
			}

		}
		return true;
	}

	@Override
	public void initConfigEntries() {
		SpeedBoostLevel  = Integer.parseInt(getConfig().getString("Items.Swimsuit.SpeedBoostLevel"));
		DamageBoostLevel = Integer.parseInt(getConfig().getString("Items.Swimsuit.DamageBoostLevel"));
	}

}
