package org.openstreetmap.josm.plugins.ods.io;

import java.util.List;

import org.openstreetmap.josm.tools.Logging;

public class TaskGroup implements Task {
    private final List<Task> subTasks;
    private final boolean parallel;
    private final TaskRunner taskRunner = new TaskRunner();

    public TaskGroup(List<Task> subTasks, boolean parallel) {
        super();
        this.subTasks = subTasks;
        this.parallel = parallel;
    }

    @Override
    public Void call() throws Exception {
        try {
            taskRunner.runTasks(subTasks, parallel);
            return null;
        }
        catch (Exception e) {
            Logging.error(e);
            throw e;
        }
    }

    @Override
    public TaskStatus getStatus() {
        return taskRunner.getStatus();
    }
}
