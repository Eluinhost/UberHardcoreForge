package gg.uhc.uberhardcore.mobs.zombie;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles loading of worlds to replace the Village seige mechanics to stop EntityZombie being spawned instead of
 * UberZombie
 */
public class ZombieSeigeHandler {

    // overwrite the seige with our own for every world loaded
    @SubscribeEvent
    public void onWorld(WorldEvent.Load loadEvent) throws IllegalAccessException {
        if (loadEvent.world instanceof WorldServer) {
            ((WorldServer) loadEvent.world).villageSiege = new UberZombieVillageSiege(loadEvent.world);
        }
    }
}
