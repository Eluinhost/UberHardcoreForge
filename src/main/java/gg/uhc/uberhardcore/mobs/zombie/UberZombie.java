package gg.uhc.uberhardcore.mobs.zombie;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gg.uhc.uberhardcore.AIUtil;
import gg.uhc.uberhardcore.UberHardcore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

/**
 * Spawns a zombie with:
 *
 * increased agro/chase range
 * no villager agro
 * slow wandering
 * faster agro speed
 * spider-like leap attack
 */
public class UberZombie extends EntityZombie {

    protected static final Multimap<Integer, Class<? extends EntityAIBase>> tasksToRemove = HashMultimap.create();
    protected static final Multimap<Integer, Class<? extends EntityAIBase>> targetTasksToRemove = HashMultimap.create();

    static {
        // remove base EntityAIAttackOnCollide to readd it with added movement speed
        // also removes attacks on villagers + iron golem
        tasksToRemove.put(2, EntityAIAttackOnCollide.class);

        // remove the village movement AI
        tasksToRemove.put(6, EntityAIMoveThroughVillage.class);

        // remove wander to readd it later with less move speed
        tasksToRemove.put(7, EntityAIWander.class);

        // remove all of the attackable targets to replace with just the Player one
        targetTasksToRemove.put(2, EntityAINearestAttackableTarget.class);

        EntitySpawnPlacementRegistry.setPlacementType(UberZombie.class, SpawnPlacementType.ON_GROUND);
    }

    public UberZombie(World worldIn) {
        super(worldIn);

        this.isImmuneToFire = true;

        AIUtil.removeAITasksByClass(tasksToRemove, this.tasks);
        AIUtil.removeAITasksByClass(targetTasksToRemove, this.targetTasks);

        // wander at half speed
        this.targetTasks.addTask(7, new EntityAIWander(this, .5D));

        // leap at targets
        this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.4F));

        // agro on players with slightly faster move speed, always keep target
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2D, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false));

        if (UberHardcore.inDebug) {
            this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityChicken.class, 1.2D, true));
        }
    }

    // copy/pasted from EntityZombie
    // changed EntityZombie -> UberZombie
    // stops EntityZombie being created on villager conversion
    public void onKillEntity(EntityLivingBase entityLivingIn)
    {
        super.onKillEntity(entityLivingIn);

        if ((this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD) && entityLivingIn instanceof EntityVillager)
        {
            if (this.worldObj.getDifficulty() != EnumDifficulty.HARD && this.rand.nextBoolean())
            {
                return;
            }

            UberZombie entityzombie = new UberZombie(this.worldObj);
            entityzombie.copyLocationAndAnglesFrom(entityLivingIn);
            this.worldObj.removeEntity(entityLivingIn);
            entityzombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityzombie)), (IEntityLivingData)null);
            entityzombie.setVillager(true);

            if (entityLivingIn.isChild())
            {
                entityzombie.setChild(true);
            }

            this.worldObj.spawnEntityInWorld(entityzombie);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
        }
    }
}
