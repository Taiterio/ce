package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Main;


public class DruidBoots extends CItem {

	int SpeedDuration;
	int SpeedLevel;
	int RegenerationDuration;
	int RegenerationLevel;
	
	public DruidBoots(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("SpeedDuration: 100");
		this.configEntries.add("SpeedLevel: 5");
		this.configEntries.add("RegenerationDuration: 100");
		this.configEntries.add("RegenerationLevel: 5");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(player.hasPotionEffect(PotionEffectType.SPEED) && player.hasPotionEffect(PotionEffectType.REGENERATION))
			return false;
		Material t = player.getLocation().getBlock().getRelative(0,-1,0).getLocation().getBlock().getType();
		if(t.equals(Material.GRASS) || t.equals(Material.DIRT) || t.equals(Material.LEAVES)){
			if(Main.tools.repeatPotionEffects) {
				Main.tools.repeatPotionEffect(player.getInventory().getBoots(), player, PotionEffectType.SPEED, SpeedLevel, this);
				Main.tools.repeatPotionEffect(player.getInventory().getBoots(), player, PotionEffectType.REGENERATION, RegenerationLevel, this);
			} else {
				if(!player.hasPotionEffect(PotionEffectType.SPEED))
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SpeedDuration , SpeedLevel), true);
				if(!player.hasPotionEffect(PotionEffectType.REGENERATION))
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, RegenerationDuration , RegenerationLevel), true);
			}
			
				
		}
		return true;
	}

	@Override
	public void initConfigEntries() {
		SpeedDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SpeedDuration"));
		SpeedLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SpeedLevel")) - 1;
		RegenerationDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".RegenerationDuration"));
		RegenerationLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".RegenerationLevel"))-1;
	}

}
