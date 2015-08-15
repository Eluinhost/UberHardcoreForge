package gg.uhc.uberhardcore.events;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidSpawnHandler {

    protected final Class<? extends EntityLiving> toCheck;

    protected static final Logger log = LogManager.getLogger();


    public InvalidSpawnHandler(Class<? extends EntityLiving> toCheck) {
        this.toCheck = toCheck;
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.entity.getClass() == toCheck) {
            BlockPos pos = event.entity.getPosition();
            log.error("Invalid spawn occured for entity type {} at x:{} y:{} z:{}, cancelling", event.entity.getClass().getName(), pos.getX(), pos.getY(), pos.getZ());

            event.setCanceled(true);
        }
    }
}
