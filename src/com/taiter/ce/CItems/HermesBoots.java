package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Main;


public class HermesBoots extends CItem {
	
	int SpeedLevel;

	public HermesBoots(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("SpeedLevel: 5");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(Main.tools.repeatPotionEffects)
			  Main.tools.repeatPotionEffect(player.getInventory().getBoots(), player, PotionEffectType.SPEED, SpeedLevel, this);
		else 
			 player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) this.cooldownTime + 20, SpeedLevel, true), true);
		return true;
	}

	@Override
	public void initConfigEntries() {
		SpeedLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SpeedLevel")) - 1;
	}

}
