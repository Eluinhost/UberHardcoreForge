package gg.uhc.uberhardcore.events;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Project: UberHardcore
 * Package: gg.uhc.uberhardcore.events
 * Created by Eluinhost on 14:51 12/08/2015 2015.
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
