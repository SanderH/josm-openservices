package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Task;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

/**
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface FeatureDownloader {
    public void setup(DownloadRequest request) throws OdsException;
    public Optional<Task> prepare();
    public Optional<Task> download();
    public Optional<Task> process();
    public void setResponse(DownloadResponse response);
    public void setNormalisation(Normalisation normalisation);
    void setRepository(Repository repository);
    public Repository getRepository();

    public static List<Task> getPrepareTasks(Collection<FeatureDownloader> downloaders) {
        return downloaders.stream()
                .map(FeatureDownloader::prepare)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Task> getDownloadTasks(Collection<FeatureDownloader> downloaders) {
        return downloaders.stream()
                .map(FeatureDownloader::download)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Task> getProcessTasks(Collection<FeatureDownloader> downloaders) {
        return downloaders.stream()
                .map(FeatureDownloader::process)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    void cancel();
}
