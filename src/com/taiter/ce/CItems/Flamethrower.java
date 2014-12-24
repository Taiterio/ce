package com.taiter.ce.CItems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;



public class Flamethrower extends CItem {

	Boolean IsReloadable;
	int BurnDuration;
	int FireBlocksPerBurst;
	
	public Flamethrower(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("IsReloadable: true");
		this.configEntries.add("BurnDuration: 100");
		this.configEntries.add("FireBlocksPerBurst: 10");
	}

	@Override
	public boolean effect(Event event, final Player player) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			e.setCancelled(true);

			if(player.getItemInHand().getDurability() >= 64) {
				if(IsReloadable) {
				addLock(player);
				player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 2);
				player.sendMessage(ChatColor.RED + "Reloading...");
				new BukkitRunnable() {
					@Override
					public void run() {
						if(player.getItemInHand().getDurability() == 0) {
							player.removeMetadata("ce." + getOriginalName() + ".lock", main);
							player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 2);
							this.cancel();
						} else {
							player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() - 1));
						}
					}
				}.runTaskTimer(main, 0l, 2l);
			} else { 
				player.getItemInHand().setType(Material.AIR);
			}
			} else {
				final List<Location> list = getLinePlayer(player, FireBlocksPerBurst);
				for(final Location l: list) {
					if(l.getBlock().getType().equals(Material.AIR)) {
					l.getBlock().setType(Material.FIRE);
					}
					l.getWorld().playEffect(l, Effect.SMOKE, 20);
					final FallingBlock fire = l.getWorld().spawnFallingBlock(l, Material.FIRE.getId(), (byte) 0x00);
					fire.setDropItem(false);
					fire.setVelocity(player.getLocation().getDirection());
					new BukkitRunnable() {
						@Override
						public void run() {
						if(fire.isDead()) {
							list.add(fire.getLocation());
							this.cancel();
						} else {
							if(fire.getLocation().getBlock().getType().equals(Material.WATER) || fire.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
								fire.getWorld().playEffect(fire.getLocation(), Effect.EXTINGUISH, 60);
								fire.remove();
								this.cancel();
							}
							for(Entity ent:fire.getNearbyEntities(0, 0, 0)) {
								if(ent != player) {
									ent.setFireTicks(BurnDuration);
								}
							}
						}
						}
					}.runTaskTimer(main, 0l, 1l);
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Location ls:list) {
							if(ls.getBlock().getType().equals(Material.FIRE)) {
							ls.getWorld().playEffect(ls, Effect.EXTINGUISH, 60);
							ls.getBlock().setType(Material.AIR);
							}
						}
					}
				}.runTaskLater(main, 200l);
				player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 30);
				if(!player.getGameMode().equals(GameMode.CREATIVE)) {
				player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));
				}
			}
		}
		
		return true;
	}

	@Override
	public void initConfigEntries() {
		BurnDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".BurnDuration"));
		FireBlocksPerBurst = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FireBlocksPerBurst"));
		IsReloadable = Boolean.parseBoolean(getConfig().getString("Items." + getOriginalName() + ".IsReloadable"));
		
	}
	
	public List<Location> getLinePlayer(Player player, int length) {
		List<Location> list = new ArrayList<Location>();
		for(int amount = length; amount > 0; amount --) {
			list.add(player.getTargetBlock(null, amount).getLocation());
		}
		return list;
	}

}
