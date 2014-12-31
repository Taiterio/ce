package com.taiter.ce.CItems;


import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.taiter.ce.Tools;



public class NecromancersStaff extends CItem {

	List<String>	spells	= Arrays.asList(new String[] { ChatColor.GRAY + "Spell: " + ChatColor.DARK_GRAY + "Wither's Apprentice", ChatColor.GRAY + "Spell: " + ChatColor.DARK_RED + "Fireball", ChatColor.GRAY + "Spell: " + ChatColor.DARK_BLUE + "Lightning Strike" });

	Material		Fuel;

	int				WitherCost;
	int				FireballCost;
	int				LightningCost;
	int				WitherCooldown;
	int				FireballCooldown;
	int				LightningCooldown;

	public NecromancersStaff(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("Fuel: 377");
		this.configEntries.add("WitherCost: 10");
		this.configEntries.add("WitherCooldown: 100");
		this.configEntries.add("FireballCost: 35");
		this.configEntries.add("FireballCooldown: 60");
		this.configEntries.add("LightningCost: 20");
		this.configEntries.add("LightningCooldown: 2400");
		
	}

	@Override
	public boolean effect(Event event, Player player) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		ItemMeta im = e.getPlayer().getItemInHand().getItemMeta();
		List<String> lore = im.getLore();
		int lastElement = lore.size() - 1;

		if(e.getAction().toString().startsWith("LEFT")) {

			int nextSpellIndex = spells.indexOf(lore.get(lastElement)) + 1;

			if(nextSpellIndex == 3)
				nextSpellIndex = 0;

			String nextSpell = spells.get(nextSpellIndex);

			lore.set(lastElement, nextSpell);

			player.sendMessage(ChatColor.GRAY + "Changed Spell to " + nextSpell.split(": ")[1] + ChatColor.GRAY + ".");
			player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
		} else if(e.getAction().toString().startsWith("RIGHT")) {

			int spell    = -1;
			int cost     = -1;
			int cooldown = -1;

			// Get all values
			if(lore.get(lastElement).equals(spells.get(0))) {
				spell = 0;
				cost = WitherCost;
				cooldown = WitherCooldown;
			} else if(lore.get(lastElement).equals(spells.get(1))) {
				spell = 1;
				cost = FireballCost;
				cooldown = FireballCooldown;
			} else if(lore.get(lastElement).equals(spells.get(2))) {
				spell = 2;
				cost = LightningCost;
				cooldown = LightningCooldown;
			}

			// Apply costs
			if(player.getInventory().contains(Fuel, cost) || player.getGameMode().equals(GameMode.CREATIVE)) {
				if(!player.getGameMode().equals(GameMode.CREATIVE)) {
					ItemStack mana = new ItemStack(Fuel, cost);
					player.getInventory().removeItem(mana);
					player.updateInventory();
				}

				Location l = player.getLocation();
				
				// Apply effect
				if(spell == 0) {
					if(Tools.checkWorldGuard(l, player, DefaultFlag.TNT)) {
						player.launchProjectile(WitherSkull.class).setVelocity(l.getDirection().multiply(2));
						player.getWorld().playSound(l, Sound.WITHER_IDLE, 0.5f, 10f);
					} else {
						player.getWorld().playSound(l, Sound.CAT_HISS, 0.3f, 5f);
					}
				} else if(spell == 1) {
					player.launchProjectile(SmallFireball.class).setVelocity(l.getDirection().multiply(1.5));
					player.getWorld().playSound(l, Sound.BLAZE_HIT, 0.2f, 0f);
				} else if(spell == 2) {
					Location target = player.getTargetBlock(null, 20).getLocation();
					player.getWorld().strikeLightning(target);
					if(Tools.checkWorldGuard(l, player, DefaultFlag.TNT))
							player.getWorld().createExplosion(target, 1);
					player.getWorld().playSound(target, Sound.ENDERDRAGON_GROWL, 5f, 9999f);
				}

				// Generate the cooldown based on the cooldown value
				generateCooldown(player, cooldown);
			} else {
				player.sendMessage(ChatColor.RED + "You need " + cost + " " + Fuel.toString().toLowerCase().replace("_", " ") + " to cast this spell.");
			}

		}
		return false; // The Staff generates it's own cooldowns, so it always
						// returns false
	}

	@Override
	public void initConfigEntries() {
		Fuel		 		= Material.getMaterial(Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Fuel")));
		LightningCost 		= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".LightningCost"));
		LightningCooldown 	= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".LightningCooldown"));
		WitherCost 			= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WitherCost"));
		WitherCooldown 		= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WitherCooldown"));
		FireballCost 		= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireballCost"));
		FireballCooldown	= Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireballCooldown"));
		this.description.add(spells.get(0));
	}

}