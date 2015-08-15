package gg.uhc.uberhardcore;

import com.google.common.collect.Lists;
import gg.uhc.uberhardcore.tasks.DefaultTaskScheduler;
import gg.uhc.uberhardcore.tasks.TaskScheduler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.List;

@Mod(modid = UberHardcore.MODID, version = UberHardcore.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class UberHardcore {
    public static final String MODID = "uberhardcore";
    public static final String VERSION = "0.0.1";

    @Mod.Instance
    public static UberHardcore INSTANCE;

    public static boolean inDebug = true;

    protected TaskScheduler scheduler;

    // TODO zombies riding chickens is going to be broken
    // TODO skeletons riding spiders is going to be broken

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        // set up scheduler
        scheduler = new DefaultTaskScheduler();
        FMLCommonHandler.instance().bus().register(scheduler);

        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();

        List<List<BiomeGenBase.SpawnListEntry>> biomeSpawnLists = Lists.newArrayList();

        // create easier to use lists
        for (BiomeGenBase biome : biomes) {
            if (biome == null) continue;

            // add each individual spawn list to the full list
            biomeSpawnLists.add(biome.getSpawnableList(EnumCreatureType.AMBIENT));
            biomeSpawnLists.add(biome.getSpawnableList(EnumCreatureType.CREATURE));
            biomeSpawnLists.add(biome.getSpawnableList(EnumCreatureType.WATER_CREATURE));
            biomeSpawnLists.add(biome.getSpawnableList(EnumCreatureType.MONSTER));
        }

        // run all of the overrides
        for (MobOverride mobOverride : MobOverride.values()) {
            mobOverride.initialize();

            if (mobOverride.toReplace != null && mobOverride.replaceWith != null) {
                // replace the EntityList version
                AIUtil.overwriteVanillaMob(mobOverride.toReplace, mobOverride.replaceWith);

                // replace all instances in the spawn lists
                for (List<BiomeGenBase.SpawnListEntry> spawnList : biomeSpawnLists) {
                    List<BiomeGenBase.SpawnListEntry> toRemove = Lists.newLinkedList();
                    List<BiomeGenBase.SpawnListEntry> toAdd = Lists.newLinkedList();

                    for (BiomeGenBase.SpawnListEntry entry : spawnList) {
                        if (entry.entityClass == mobOverride.toReplace) {
                            toRemove.add(entry);
                            toAdd.add(new BiomeGenBase.SpawnListEntry(mobOverride.replaceWith, entry.itemWeight, entry.minGroupCount, entry.maxGroupCount));
                        }
                    }

                    spawnList.removeAll(toRemove);
                    spawnList.addAll(toAdd);
                }
            }
        }
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
        // register all of the required handlers for events
        for (MobOverride mobOverride : MobOverride.values()) {
            for (Object hander : mobOverride.handlers) {
                MinecraftForge.EVENT_BUS.register(hander);
            }
        }
    }
}


