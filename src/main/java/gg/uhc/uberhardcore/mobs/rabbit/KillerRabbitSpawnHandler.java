package gg.uhc.uberhardcore.mobs.rabbit;

import net.minecraft.entity.passive.EntityRabbit;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Adds a random chance for a spawning rabbit to be a killer one
 */
public class KillerRabbitSpawnHandler {

    protected static final Random random = new Random();

    @SubscribeEvent
    public void on(LivingSpawnEvent event) {
        if (!(event.entity instanceof EntityRabbit)) return;

        EntityRabbit rabbit = (EntityRabbit) event.entityLiving;

        // switch to a killer rabbit
        if (random.nextDouble() < .01D) rabbit.setRabbitType(99);
    }
}
