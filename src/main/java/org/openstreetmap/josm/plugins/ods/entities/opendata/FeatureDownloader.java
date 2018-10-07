package org.openstreetmap.josm.plugins.ods.entities.opendata;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Task;

/**
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface FeatureDownloader {
    public void setup(DownloadRequest request) throws OdsException;
    public Task prepare();
    public Task download();
    public Task process();
    public void setResponse(DownloadResponse response);
    public void setNormalisation(Normalisation normalisation);
    void cancel();
}
