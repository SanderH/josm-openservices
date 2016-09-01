package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public interface Downloader {
    public void setup(DownloadRequest request) throws OdsException;
    
    public void prepare() throws ExecutionException;

    public void download() throws ExecutionException;

    public void process() throws ExecutionException;

    public void cancel();

    public static List<Callable<Void>> getPrepareTasks(List<? extends Downloader> downloaders) {
        List<Callable<Void>> tasks = new ArrayList<>(downloaders.size());
        for (final Downloader downloader : downloaders) {
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws ExecutionException {
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
                public Void call() throws ExecutionException {
                    downloader.download();
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
                public Void call() throws ExecutionException {
                    downloader.process();
                    return null;
                }
            });
        }
        return tasks;
    }
}