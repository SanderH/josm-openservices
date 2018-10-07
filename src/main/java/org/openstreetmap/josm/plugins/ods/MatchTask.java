package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.Task;

/*
 * A MatchTask performs the matching between open data entities and OSM
 * entities.
 */
public interface MatchTask extends Task {
    public void initialize() throws OdsException;

    public void reset();
}
