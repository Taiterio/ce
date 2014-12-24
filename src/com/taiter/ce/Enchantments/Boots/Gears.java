package com.taiter.ce.Enchantments.Boots;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Gears extends CEnchantment {

	int	strength;

	public Gears(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("SpeedBoost: 1");
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
			PlayerMoveEvent event = (PlayerMoveEvent) e;
			final Player player = event.getPlayer();
			if(Main.tools.repeatPotionEffects)
				Main.tools.repeatPotionEffect(item, player, PotionEffectType.SPEED, strength+level, true, this);
			else {
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, strength + level), true);
				generateCooldown(player, 100l);	
			}
	}

	@Override
	public void initConfigEntries() {
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SpeedBoost"))-1;
	}
}
