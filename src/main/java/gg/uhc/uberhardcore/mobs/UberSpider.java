package gg.uhc.uberhardcore.mobs;

import gg.uhc.uberhardcore.AIUtil;
import gg.uhc.uberhardcore.UberHardcore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class UberSpider extends EntitySpider {

    static {
        EntitySpawnPlacementRegistry.setPlacementType(UberSpider.class, SpawnPlacementType.ON_GROUND);
    }

    public UberSpider(World worldIn) {
        super(worldIn);

        // start by clearing all of the tasks added in super()
        AIUtil.removeAllTasks(this.tasks);
        AIUtil.removeAllTasks(this.targetTasks);

        // readd the tasks
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiAvoidExplodingCreepers);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));

        // replace regular agro with non-daylight one
        // this.tasks.addTask(4, new EntitySpider.AISpiderAttack(EntityPlayer.class));
        this.tasks.addTask(4, new AISpiderAttack(EntityPlayer.class));

        // remove iron golem agro
        //this.tasks.addTask(4, new EntitySpider.AISpiderAttack(EntityIronGolem.class));

        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));

        // replace existing agro with non-daylight one
        //this.targetTasks.addTask(2, new EntitySpider.AISpiderTarget(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));

        // remove iron golem agro
        //this.targetTasks.addTask(3, new EntitySpider.AISpiderTarget(EntityIronGolem.class));

        if (UberHardcore.inDebug) {
            this.tasks.addTask(4, new AISpiderAttack(EntitySkeleton.class));
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntitySkeleton.class, true));
        }
    }

    // spider attack without daylight considerations
    class AISpiderAttack extends EntityAIAttackOnCollide
    {
        private static final String __OBFID = "CL_00002197";

        public AISpiderAttack(Class p_i45819_2_)
        {
            super(UberSpider.this, p_i45819_2_, 1.0D, true);
        }

        // attack radius?
        protected double func_179512_a(EntityLivingBase p_179512_1_)
        {
            return (double)(4.0F + p_179512_1_.width);
        }
    }
}
