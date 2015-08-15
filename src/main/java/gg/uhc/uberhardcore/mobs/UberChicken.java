package gg.uhc.uberhardcore.mobs;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gg.uhc.uberhardcore.AIUtil;
import gg.uhc.uberhardcore.UberHardcore;
import gg.uhc.uberhardcore.entities.UberChickenEgg;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class UberChicken extends EntityChicken implements IRangedAttackMob {

    static {
        EntitySpawnPlacementRegistry.setPlacementType(UberChicken.class, SpawnPlacementType.ON_GROUND);
    }

    public UberChicken(World worldIn) {
        super(worldIn);

        Multimap<Integer, Class<? extends EntityAIBase>> toRemove = HashMultimap.create();

        toRemove.put(null, EntityAIPanic.class);
        toRemove.put(null, EntityAIMate.class);
        toRemove.put(null, EntityAITempt.class);
        toRemove.put(null, EntityAIFollowParent.class);

        AIUtil.removeAITasksByClass(toRemove, this.tasks);

        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false));

        if (UberHardcore.inDebug)
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityZombie.class, false));
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable)
    {
        return new UberChicken(this.worldObj);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
        UberChickenEgg entityEgg = new UberChickenEgg(this.worldObj, this);
        double d0 = p_82196_1_.posY + (double)p_82196_1_.getEyeHeight() - 1.100000023841858D;
        double d1 = p_82196_1_.posX - this.posX;
        double d2 = d0 - entityEgg.posY;
        double d3 = p_82196_1_.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d1 * d1 + d3 * d3) * 0.2F;
        entityEgg.setThrowableHeading(d1, d2 + (double) f1, d3, 1.6F, 12.0F);

        // change from random.bow noise
        this.playSound("mob.chicken.plop", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityEgg);
    }
}
