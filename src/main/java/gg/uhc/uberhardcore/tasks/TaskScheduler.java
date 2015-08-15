package gg.uhc.uberhardcore.tasks;

public interface TaskScheduler {

    /**
     * Schedule the given task to run in x amount of ticks
     * @param task the taks to run
     * @param ticks the amount of ticks before running, 0 will run on next available tick
     * @return the target tick to be run on, can be used to cancel
     */
    long scheduleTask(Task task, int ticks);

    /**
     * @param task the entry to cancel
     * @param targetTick the tick the task is to be run on
     */
    void cancelTask(long targetTick, Task task);

    /**
     * Cancels all version of this task on any target tick
     * @param task the task to cancel
     */
    void cancelAllTasks(Task task);
}
