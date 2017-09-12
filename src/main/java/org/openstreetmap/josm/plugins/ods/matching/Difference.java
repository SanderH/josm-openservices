package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;

import org.openstreetmap.josm.command.Command;

public interface Difference {
    public List<Command> fix();
}
