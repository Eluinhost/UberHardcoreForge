package gg.uhc.uberhardcore.mobs.creeper;

import gg.uhc.uberhardcore.UberHardcore;
import gg.uhc.uberhardcore.tasks.Task;
import gg.uhc.uberhardcore.tasks.TaskScheduler;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Causes creepers to explode again after their death
 */
public class CreeperDeathHandler {

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!(event.entity instanceof EntityCreeper)) return;

        EntityCreeper creeper = (EntityCreeper) event.entity;

        TaskScheduler scheduler = UberHardcore.INSTANCE.getScheduler();

        // explode after 2 seconds ticks
        scheduler.scheduleTask(new DelayedExplosionRunnable(creeper), 40);

        // set up some layered smoke effects
        for (int i = 0; i < 5; i++) {
            scheduler.scheduleTask(new ImpedingExplosionParticleEffect(
                    creeper.worldObj,
                    creeper.posX,
                    creeper.posY,
                    creeper.posZ,
                    1,
                    1,
                    1,
                    0,
                    20 * (i+1) // increase the speed over time

            ), 10 + (i * 10));
        }
    }

    class DelayedExplosionRunnable implements Task {
        protected final World world;
        protected final BlockPos pos;
        protected final float radius;
        protected final EntityCreeper creeper;

        /**
         * Creates an explosion when ran, explosion is at inital location when object is created
         * @param creeper the creeper to associate with the explosion
         */
        public DelayedExplosionRunnable(EntityCreeper creeper) {
            this.world = creeper.getEntityWorld();
            this.pos = creeper.getPosition();
            this.radius = creeper.getPowered() ? 6 : 3;
            this.creeper = creeper;
        }

        @Override
        public void run() {
            // simulate a creepr explosion at original location
            boolean flag = world.getGameRules().getGameRuleBooleanValue("mobGriefing");
            world.createExplosion(creeper, pos.getX(), pos.getY(), pos.getZ(), radius, flag);
        }
    }

    class ImpedingExplosionParticleEffect implements Task {

        protected final World world;
        protected final double x;
        protected final double y;
        protected final double z;
        protected final double offsetX;
        protected final double offsetY;
        protected final double offsetZ;
        protected final double speed;
        protected final int count;

        public ImpedingExplosionParticleEffect(World world, double x, double y, double z, double motX, double motY, double motZ, double speed, int count) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.offsetX = motX;
            this.offsetY = motY;
            this.offsetZ = motZ;
            this.speed = speed;
            this.count = count;
        }

        @Override
        public void run() {
            if (!(world instanceof WorldServer)) return;

            WorldServer server = (WorldServer) world;

            // use WorldServer specific spawn particle method
            server.spawnParticle(
                    EnumParticleTypes.SMOKE_LARGE,
                    false,
                    x,
                    y,
                    z,
                    count,
                    offsetX,
                    offsetY,
                    offsetZ,
                    speed
            );
        }
    }
}
