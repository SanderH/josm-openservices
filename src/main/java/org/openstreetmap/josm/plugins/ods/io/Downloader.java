package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.tools.Logging;

public interface Downloader {
    public void setup(DownloadRequest request) throws OdsException;

    //    public PrepareResponse prepare() throws OdsException;

    public void download() throws OdsException;

    public List<? extends Task> process();

    public void cancel();

    //    public static Collection<PrepareTask> collectPrepareTasks(List<? extends Downloader> downloaders) {
    //        List<Callable<Void>> tasks = new ArrayList<>(downloaders.size());
    //        for (final Downloader downloader : downloaders) {
    //            tasks.add(downloader.getPrepareTask());
    //            //            tasks.add(new Callable<Void>() {
    //            //                @Override
    //            //                public Void call() throws OdsException {
    //            //                    downloader.prepare();
    //            //                    return null;
    //            //                }
    //            //            });
    //        }
    //        return tasks;
    //    }

    public List<PrepareTask> prepare();

    public static List<Callable<Void>> getDownloadTasks(List<? extends Downloader> downloaders) {
        List<Callable<Void>> tasks = new ArrayList<>(downloaders.size());
        for (final Downloader downloader : downloaders) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws OdsException {
                    try {
                        downloader.download();
                    } catch (Exception e) {
                        Logging.error(e);
                        throw e;
                    }
                    return null;
                }
            });
        }
        return tasks;
    }

    public static List<Callable<Void>> getProcessTasks(List<? extends Downloader> downloaders) {
        List<Callable<Void>> tasks = new ArrayList<>(downloaders.size());
        for (final Downloader downloader : downloaders) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws OdsException {
                    downloader.process();
                    return null;
                }
            });
        }
        return tasks;
    }
}