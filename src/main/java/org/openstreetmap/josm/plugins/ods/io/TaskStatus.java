package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.List;

public class TaskStatus {
    private boolean failed = false;
    private boolean cancelled = false;
    private final List<String> issues = new ArrayList<>();

    /**
     * Create a new TaskStatus from the statuses of a list of tasks.
     *
     * @param tasks
     */
    public TaskStatus(List<? extends Task> tasks) {
        tasks.forEach(task -> {
            TaskStatus status = task.getStatus();
            failed = failed || (status.isFailed());
            cancelled = cancelled || status.isCancelled();
            issues.addAll(status.getIssues());
        });
    }

    public TaskStatus() {
        // Hide default constructor
    }

    public void failed(String issue) {
        failed = true;
        issues.add(issue);
    }

    public void cancelled() {
        cancelled = true;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void clear() {
        failed=false;
        issues.clear();
    }
}
