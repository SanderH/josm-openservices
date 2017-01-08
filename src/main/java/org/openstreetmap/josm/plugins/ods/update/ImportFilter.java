package org.openstreetmap.josm.plugins.ods.update;

import java.util.function.Predicate;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

/**
 * Predicate to determine whether an Entity should be imported.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface ImportFilter extends Predicate<Entity> {
    //
}
