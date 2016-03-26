package com.taiter.ce.Enchantments.Armor;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Enchantments.CEnchantment;

public class Cloaking extends CEnchantment {

    int duration;
    int cooldown;

    public Cloaking(Application app) {
        super(app);
        configEntries.put("DurationPerLevel", 60);
        configEntries.put("Cooldown", 200);
        triggers.add(Trigger.DAMAGE_TAKEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        Player player = (Player) event.getEntity();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * level, 0));
        player.sendMessage(ChatColor.DARK_GRAY + "You have become invisible!");
        generateCooldown(player, cooldown);
    }

    @Override
    public void initConfigEntries() {
        duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DurationPerLevel"));
        cooldown = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));
    }
}
