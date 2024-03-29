package gg.uhc.uberhardcore.mobs.spider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Modifies spider death to explode into webs + blood
 */
public class SpiderDeathHandler {

    protected static final Random random = new Random();

    @SubscribeEvent
    public void on(LivingDeathEvent event) {
        if (!(event.entity instanceof EntitySpider)) return;

        // spawn 5 random webs
        for (int i = 0; i < 5; i++) {
            spawnRandomWeb(event.entity);
        }

        // spawn 30 random particle effects
        for (int i = 0; i < 30; i++) {
            spawnRandomRedstoneParticle(event.entity);
        }
    }

    /**
     * Spawns a redstone particle effect at the given entity.
     *
     * Adds a random positional offset from the given entity (1 block each direction max)
     *
     * @param entity location to spawn
     */
    protected static void spawnRandomRedstoneParticle(Entity entity) {
        ((WorldServer) entity.worldObj).spawnParticle(
                EnumParticleTypes.REDSTONE,
                false,
                entity.posX + (random.nextDouble() * 2.0D) - 1D,
                entity.posY + (random.nextDouble() * 2.0D) - 1D,
                entity.posZ + (random.nextDouble() * 2.0D) - 1D,
                3,
                0D,
                0D,
                0D,
                0
        );
    }

    /**
     * Spawns random falling block cobweb.
     *
     * Takes initial 50% momentum from given entity, then adds += .2 to each direction. Vertical momentum is guarenteed
     * to be at least .25
     *
     * @param entity location to spawn
     */
    protected static void spawnRandomWeb(Entity entity) {
        EntityFallingBlock block = new EntityFallingBlock(entity.worldObj, entity.posX, entity.posY, entity.posZ, Blocks.web.getDefaultState());

        // don't drop the item if it fails to place
        block.shouldDropItem = false;

        // don't despawn instantly
        block.fallTime = 1;

        // add (reduced) spider momentum
        block.motionX = entity.motionX *= .5D;
        block.motionY = entity.motionY *= .5D;
        block.motionZ = entity.motionZ *= .5D;

        // add some random spread
        block.motionX += (random.nextDouble() * 0.4D) - 0.2D;
        block.motionY += (random.nextDouble() * 0.4D) - 0.2D;
        block.motionZ += (random.nextDouble() * 0.4D) - 0.2D;

        // add some base vertical motion
        block.motionY += 0.25D;

        // make sure it goes up
        block.motionY = Math.min(block.motionY, 0.25D);
        block.isAirBorne = true;

        entity.worldObj.spawnEntityInWorld(block);
    }
}
