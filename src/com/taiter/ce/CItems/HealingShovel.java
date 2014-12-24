package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


public class HealingShovel extends CItem {

	int Heal;
	
	public HealingShovel(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("Heal: 4");
	}

	@Override
	public boolean effect(Event event, Player player) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		if(e.getEntity() instanceof Player) {
		Player damaged = (Player) e.getEntity();
		e.setDamage((double) 0);
		damaged.getWorld().playSound(
				damaged.getLocation(),
				Sound.ORB_PICKUP, 0.5f, 1f);
		short currentDur = player.getItemInHand()
				.getDurability();

		if (((Damageable) damaged).getHealth() + Heal <= ((Damageable) damaged).getMaxHealth()) {
			damaged.setHealth(((Damageable) damaged).getHealth()
					+ Heal);
		} else {
			damaged.setHealth(((Damageable) damaged).getMaxHealth());
		}

			if (currentDur + Heal < player.getItemInHand().getType().getMaxDurability()) {
			player.getItemInHand()
					.setDurability(
							(short) (currentDur + Heal));
			} else {
			player.setItemInHand(new ItemStack(
					Material.AIR, 1));
			player.getWorld().playSound(
					player.getLocation(),
					Sound.ITEM_BREAK, 0.1f, 0f);
			}
			return true;
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		
		Heal = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Heal"));

	}

}
