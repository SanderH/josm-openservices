package org.openstreetmap.josm.plugins.ods.io;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

/**
 * Marker interface
 */
public interface LayerDownloader {
    public abstract void initialize() throws OdsException;

    public void setup(DownloadRequest request) throws OdsException;

    public void setResponse(DownloadResponse response);

    public Optional<Task> prepare();

    public Optional<Task> download();

    public Optional<Task> process();

    public abstract void cancel();

    public static List<Task> getPrepareTasks(Collection<LayerDownloader> downloaders) {
        return downloaders.stream()
                .map(LayerDownloader::prepare)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Task> getDownloadTasks(Collection<LayerDownloader> downloaders) {
        return downloaders.stream()
                .map(LayerDownloader::download)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Task> getProcessTasks(Collection<LayerDownloader> downloaders) {
        return downloaders.stream()
                .map(LayerDownloader::process)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
