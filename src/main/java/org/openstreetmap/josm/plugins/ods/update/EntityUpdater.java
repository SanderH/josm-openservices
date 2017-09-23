package org.openstreetmap.josm.plugins.ods.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.matching.Osm2OdMatch;

/**
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface EntityUpdater {
    UpdateResult update(List<Osm2OdMatch> matches);
}
