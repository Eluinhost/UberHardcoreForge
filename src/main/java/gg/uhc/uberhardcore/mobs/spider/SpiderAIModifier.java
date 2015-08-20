package gg.uhc.uberhardcore.mobs.spider;

import gg.uhc.uberhardcore.AIUtil;
import gg.uhc.uberhardcore.UberHardcore;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Modifies spider AI to attack during the daytime
 */
public class SpiderAIModifier {

    public void on(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntitySpider)) return;

        EntitySpider spider = (EntitySpider) event.entity;

        // remove all spider attacks + targetting
        AIUtil.removeAITasksByClass(EntitySpider.AISpiderAttack.class, spider.tasks);
        AIUtil.removeAITasksByClass(EntitySpider.AISpiderTarget.class, spider.targetTasks);

        // replace with non-daylight dependant attacks/targetting on players only
        spider.tasks.addTask(4, new AISpiderAttackIgnoresSunlight(spider, EntityPlayer.class));
        spider.targetTasks.addTask(2, new EntityAINearestAttackableTarget(spider, EntityPlayer.class, true));

        // give a movement speed buff
        spider.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).setBaseValue(0.45D);

        if (UberHardcore.inDebug) {
            spider.tasks.addTask(4, new AISpiderAttackIgnoresSunlight(spider, EntitySkeleton.class));
            spider.targetTasks.addTask(2, new EntityAINearestAttackableTarget(spider, EntitySkeleton.class, true));
        }
    }

    // spider attack without daylight considerations
    public class AISpiderAttackIgnoresSunlight extends EntityAIAttackOnCollide
    {
        public AISpiderAttackIgnoresSunlight(EntityCreature entity, Class p_i45819_2_) {
            super(entity, p_i45819_2_, 1.0D, true);
        }

        // attack radius?
        protected double func_179512_a(EntityLivingBase p_179512_1_) {
            return (double)(4.0F + p_179512_1_.width);
        }
    }
}
