package gg.uhc.uberhardcore;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.List;

public class AIUtil {

    /**
     * Removes matching AI from the given EntityAITasks object
     * <p/>
     * toRemove should be a map of Classes to remove from each priority. If the priority is null it means to remove
     * all instances of the Class no matter the priority
     *
     * @param toRemove map of priorities -> Class to remove
     * @param tasks    the tasks list to remove from
     */
    public static void removeAITasksByClass(Multimap<Integer, Class<? extends EntityAIBase>> toRemove, EntityAITasks tasks) {

        // find all the matching EntityAIBase objects
        List<EntityAIBase> found = Lists.newLinkedList();
        for (EntityAITasks.EntityAITaskEntry entry : (List<EntityAITasks.EntityAITaskEntry>) tasks.taskEntries) {
            Class type = entry.action.getClass();

            // allow matching priority or wildcard priority
            if (toRemove.containsEntry(entry.priority, type) || toRemove.containsEntry(null, type)) {
                found.add(entry.action);
            }
        }

        // remove all the found ai
        for (EntityAIBase ai : found) {
            tasks.removeTask(ai);
        }
    }

    /**
     * Removes all tasks from the EntityAITasks object
     *
     * @param tasks the EntityAITasks object to clear
     */
    public static void removeAllTasks(EntityAITasks tasks) {
        List<EntityAITasks.EntityAITaskEntry> entries = Lists.newLinkedList(tasks.taskEntries);
        for (EntityAITasks.EntityAITaskEntry entry : entries) {
            tasks.removeTask(entry.action);
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
    public static void overwriteVanillaMob(Class<? extends EntityLiving> original, Class<? extends EntityLiving> replacement) {
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
