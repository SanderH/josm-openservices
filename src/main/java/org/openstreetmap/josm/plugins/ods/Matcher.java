package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public interface Matcher<T extends Entity> {
    void initialize() throws OdsException;

    void run();

    void reset();

}
