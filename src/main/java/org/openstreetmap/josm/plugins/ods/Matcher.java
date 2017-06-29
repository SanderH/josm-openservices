package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public interface Matcher {
    public void initialize() throws OdsException;

    public void run();

    public void reset();

}
