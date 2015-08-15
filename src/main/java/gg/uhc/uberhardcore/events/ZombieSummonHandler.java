package gg.uhc.uberhardcore.events;

import gg.uhc.uberhardcore.mobs.UberZombie;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class ZombieSummonHandler {

    protected final Random rand = new Random();

    /**
     * Used to rewrite the entity type from zombie summons
     * @param event the summon aid event
     */
    @SubscribeEvent
    public void onSummonEvent(ZombieEvent.SummonAidEvent event) {
        if (event.attacker != null && (double) rand.nextFloat() < event.summonChance) {
            // use regular summon chances e.t.c.
            event.setResult(Event.Result.ALLOW);
            event.customSummonedAid = new UberZombie(event.world);
        } else {
            event.setResult(Event.Result.DENY);
        }
    }
}
