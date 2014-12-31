package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.taiter.ce.Main;
import com.taiter.ce.Tools;


public class LivefireBoots extends CItem {

	int FlameDuration;
	int FireResistanceLevel;
	
	public LivefireBoots(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("FlameDuration: 200");
		this.configEntries.add("FireResistanceLevel: 5");
	}

	@Override
	public boolean effect(Event event, final Player player) {
		  final PlayerMoveEvent e = (PlayerMoveEvent) event;
		  final Block b = e.getTo().getBlock();
		  
		  if(!Tools.checkWorldGuard(e.getTo(), player, DefaultFlag.PVP))
			  return false;
		  
		  if(Main.tools.repeatPotionEffects)
			  Main.tools.repeatPotionEffect(player.getInventory().getBoots(), player, PotionEffectType.FIRE_RESISTANCE, FireResistanceLevel, this);
		  else
			  player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, FlameDuration + 20, FireResistanceLevel, true), true);
		if(b.getType().equals(Material.AIR)) {
			b.setType(Material.FIRE);
			new BukkitRunnable() {
				@Override
				public void run() {
					if(b.getType().equals(Material.FIRE)) {
						player.getWorld().playEffect(e.getTo(), Effect.EXTINGUISH, 60);
						b.setType(Material.AIR);
					}
				}
			}.runTaskLater(main, FlameDuration);
		
		}
		return true;
	}

	@Override
	public void initConfigEntries() {
		FireResistanceLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireResistanceLevel"))-1;
		FlameDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FlameDuration"));
		
	}

}
