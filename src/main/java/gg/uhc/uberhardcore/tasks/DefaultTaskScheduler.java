package gg.uhc.uberhardcore.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class DefaultTaskScheduler implements TaskScheduler {

    protected Multimap<Long, Task> taskList = HashMultimap.create();
    protected long ticksLived = 0L;

    @Override
    public long scheduleTask(Task task, int ticks) {
        Preconditions.checkArgument(ticks > -1, "Tick amount must be greater than 0");

        taskList.put(ticksLived + ticks, task);

        return ticksLived + ticks;
    }

    @Override
    public void cancelTask(long targetTick, Task entry) {
        taskList.remove(targetTick, entry);
    }

    @Override
    public void cancelAllTasks(Task entry) {
        taskList.values().remove(entry);
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Collection<Task> toRun = taskList.get(ticksLived);

        for(Task task : toRun) {
            task.run();
        }

        taskList.removeAll(ticksLived);

        ticksLived++;
    }
}
