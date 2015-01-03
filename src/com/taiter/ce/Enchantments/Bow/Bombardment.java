package com.taiter.ce.Enchantments.Bow;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.taiter.ce.Enchantments.CEnchantment;



public class Bombardment extends CEnchantment {

	int	Volume;
	int TNTAmount;

	public Bombardment(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("BaseTNTAmount: 3");
		configEntries.add("Volume: 1");
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Entity target = event.getEntity();

		final World world = target.getWorld();
		Vector vec = new Vector(0, -5, 0);
		Location spawnLocation = new Location(world, target.getLocation().getX(), 255, target.getLocation().getZ());
		final FallingBlock b = world.spawnFallingBlock(spawnLocation, 46, (byte) 0x0);
		b.setVelocity(vec);

		new BukkitRunnable() {

			Location	l	= b.getLocation();

			@Override
			public void run() {
				l = b.getLocation();
				if(b.isDead()) {
					l.getBlock().setType(Material.AIR);
					for(int i = 0; i <= TNTAmount + level; i++) {
						world.spawn(l, TNTPrimed.class).setFuseTicks(0);
					}
					this.cancel();
				}
				
				world.playSound(l, Sound.ENDERDRAGON_GROWL, Volume, 5f); // First
																						// float
																						// =
																						// volume,
																						// 2nd
																						// Float
																						// =
																						// pitch
			}
		}.runTaskTimer(getPlugin(), 0l, 5l);
		}
	}

	@Override
	public void initConfigEntries() {
		Volume = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Volume"));
		TNTAmount = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".BaseTNTAmount"));
	}

}
