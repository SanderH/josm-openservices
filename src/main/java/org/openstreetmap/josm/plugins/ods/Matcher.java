package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public interface Matcher<T extends Entity> {
    public void initialize() throws OdsException;
    
    public Class<T> getType();

    public void run();

    public void reset();

}
