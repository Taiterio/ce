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



import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.taiter.ce.Enchantments.CEnchantment;



public class Volley extends CEnchantment {

	// Constants
	private static final int CONE_DEGREES = 45; // The volley will spawn a cone of CONE_DEGREES in front of the player.

	public Volley(Application app) {
		super(app);
		triggers.add(Trigger.SHOOT_BOW); // Shooting a bow triggers a volley (spawning extra arrows)
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		if(e instanceof EntityShootBowEvent) {
			//Spawn arrows with a slightly altered direction, based on the location of the first arrow.
			volley((EntityShootBowEvent)e,item,level);
		}
	}

	// Helper method that spawns the volley.
	private void volley(EntityShootBowEvent e, ItemStack item, int lvl) {
		Player p = (Player) e.getEntity();
		int amount = 1 + 2*lvl; // Keep amount of arrows uneven, 2 extra arrows in a volley per level.
		
		Arrow oldArrow =  (Arrow) e.getProjectile();
		Vector velocity = oldArrow.getVelocity();
		oldArrow.remove(); // Remove original arrow.
		
		
		double angleBetweenArrows = (CONE_DEGREES / (amount-1)) * Math.PI/180;
		double pitch = (p.getLocation().getPitch() + 90) * Math.PI / 180;
		double yaw  = (p.getLocation().getYaw() + 90 -CONE_DEGREES/2)  * Math.PI / 180;
		
		// Starting direction values for the cone, each arrow increments it's direction on these values.
		double sZ = Math.cos(pitch);
		
		for (int i = 0; i < amount; i++){ // spawn all arrows in a cone of 90 degrees (equally distributed).;
			double nX = Math.sin(pitch) * Math.cos(yaw + angleBetweenArrows*i);
			double nY = Math.sin(pitch) * Math.sin(yaw + angleBetweenArrows*i);
			Vector newDir = new Vector(nX,sZ,nY);
			
			Arrow arrow = p.launchProjectile(Arrow.class);
			arrow.setShooter(p);
			// Need to make sure arrow has same speed as original arrow.
			arrow.setVelocity(newDir.normalize().multiply(velocity.length()));
			arrow.setMetadata("ce.Volley", new FixedMetadataValue(getPlugin(), null)); //Control metadata to prevent players from duplicating arrows
		}
	}

	@Override
	public void initConfigEntries() {
	}
}
