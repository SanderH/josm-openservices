package org.openstreetmap.josm.plugins.ods.io;

public abstract class AbstractTask implements Task{
    private final TaskStatus status = new TaskStatus();

    @Override
    public TaskStatus getStatus() {
        return status;
    }

}
