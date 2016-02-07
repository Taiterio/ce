package com.taiter.ce.Enchantments.Global;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        ItemStack inHand = target.getEquipment().getItemInHand();
        if (inHand != null && !inHand.getType().equals(Material.AIR)) {
            target.getWorld().dropItem(target.getLocation(), inHand).setPickupDelay(40);
            target.getEquipment().setItemInHand(new ItemStack(Material.AIR));
        }
    }

    @Override
    public void initConfigEntries() {
    }
}
