package org.openstreetmap.josm.plugins.ods.io;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openstreetmap.josm.tools.Logging;

public class TaskRunner {
    private static final int NTHREADS = 4;

    private final ExecutorService executor;
    private TaskStatus status = new TaskStatus();
    private List<Future<Void>> futures;

    public TaskRunner() {
        // TODO Improve pooling of executors
        executor = Executors.newFixedThreadPool(NTHREADS);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void runTasks(List<? extends Task> tasks, boolean parallel) {
        status.clear();
        if (parallel) {
            runParallel(tasks);
        }
        else {
            runSequential(tasks);
        }
        status = new TaskStatus(tasks);
    }

    private void runParallel(List<? extends Task> tasks) {
        try {
            futures = executor.invokeAll(tasks);
            executor.shutdown();
            for (Future<Void> future : futures) {
                // TODO Clean-up most exception handling.
                // Is should be handled by the task
                try {
                    future.get();
                } catch (CancellationException e1) {
                    status.cancelled();
                    //                } catch (ExecutionException executionException) {
                    //                    Exception e = (Exception) executionException.getCause();
                    //                    if (e instanceof NullPointerException) {
                    //                        messages.add("A null pointer exception occurred. This is allways a programming error\n" +
                    //                                "please check the logs.");
                    //                        failed = true;
                    //                        Main.error(e);
                    //                    }
                    //                    else {
                    //                        messages.add(e.getMessage());
                    //                        failed = true;
                    //                    }
                } catch (Exception e) {
                    Logging.error(e);
                    //                    messages.add("An unexpected exception occurred. please check the logs.");
                    //                    failed = true;
                }
            }
            executor.shutdownNow();
        } catch (InterruptedException e) {
            // TODO do we need this?
            for (Future<Void> future : futures) {
                future.cancel(true);
            }
        } catch (Exception e) {
            Logging.error(e);
            throw e;
        }
    }

    private void runSequential(List<? extends Task> tasks) {
        boolean debug = "true".equals(System.getenv("ods-debug"));
        for (Task task : tasks) {
            try {
                long startTime = System.currentTimeMillis();
                if (debug) {
                    Logging.info("Start task {0}", task);
                }
                task.call();
                if (debug) {
                    long ms = System.currentTimeMillis() - startTime;
                    Logging.info("Task {0} finished in {1} ms", task, ms);
                }
            } catch (CancellationException e1) {
                status.cancelled();
            } catch (Exception e) {
                Logging.error(e);
                throw new RuntimeException(e);
            }
        }
    }

}
