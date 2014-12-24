package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.taiter.ce.Main;


public class Pyroaxe extends CItem {

	int damageMultiplier;
	
	public Pyroaxe(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		configEntries.add("DamageMultiplier: 4");
	}

	@Override
	public boolean effect(Event event, Player player) {
		
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		Entity entity = e.getEntity();
		
		if(entity.getFireTicks() > 0) {
			e.setDamage(damageMultiplier * e.getDamage());
			entity.getWorld().playEffect(entity.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
			entity.getWorld().playSound(entity.getLocation(), Sound.ANVIL_LAND, 1f, 0.001f);
			return true;
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		damageMultiplier = Integer.parseInt(Main.config.getString("Items." + getOriginalName() + ".DamageMultiplier"));
	}

}
