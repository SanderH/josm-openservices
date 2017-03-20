package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public interface Downloader {
    public void setup(DownloadRequest request) throws OdsException;
    
    public PrepareResponse prepare() throws OdsException;

    public void download() throws OdsException;

    public void process() throws OdsException;

    public void cancel();

    public static List<Callable<Void>> getPrepareTasks(List<? extends Downloader> downloaders) {
        List<Callable<Void>> tasks = new ArrayList<>(downloaders.size());
        for (final Downloader downloader : downloaders) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws OdsException {
                    downloader.prepare();
                    return null;
                }
            });
        }
        return tasks;
    }

    public static List<Callable<Void>> getDownloadTasks(List<? extends Downloader> downloaders) {
        List<Callable<Void>> tasks = new ArrayList<>(downloaders.size());
        for (final Downloader downloader : downloaders) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws OdsException {
                    try {
                        downloader.download();
                    } catch (Exception e) {
                        Main.error(e);
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