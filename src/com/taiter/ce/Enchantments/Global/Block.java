package com.taiter.ce.Enchantments.Global;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Block extends CEnchantment {

	int	strength;
	int	cooldown;

	public Block(Application app) {
		super(app);		
		configEntries.put("Strength", 1);
		configEntries.put("Cooldown", 600);
		triggers.add(Trigger.INTERACT_RIGHT);
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		PlayerInteractEvent event = (PlayerInteractEvent) e;
		final Player owner = event.getPlayer();

		EffectManager.playSound(event.getPlayer().getLocation(), "BLOCK_ANVIL_LAND", 0.8f, 1);
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
