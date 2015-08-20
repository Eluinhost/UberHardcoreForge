package gg.uhc.uberhardcore;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
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

    public static void removeAITasksByClass(Class<? extends EntityAIBase> toRemove, EntityAITasks tasks) {
        Multimap<Integer, Class<? extends EntityAIBase>> map = HashMultimap.create(1, 1);

        map.put(null, toRemove);

        removeAITasksByClass(map, tasks);
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
}
