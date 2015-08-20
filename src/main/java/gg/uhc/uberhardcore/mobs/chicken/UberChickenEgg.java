package gg.uhc.uberhardcore.mobs.chicken;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Used to replace an EntityEgg with a version that spawns an UberChicken instead.
 *
 * Also reduces rates of babies spawning from eggs too to avoid overpopulation
 */
public class UberChickenEgg extends EntityEgg {

    public UberChickenEgg(World worldIn, EntityLivingBase p_i1780_2_) {
        super(worldIn, p_i1780_2_);
    }

    // copied from EntityEgg replaced EntityChicken with UberChicken
    // replace 1/8 chance to spawn baby to 1/32 + no change for 4 to spawn
    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_)
    {
        if (p_70184_1_.entityHit != null)
        {
            // added this.getThrower() instanceof UberChicken ? 1.0F : 0.0F]
            // causes non-chicken thrown eggs to do no damage
            p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.getThrower() instanceof UberChicken ? 1.0F : 0.0F);
        }

        if (!this.worldObj.isRemote && this.rand.nextInt(32) == 0)
        {
            UberChicken entitychicken = new UberChicken(this.worldObj);
            entitychicken.setGrowingAge(-24000);
            entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            this.worldObj.spawnEntityInWorld(entitychicken);
        }

        double d0 = 0.08D;

        for (int j = 0; j < 8; ++j)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {Item.getIdFromItem(Items.egg)});
        }

        if (!this.worldObj.isRemote)
        {
            this.setDead();
        }
    }
}
