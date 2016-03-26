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
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;



public class Powergloves extends CItem {

	int	ThrowSpeedMultiplier;
	int	ThrowDelayAfterGrab;
	int	MaxGrabtime;

	public Powergloves(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("ThrowSpeedMultiplier", 60);
		this.configEntries.put("ThrowDelayAfterGrab", 20);
		this.configEntries.put("MaxGrabtime", 10);
		triggers.add(Trigger.INTERACT_RIGHT);
		triggers.add(Trigger.INTERACT_ENTITY);
	}

	@Override
	public boolean effect(Event event, final Player player) {
		if(event instanceof PlayerInteractEntityEvent) {
			PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
			e.setCancelled(true);
			final Entity clicked = e.getRightClicked();
			if(!player.hasMetadata("ce." + getOriginalName()))
				if(!clicked.getType().equals(EntityType.PAINTING) && !clicked.getType().equals(EntityType.ITEM_FRAME) && clicked.getPassenger() != player && player.getPassenger() == null) {
					player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, false));

					player.setPassenger(clicked);

					player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_CHEW_IRON_DOOR, 10);

					new BukkitRunnable() {

						@Override
						public void run() {
							player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 10);
							player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, true));
							this.cancel();
						}
					}.runTaskLater(main, ThrowDelayAfterGrab);

					new BukkitRunnable() {

						int			GrabTime	= MaxGrabtime;
						ItemStack	current		= player.getItemInHand();

						@Override
						public void run() {
							if(current.equals(player.getItemInHand())) {
								current = player.getItemInHand();
							if(GrabTime > 0) {
								if(!player.hasMetadata("ce." + getOriginalName())) {
									this.cancel();
								}
								GrabTime--;
							} else if(GrabTime <= 0) {
								if(player.hasMetadata("ce." + getOriginalName())) {
									player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
									player.removeMetadata("ce." + getOriginalName(), main);
									generateCooldown(player, getCooldown());
								}
								clicked.leaveVehicle();
								this.cancel();
							}
						  } else {
							  player.removeMetadata("ce." + getOriginalName(), main);
							  generateCooldown(player, getCooldown());
							  this.cancel();
						  }
						}
					}.runTaskTimer(main, 0l, 10l);
				}
		} else if(event instanceof PlayerInteractEvent) {
			if(player.hasMetadata("ce." + getOriginalName()) && player.getMetadata("ce." + getOriginalName()).get(0).asBoolean())
					if(player.getPassenger() != null) {
						Entity passenger = player.getPassenger();
						player.getPassenger().leaveVehicle();
						passenger.setVelocity(player.getLocation().getDirection().multiply(ThrowSpeedMultiplier));
						player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
						player.removeMetadata("ce." + getOriginalName(), main);
						return true;
					}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		ThrowDelayAfterGrab = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowDelayAfterGrab"));
		MaxGrabtime = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaxGrabtime"));
		ThrowSpeedMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowSpeedMultiplier"));
	}

}
