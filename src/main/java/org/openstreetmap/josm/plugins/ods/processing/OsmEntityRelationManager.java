package org.openstreetmap.josm.plugins.ods.processing;

/**
 * OsmEntityRelationManager manages relations between entities on
 * the OSM layer.
 * Relations between Entities can be materialized after each
 * download. The relations can be deferred from osm relation primitives,
 * spatial relations, or attribute relations.
 * Modifications to the osm layer can trigger re-evaluation of relations
 * or the creation of new relationships.
 *
 * @author Gertjan Idema
 *
 */
public interface OsmEntityRelationManager {

}
