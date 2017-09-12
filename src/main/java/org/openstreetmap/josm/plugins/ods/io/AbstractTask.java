package org.openstreetmap.josm.plugins.ods.io;

import java.util.Collection;

public abstract class AbstractTask implements Task{
    private final TaskStatus status = new TaskStatus();
    private final Collection<Class<? extends Task>> dependencies = Task.NO_DEPENDENCIES;

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public Collection<Class<? extends Task>> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean depends(Task task) {
        for (Class<? extends Task> dependency : getDependencies()) {
            if (dependency.isInstance(task)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Task task) {
        if (task.depends(this)) {
            return -1;
        }
        if (this.depends(task)) {
            return 1;
        }
        return 0;
    }
}
