package gg.uhc.uberhardcore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gg.uhc.uberhardcore.mobs.chicken.ThrownEggHandler;
import gg.uhc.uberhardcore.mobs.chicken.UberChicken;
import gg.uhc.uberhardcore.mobs.creeper.CreeperDeathHandler;
import gg.uhc.uberhardcore.mobs.sheep.UberSheep;
import gg.uhc.uberhardcore.mobs.skeleton.UberSkeleton;
import gg.uhc.uberhardcore.mobs.spider.SpiderAIModifier;
import gg.uhc.uberhardcore.mobs.spider.SpiderDeathHandler;
import gg.uhc.uberhardcore.mobs.zombie.UberZombie;
import gg.uhc.uberhardcore.mobs.zombie.ZombieSeigeHandler;
import gg.uhc.uberhardcore.mobs.zombie.ZombieSummonHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;

import java.util.List;

public enum MobOverride {
    ZOMBIE(EntityZombie.class, UberZombie.class, new ZombieSummonHandler(), new ZombieSeigeHandler()),
    SKELETON(EntitySkeleton.class, UberSkeleton.class),
    CHICKEN(EntityChicken.class, UberChicken.class, new ThrownEggHandler()),
    SPIDER(null, null, new SpiderDeathHandler(), new SpiderAIModifier()),
    SHEEP(EntitySheep.class, UberSheep.class),
    CREEPER(null, null, new CreeperDeathHandler())
    ;

    public final Class<? extends EntityLiving> toReplace;
    public final Class<? extends EntityLiving> replaceWith;
    public final List<Object> handlers;

    /**
     * Any extra code to run on pre-init forge
     */
    public void initialize() {}

    /**
     * A mob overide
     *
     * Keep toReplace and replaceWith null if no Entity override is required (if just events are good enough)
     *
     * @param toReplace the original Entity class to replace
     * @param replaceWith the override class to reaplce with
     * @param handers a list of object to register for events
     */
    MobOverride(Class<? extends EntityLiving> toReplace, Class<? extends EntityLiving> replaceWith, Object... handers) {
        this.toReplace = toReplace;
        this.replaceWith = replaceWith;

        List<Object> temp = Lists.newArrayListWithCapacity(handers.length + 1);
        temp.add(new InvalidSpawnHandler(toReplace));

        this.handlers = ImmutableList.copyOf(handers);
    }
}
