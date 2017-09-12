package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.tools.I18n;

public class EmptyCommand extends Command {

    @Override
    public String getDescriptionText() {
        return I18n.tr("Empty command");
    }

    @Override
    public void fillModifiedData(Collection<OsmPrimitive> modified,
            Collection<OsmPrimitive> deleted, Collection<OsmPrimitive> added) {
        // No action required
    }
}
