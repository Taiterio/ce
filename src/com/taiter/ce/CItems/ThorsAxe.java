package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.taiter.ce.Tools;


public class ThorsAxe extends CItem {

	int FireDuration;
	
	public ThorsAxe(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("FireDuration: 200");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof PlayerInteractEvent) {
			final PlayerInteractEvent e = (PlayerInteractEvent) event;
				if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(e.getClickedBlock() != null) {
						Location loc = e.getClickedBlock().getLocation();
						loc.setY(loc.getY() +1);
					if(Tools.checkWorldGuard(loc, player, DefaultFlag.PVP)) {
						player.getWorld().strikeLightning(e.getClickedBlock().getLocation());
						if(loc.getBlock().getType().equals(Material.AIR)) {
							loc.getBlock().setType(Material.FIRE);
						}
						loc.setX(loc.getX() +1);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, DefaultFlag.PVP)) 
							loc.getBlock().setType(Material.FIRE);
						loc.setX(loc.getX() -2);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, DefaultFlag.PVP)) 
							loc.getBlock().setType(Material.FIRE);
						loc.setX(loc.getX() +1);
						loc.setZ(loc.getZ() +1);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, DefaultFlag.PVP)) 
							loc.getBlock().setType(Material.FIRE);
						loc.setZ(loc.getZ() -2);
						if(loc.getBlock().getType().equals(Material.AIR) && Tools.checkWorldGuard(loc, player, DefaultFlag.PVP)) 
							loc.getBlock().setType(Material.FIRE);
					
						player.getWorld().playSound(e.getClickedBlock().getLocation(), Sound.ENDERDRAGON_GROWL, 3f, 1f);
					
						player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));
						generateCooldown(player, cooldownTime);
					
						new BukkitRunnable() {
							@Override
							public void run() {
								Location loc = e.getClickedBlock().getLocation();
								loc.setY(loc.getY() +1);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
								loc.getBlock().setType(Material.AIR);
								}
								loc.setX(loc.getX() +1);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
								loc.getBlock().setType(Material.AIR);
								}
								loc.setX(loc.getX() -2);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
									loc.getBlock().setType(Material.AIR);
								}
								loc.setX(loc.getX() +1);
									loc.setZ(loc.getZ() +1);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
									loc.getBlock().setType(Material.AIR);
								}
								loc.setZ(loc.getZ() -2);
								if(loc.getBlock().getType().equals(Material.FIRE)) {
									loc.getBlock().setType(Material.AIR);
								}
								this.cancel();
								}
							}.runTaskLater(main, FireDuration);
						}
					}
				}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		FireDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireDuration"));
	}

}
