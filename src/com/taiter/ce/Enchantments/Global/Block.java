package com.taiter.ce.Enchantments.Global;


import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Enchantments.CEnchantment;



public class Block extends CEnchantment {

	int	strength;
	int	cooldown;

	public Block(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("Strength: 1");
		configEntries.add("Cooldown: 600");
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		PlayerInteractEvent event = (PlayerInteractEvent) e;
		final Player owner = event.getPlayer();

		event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ANVIL_LAND, 10, 10);
		new BukkitRunnable() {

			PotionEffect	resistance	= new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, strength + level);

			@Override
			public void run() {
				if(owner.isBlocking()) {
					owner.addPotionEffect(resistance);
				} else {
					generateCooldown(owner, cooldown);
					this.cancel();
				}
			}
		}.runTaskTimer(getPlugin(), 0l, 15l);
	}

	@Override
	public void initConfigEntries() {

		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;
		cooldown = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));
		
	}
}
