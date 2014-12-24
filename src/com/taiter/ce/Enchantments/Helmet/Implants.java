package com.taiter.ce.Enchantments.Helmet;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Implants extends CEnchantment {

	public int	burstDelay;
	List<Player> inWater = new ArrayList<Player>();

	public Implants(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("FoodBurstDelay: 20");
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;
		int hunger = event.getPlayer().getFoodLevel();
		final int air = event.getPlayer().getRemainingAir();
		final Player player  = event.getPlayer();
		
		if(hunger < 20)
			player.setFoodLevel(hunger + level);
		if(air < event.getPlayer().getMaximumAir())
			if(!inWater.contains(player)) {
				inWater.add(player);
				new BukkitRunnable() {
					@Override
					public void run() {
						if(player.isOnline() && !player.isDead() && player.getInventory().getBoots() != null && player.getInventory().getBoots().hasItemMeta() && player.getInventory().getBoots().getItemMeta().hasLore() && player.getInventory().getBoots().getItemMeta().getLore().contains(Main.enchantments.get(23).getDisplayName())) {
						if(player.getLocation().getBlock().getRelative(0, 1, 0).getType() != Material.WATER || player.getLocation().getBlock().getRelative(0, 1, 0).getType() != Material.STATIONARY_WATER)
							this.cancel();
						if(player.getRemainingAir() < player.getMaximumAir())
							player.setRemainingAir(player.getRemainingAir() + level);
						} else
							this.cancel();
					}
				}.runTaskTimer(Main.plugin, 20l, 60l);
			}
		generateCooldown(event.getPlayer(), burstDelay);
		
	}

	@Override
	public void initConfigEntries() {
		this.burstDelay = Integer.parseInt(Main.config.getString("Enchantments." + getOriginalName() + ".FoodBurstDelay"));	
	}
}
