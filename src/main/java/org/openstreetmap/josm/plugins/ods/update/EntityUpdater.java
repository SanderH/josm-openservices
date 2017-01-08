package org.openstreetmap.josm.plugins.ods.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface EntityUpdater {
    UpdateResult update(List<Match<?>> matches);
    Class<? extends Entity> getType();
}
