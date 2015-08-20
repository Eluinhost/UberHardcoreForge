package gg.uhc.uberhardcore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gg.uhc.uberhardcore.events.*;
import gg.uhc.uberhardcore.mobs.UberChicken;
import gg.uhc.uberhardcore.mobs.UberSheep;
import gg.uhc.uberhardcore.mobs.UberSkeleton;
import gg.uhc.uberhardcore.mobs.UberZombie;
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

    MobOverride(Class<? extends EntityLiving> toReplace, Class<? extends EntityLiving> replaceWith, Object... handers) {
        this.toReplace = toReplace;
        this.replaceWith = replaceWith;

        List<Object> temp = Lists.newArrayListWithCapacity(handers.length + 1);
        temp.add(new InvalidSpawnHandler(toReplace));

        this.handlers = ImmutableList.copyOf(handers);
    }

    MobOverride(Class<? extends EntityLiving> toReplace, Class<? extends EntityLiving> replaceWith) {
        this.toReplace = toReplace;
        this.replaceWith = replaceWith;
        this.handlers = Lists.newArrayListWithCapacity(0);
    }
}
