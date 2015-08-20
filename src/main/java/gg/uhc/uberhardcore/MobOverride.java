package gg.uhc.uberhardcore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gg.uhc.uberhardcore.mobs.chicken.ThrownEggHandler;
import gg.uhc.uberhardcore.mobs.chicken.UberChicken;
import gg.uhc.uberhardcore.mobs.creeper.CreeperDeathHandler;
import gg.uhc.uberhardcore.mobs.rabbit.KillerRabbitSpawnHandler;
import gg.uhc.uberhardcore.mobs.sheep.UberSheep;
import gg.uhc.uberhardcore.mobs.skeleton.UberSkeleton;
import gg.uhc.uberhardcore.mobs.spider.SpiderAIModifier;
import gg.uhc.uberhardcore.mobs.spider.SpiderDeathHandler;
import gg.uhc.uberhardcore.mobs.zombie.UberZombie;
import gg.uhc.uberhardcore.mobs.zombie.ZombieSeigeHandler;
import gg.uhc.uberhardcore.mobs.zombie.ZombieSummonHandler;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

public enum MobOverride {
    ZOMBIE(EntityZombie.class, UberZombie.class, new ZombieSummonHandler(), new ZombieSeigeHandler()),
    SKELETON(EntitySkeleton.class, UberSkeleton.class),
    CHICKEN(EntityChicken.class, UberChicken.class, new ThrownEggHandler()),
    SPIDER(null, null, new SpiderDeathHandler(), new SpiderAIModifier()),
    SHEEP(EntitySheep.class, UberSheep.class),
    CREEPER(null, null, new CreeperDeathHandler()),
    RABBIT(null, null, new KillerRabbitSpawnHandler())
    ;

    public final Class<? extends EntityLiving> toReplace;
    public final Class<? extends EntityLiving> replaceWith;
    public final List<Object> handlers;

    /**
     * Any extra code to run on pre-init forge
     */
    protected void initialize() {}

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

    protected static void runMobOverrides(Configuration configuration) {
        // setup comment for the configuration
        configuration.addCustomCategoryComment("overrides", "Enable or disable each mob override");

        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();

        List<List<BiomeGenBase.SpawnListEntry>> biomeSpawnLists = Lists.newArrayList();

        // create easier to use list of lists
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
            if (!configuration.get("overrides", mobOverride.name(), true).getBoolean()) return;

            // run intialization scripts
            mobOverride.initialize();

            // if any replacement classes are null don't attempt overwrites
            if (mobOverride.toReplace != null && mobOverride.replaceWith != null) {

                // replace the EntityList version
                overwriteVanillaMob(mobOverride.toReplace, mobOverride.replaceWith);

                // replace all classes within the spawn lists
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

    /**
     * Replaces a vanilla mob class with a custom class, should be ran before forge initializes (I think) {@see FMLPreInitializationEvent}
     *
     * Extra care should be taken to make sure the client can handle the replacement mob packets. Recommended to extend the original class in the replacement
     *
     * @param original the vanilla mob to replace
     * @param replacement the class to use as a replacement
     */
    protected static void overwriteVanillaMob(Class<? extends EntityLiving> original, Class<? extends EntityLiving> replacement) {
        // grab other required details from the original mob
        int id = (int) EntityList.classToIDMapping.get(original);
        String name = (String) EntityList.classToStringMapping.get(original);

        // modify string <-> class
        EntityList.stringToClassMapping.put(name, replacement);
        EntityList.classToStringMapping.remove(original);
        EntityList.classToStringMapping.put(replacement, name);

        // modify id <-> class
        EntityList.idToClassMapping.put(id, replacement);
        EntityList.classToIDMapping.remove(original);
        EntityList.classToIDMapping.put(replacement, id);

        // string->id mapping shouldn't need to change
    }
}
