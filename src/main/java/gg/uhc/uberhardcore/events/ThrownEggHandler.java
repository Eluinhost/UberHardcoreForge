package gg.uhc.uberhardcore.events;

import gg.uhc.uberhardcore.entities.UberChickenEgg;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ThrownEggHandler {

    @SubscribeEvent
    public void onSummonEvent(EntityJoinWorldEvent event) {
        // only run for eggs
        if (!(event.entity instanceof EntityEgg)) return;

        // skip our eggs
        if (event.entity instanceof UberChickenEgg) return;

        EntityEgg original = (EntityEgg) event.entity;

        // make a replacement entity
        UberChickenEgg replacement = new UberChickenEgg(original.getEntityWorld(), original.getThrower());

        // spawn replacement in
        original.getEntityWorld().spawnEntityInWorld(replacement);

        // cancel original spawn
        event.setCanceled(true);
    }
}
