package gg.uhc.uberhardcore.mobs;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gg.uhc.uberhardcore.AIUtil;
import gg.uhc.uberhardcore.UberHardcore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class UberSkeleton extends EntitySkeleton {

    static {
        EntitySpawnPlacementRegistry.setPlacementType(UberSkeleton.class, SpawnPlacementType.ON_GROUND);
    }

    public UberSkeleton(World worldIn) {
        super(worldIn);

        Multimap<Integer, Class<? extends EntityAIBase>> toRemove = HashMultimap.create();

        // removes the melee attack of the wither version
        toRemove.put(4, EntityAIAttackOnCollide.class);

        // remove standard arrow attack
        toRemove.put(4, EntityAIArrowAttack.class);

        if (this.rand.nextFloat() < 0.3F) {
            // make it a wither skeleton
            // must happen before AI removal as it changes the AI
            this.setSkeletonType(1);
        }

        // remove AIs
        AIUtil.removeAITasksByClass(toRemove, this.tasks);

        // readd the arrow attacks with longer range + faster attacks at range
        this.tasks.addTask(4, new EntityAIArrowAttack(this, 1.0D, 20, 30, 40.0F));

        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(60);

        if (UberHardcore.inDebug) {
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityZombie.class, false));
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityCreeper.class, false));
        }
    }

    // copied from EntitySkeleton, made shots more accurate
    public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_)
    {
//        EntityArrow entityarrow = new EntityArrow(this.worldObj, this, p_82196_1_, 1.6F, (float)(14 - this.worldObj.getDifficulty().getDifficultyId() * 4));
        EntityArrow entityarrow = new EntityArrow(this.worldObj, this, p_82196_1_, 1.6F, 0);
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        entityarrow.setDamage((double)(p_82196_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1)
        {
            entityarrow.setFire(100);
        }

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityarrow);
    }
}
