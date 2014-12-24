package com.taiter.ce.CItems;


import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;



public class Powergloves extends CItem {

	int	ThrowSpeedMultiplier;
	int	ThrowDelayAfterGrab;
	int	MaxGrabtime;

	public Powergloves(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("ThrowSpeedMultiplier: 60");
		this.configEntries.add("ThrowDelayAfterGrab: 20");
		this.configEntries.add("MaxGrabtime: 10");
	}

	@Override
	public boolean effect(Event event, final Player player) {
		if(event instanceof PlayerInteractEntityEvent) {
			PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
			e.setCancelled(true);
			final Entity clicked = e.getRightClicked();
			if(!player.hasMetadata("ce." + getOriginalName()))
				if(!clicked.getType().equals(EntityType.PAINTING) && !clicked.getType().equals(EntityType.ITEM_FRAME) && clicked.getPassenger() != player && player.getPassenger() == null) {
					player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, false));

					player.setPassenger(clicked);

					player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_CHEW_IRON_DOOR, 10);

					new BukkitRunnable() {

						@Override
						public void run() {
							player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 10);
							player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, true));
							this.cancel();
						}
					}.runTaskLater(main, ThrowDelayAfterGrab);

					new BukkitRunnable() {

						int			GrabTime	= MaxGrabtime;
						ItemStack	current		= player.getItemInHand();

						@Override
						public void run() {
							if(current.equals(player.getItemInHand())) {
								current = player.getItemInHand();
							if(GrabTime > 0) {
								if(!player.hasMetadata("ce." + getOriginalName())) {
									this.cancel();
								}
								GrabTime--;
							} else if(GrabTime <= 0) {
								if(player.hasMetadata("ce." + getOriginalName())) {
									player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
									player.removeMetadata("ce." + getOriginalName(), main);
									generateCooldown(player, cooldownTime);
								}
								clicked.leaveVehicle();
								this.cancel();
							}
						  } else {
							  player.removeMetadata("ce." + getOriginalName(), main);
							  generateCooldown(player, cooldownTime);
							  this.cancel();
						  }
						}
					}.runTaskTimer(main, 0l, 10l);
				}
		} else if(event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			if(player.hasMetadata("ce." + getOriginalName()) && player.getMetadata("ce." + getOriginalName()).get(0).asBoolean())
				if(e.getAction().toString().startsWith("RIGHT"))
					if(player.getPassenger() != null) {
						Entity passenger = player.getPassenger();
						player.getPassenger().leaveVehicle();
						passenger.setVelocity(player.getLocation().getDirection().multiply(ThrowSpeedMultiplier));
						player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
						player.removeMetadata("ce." + getOriginalName(), main);
						return true;
					}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		ThrowDelayAfterGrab = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowDelayAfterGrab"));
		MaxGrabtime = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaxGrabtime"));
		ThrowSpeedMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowSpeedMultiplier"));
	}

}
