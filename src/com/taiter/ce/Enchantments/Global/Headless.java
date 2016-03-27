package com.taiter.ce.Enchantments.Global;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;

public class Headless extends CEnchantment {

    public Headless(Application app) {
        super(app);
        triggers.add(Trigger.DAMAGE_GIVEN);
        resetMaxLevel();
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        final Player player = (Player) event.getDamager();
        final LivingEntity ent = (LivingEntity) event.getEntity();

        new BukkitRunnable() {
            @Override
            public void run() {

                if (ent.getHealth() <= 0) {
                    byte type = 3;

                    if (ent instanceof Skeleton) {
                        type = 0;
                        if (((Skeleton) ent).getSkeletonType().equals(SkeletonType.WITHER))
                            type = 1;
                    } else if (ent instanceof Zombie)
                        type = 2;
                    else if (ent instanceof Creeper)
                        type = 4;

                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, type);
                    if (type == 3) {
                        SkullMeta sm = (SkullMeta) skull.getItemMeta();
                        sm.setOwner(ent.getName());
                        skull.setItemMeta(sm);
                    }
                    ent.getWorld().dropItem(ent.getLocation(), skull);
                    EffectManager.playSound(player.getLocation(), "BLOCK_ANVIL_LAND", 0.1f, 1.5f);
                }
            }
        }.runTaskLater(getPlugin(), 5l);

    }

    @Override
    public void initConfigEntries() {
    }
}
