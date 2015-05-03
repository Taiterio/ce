package com.taiter.ce.Enchantments.Bow;

/*
* This file is written by _fantasm0_ for the CustomEnchants plugin.
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/



import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.taiter.ce.CEListener;
import com.taiter.ce.CEventHandler;
import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Volley extends CEnchantment {

	// Constants
	private static final int ARROW_AMOUNT_LVL1 = 3;
	private static final int ARROW_AMOUNT_LVL2 = 5;
	private static final int CONE_DEGREES = 45; // The volley will span a cone of CONE_DEGREES in front of the player.

	public Volley(String originalName, Application app, int enchantProbability, int occurrenceChance) {
		super(originalName, app, enchantProbability, occurrenceChance);
		triggers.add(Trigger.SHOOT_BOW); // Shooting a bow triggers a volley (spawning extra arrows)
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		if(e instanceof EntityShootBowEvent) {
			//spawn arrows with a slightly altered direction, based on the location of the first arrow.
			volley((EntityShootBowEvent)e,item,level);
			// If level is not 1 or 2, nothing happens.
		}
	}

	// Helper method that spawns the volley.
	private void volley(EntityShootBowEvent e, ItemStack item,int lvl) {
		Player p;
		try {
		p = (Player) e.getEntity();
		} catch (ClassCastException error){
			return; // If arrow is not shot by player --> do nothing.
		}
		int amount;
		if (lvl == 1){
			amount = ARROW_AMOUNT_LVL1;
		}
		else if (lvl == 2){
			amount = ARROW_AMOUNT_LVL2;
		}
		else {
			return; // Invalid lvl --> do nothing.
		}
		
		Vector velocity = e.getProjectile().getVelocity();
		e.getProjectile().remove(); // Remove original arrow.
		Double angleBetweenArrows = (CONE_DEGREES / (amount-1))*Math.PI/180;
		double pitch = (p.getLocation().getPitch() + 90) * Math.PI / 180;
		double yaw  = (p.getLocation().getYaw() + 90 -CONE_DEGREES/2)  * Math.PI / 180;
		// Starting direction values for the cone, each arrow increments it's direction on these values.
		double sX = Math.sin(pitch) * Math.cos(yaw);
		double sY = Math.sin(pitch) * Math.sin(yaw);
		double sZ = Math.cos(pitch);
		for (int i=0;i<amount;i++){ // spawn all arrows in a cone of 90 degrees (equally distributed).;
			double nX = Math.sin(pitch) * Math.cos(yaw+ angleBetweenArrows*i);
			double nY = Math.sin(pitch) * Math.sin(yaw+ angleBetweenArrows*i);
			Vector newDir = new Vector(nX,sZ,nY);
			Arrow arrow = p.launchProjectile(Arrow.class);
			arrow.setShooter(p);
			// Need to make sure arrow has same speed as original arrow.
			arrow.setVelocity(newDir.normalize().multiply(velocity.length())); 
			// Set the metaData of the arrow, so that all arrows support other custom enchants.
			String enchantments = this.getOriginalName() + " : " + lvl;
			if((e.getProjectile().hasMetadata("ce.bow.enchantment"))){
				  enchantments += " ; " + ((EntityShootBowEvent) e).getProjectile().getMetadata("ce.bow.enchantment").get(0).asString();
			}
		    arrow.setMetadata("ce.bow.enchantment", new FixedMetadataValue(Main.plugin, enchantments));
		}
	}

	@Override
	public void initConfigEntries() {
		// Nothing to initialise For this enchantment. What exactly configEntries used for?
		// Is there a way for users to change generated config entries for customization?
	}
}
