package org.openstreetmap.josm.plugins.ods.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.tools.Logging;

public interface Task extends Callable<Void> {
    public final Collection<Class<? extends Task>> NO_DEPENDENCIES = Collections.emptyList();
    public TaskStatus getStatus();

    /**
     * Create a sorted list of tasks that satisfies the correct operation
     * order, based on the task dependencies.
     *
     * @param classes
     * @param enclosingInstance
     * @return
     */
    public static List<? extends Task> createTasks(List<Class<? extends Task>> classes, Object enclosingInstance) {
        List<Task> tasks = new LinkedList<>();
        for (Class<? extends Task> clazz : classes) {
            tasks.add(createTask(clazz, enclosingInstance));
        }
        //        tasks.sort(null);
        return tasks;
    }

    public static Task createTask(Class<? extends Task> clazz, Object enclosingInstance) {
        try {
            Class<?> enclosingClass = clazz.getEnclosingClass();
            if (enclosingClass == null || Modifier.isStatic(clazz.getModifiers())) {
                return clazz.newInstance();
            }
            assert enclosingClass.isInstance(enclosingInstance);

            Constructor<? extends Task> ctor = clazz.getDeclaredConstructor(enclosingClass);
            return ctor.newInstance(enclosingInstance);
        } catch (Exception e) {
            Logging.error(e);
            throw new RuntimeException(e);
        }
    }

    public static class IdleTask implements Task {
        TaskStatus status = new TaskStatus();

        @Override
        public Void call() throws Exception {
            return null;
        }

        @Override
        public TaskStatus getStatus() {
            return status;
        }
    }
}
