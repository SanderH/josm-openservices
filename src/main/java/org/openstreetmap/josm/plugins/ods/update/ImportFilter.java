package org.openstreetmap.josm.plugins.ods.update;

import java.util.function.Predicate;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

/**
 * QueryPredicate to determine whether an Entity should be imported.
 * TODO Using a QueryPredicate here gives us no way to inform the user
 * about how many entities were not imported and why. Find a solution
 * for this.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface ImportFilter extends Predicate<Entity<?>> {
    //
}
