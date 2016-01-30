package com.taiter.ce.Enchantments.Bow;

import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;

public class Blaze extends CEnchantment {

    public Blaze(Application app) {
        super(app);
        triggers.add(Trigger.SHOOT_BOW);
        this.resetMaxLevel();
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityShootBowEvent event = (EntityShootBowEvent) e;
        event.setCancelled(true);
        event.getEntity().launchProjectile(SmallFireball.class);
    }

    @Override
    public void initConfigEntries() {
    }
}
