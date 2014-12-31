package com.taiter.ce.CItems;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;



public class HookshotBow extends CItem {

	String	PushLine	= ChatColor.DARK_GRAY + "Mode: Push";
	String	PullLine	= ChatColor.DARK_GRAY + "Mode: Pull";
	
	

	public HookshotBow(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);

	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			if(e.getAction().toString().startsWith("LEFT")) {
				ItemStack item = player.getItemInHand();
				ItemMeta im = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				if(im.hasLore())
					lore =  im.getLore();
				player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
				String newMode = ChatColor.DARK_GRAY + "Push";
				if(lore.contains(PushLine)) {
					lore.set(lore.size() - 1, PullLine);
					newMode = ChatColor.DARK_GRAY + "Pull";
				} else  {
					if(lore.size() == 0)
						lore.add(PushLine);
					else
						lore.set(lore.size()-1, PushLine);
				}
				
				im.setLore(lore);
				item.setItemMeta(im);
				player.sendMessage(ChatColor.GREEN + "Hookshot Mode: " + newMode);

			}
		} else if(event instanceof EntityShootBowEvent) {
			EntityShootBowEvent e = (EntityShootBowEvent) event;
			List<String> lore = e.getBow().getItemMeta().getLore();
			e.getProjectile().setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, ChatColor.stripColor(lore.get(lore.size() - 1)).split(": ")[1]));
			e.getProjectile().setMetadata("ce.bow", new FixedMetadataValue(main, this.getOriginalName()));
		} else if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			Projectile pr = ((Projectile) e.getDamager());
			Entity target = e.getEntity();
			
			Location targetLocation = target.getLocation();
			Location playerLocation = player.getLocation();
			
			
			Vector vec = targetLocation.subtract(playerLocation).toVector();
			// TODO: REWRITE
			//double distance = targetLocation.distance(playerLocation);
			if(pr.getMetadata("ce." + getOriginalName()).get(0).asString().equalsIgnoreCase("Push")) {
				vec.normalize();
				vec.multiply(2);
				vec.setY(0.4);
				player.setFallDistance(-10);
				player.setVelocity(vec);
			} else {
				vec = player.getLocation().subtract(target.getLocation()).toVector();
				vec.normalize();
				target.setFallDistance(-10);
				target.setVelocity(vec);
			}
		}

		return false;
	}

	@Override
	public void initConfigEntries() {
		this.description.add(PushLine);
	}

}
