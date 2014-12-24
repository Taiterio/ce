package com.taiter.ce.Enchantments.Helmet;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Glowing extends CEnchantment {


	public Glowing(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;
		Player player = event.getPlayer();
		if(Main.tools.repeatPotionEffects)
			Main.tools.repeatPotionEffect(item, player, PotionEffectType.NIGHT_VISION, 0, true, this);
		else {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 0), true);
			generateCooldown(player, 400l);	
		}
	}

	@Override
	public void initConfigEntries() {
	}
}
