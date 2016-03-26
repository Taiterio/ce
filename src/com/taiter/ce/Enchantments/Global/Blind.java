package com.taiter.ce.Enchantments.Global;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;

public class Blind extends CEnchantment {

    int duration;
    int strength;

    public Blind(Application app) {
        super(app);
        configEntries.put("Duration", 100);
        triggers.add(Trigger.SHOOT_BOW);
        triggers.add(Trigger.DAMAGE_GIVEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        LivingEntity target = (LivingEntity) event.getEntity();
        EffectManager.playSound(target.getLocation(), "ENTITY_PLAYER_HURT", 1f, 0.4f);
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration + 20 * level, 0));

    }

    @Override
    public void initConfigEntries() {
        duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
    }
}
