package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
@Deprecated
public interface EntityUpdater {
    void update(List<Match<?>> matches);

    Collection<? extends Way> getUpdatedWays();
}
