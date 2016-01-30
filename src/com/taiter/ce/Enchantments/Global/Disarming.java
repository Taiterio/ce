package com.taiter.ce.Enchantments.Global;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Disarming extends CEnchantment {

	public Disarming(Application app) {
		super(app);		
		triggers.add(Trigger.DAMAGE_GIVEN);
		}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getEntity();
		
		if(target instanceof Player) {
		    Player pTarget = (Player) target;
		    ItemStack hand = pTarget.getItemInHand();
		    Inventory inv = pTarget.getInventory();
		    if(inv.firstEmpty() != -1)
		        inv.addItem(hand);
		    else
		        pTarget.getWorld().dropItem(pTarget.getLocation(), hand);
		    pTarget.setItemInHand(new ItemStack(Material.AIR));
		}
	}

	@Override
	public void initConfigEntries() {
	}
}
