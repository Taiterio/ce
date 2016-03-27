package com.taiter.ce.CItems;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.taiter.ce.EffectManager;


public class Deathscythe extends CItem {

	int Range;
	int MaximumHookTime;
	
	public Deathscythe(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("Range", 10);
		this.configEntries.put("MaximumHookTime", 240);
		triggers.add(Trigger.INTERACT);
	}

	@Override
	public boolean effect(Event event, final Player player) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		
		if(player.hasMetadata("ce." + getOriginalName())) {
		
			Entity target = null;
			
			for(Entity ent : player.getNearbyEntities(Range*2, Range*2, Range*2))
				if(ent.getEntityId() == player.getMetadata("ce." + getOriginalName()).get(0).asInt())
					target = ent;
			
			if(target == null) {
				player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "The Soul escaped!");
				return true;
			}
			
			Vector vec;
			
			
			
			if(e.getAction().toString().startsWith("RIGHT")) {
				e.setCancelled(true);
				
				vec = player.getLocation().subtract(target.getLocation()).toVector();
				vec.normalize();
				vec.add(new Vector(0, 0.25, 0));
				vec.multiply(2);
				target.setFallDistance(-10);
				target.setVelocity(vec);
				
			} else if(e.getAction().toString().startsWith("LEFT")) {
				
				vec = target.getLocation().subtract(player.getLocation()).toVector();
				vec.normalize();
				vec.add(new Vector(0, 0.25, 0));
				vec.multiply(2);
				player.setFallDistance(-10);
				player.setVelocity(vec);
				
			}
			
			player.removeMetadata("ce." + getOriginalName(), main);
			return true;
			
		}
		
		if(e.getAction().toString().startsWith("RIGHT"))  {
			e.setCancelled(true);
		Location loc = player.getLocation();
		List<Entity> ents = player.getNearbyEntities(Range, Range, Range);
		
		for(int i = ents.size()-1; i >= 0; i--)
			if(!(ents.get(i) instanceof LivingEntity))
				ents.remove(i);
		
		if(ents.isEmpty()) {
			EffectManager.playSound(loc, "BLOCK_PORTAL_TRAVEL", 0.01f, 100f);
			player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "There are no souls nearby");
			return true;
		}
			
		for(int i = 1; i < Range; i++) {
			loc = player.getTargetBlock((Set<Material>)null, i).getLocation();
			if(!loc.getBlock().getType().equals(Material.AIR))
				return true;
			loc.getWorld().playEffect(loc, Effect.SMOKE, Range*2);

			for(Entity ent : ents) {
				Location entLoc = ent.getLocation();
				if( Math.abs(entLoc.getBlockX() - loc.getBlockX()) < 2 && Math.abs(entLoc.getBlockY() - loc.getBlockY()) < 2 &&Math.abs(entLoc.getBlockZ() - loc.getBlockZ()) < 2) {
					EffectManager.playSound(ent.getLocation(), "BLOCK_PISTON_EXTEND", 0.1f, 0.3f);
					player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "You caught a Soul!");
					player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, ent.getEntityId()));
					new BukkitRunnable() {
						@Override
						public void run() {
							if(player.hasMetadata("ce." + getOriginalName())) {
								player.removeMetadata("ce." + getOriginalName(), main);
								player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "The Soul escaped!");
							}
						}
					}.runTaskLater(main, MaximumHookTime);
					return false;
				}
			}
		}
		

		player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "You missed!");
		EffectManager.playSound(loc, "BLOCK_PORTAL_TRAVEL", 0.01f, 100f);
			
		}
		return true;
	}

	@Override
	public void initConfigEntries() {
		Range = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Range"));
		MaximumHookTime = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaximumHookTime"));

	}

}
