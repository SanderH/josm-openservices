package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.StartDate;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public abstract class AbstractOsmEntityBuilder<E extends OsmEntity> implements OsmEntityBuilder {
    private OsmLayerManager layerManager;
    private final Predicate<OsmPrimitive> recognizer;
    private Repository repository;
    private GeoUtil geoUtil;

    public AbstractOsmEntityBuilder(Predicate<OsmPrimitive> recognizer) {
        super();
        this.recognizer = recognizer;
    }

    @Override
    public void initialize(OdsModule module) {
        this.geoUtil = module.getGeoUtil();
        this.layerManager = module.getOsmLayerManager();
        this.repository = module.getRepository();
    }

    @Override
    public OsmLayerManager getLayerManager() {
        return layerManager;
    }

    @Override
    public boolean recognizes(OsmPrimitive primitive) {
        return recognizer.test(primitive);
    }

    public GeoUtil getGeoUtil() {
        return geoUtil;
    }

    protected void register(ManagedPrimitive primitive, OsmEntity entity) {
        entity.setPrimitive(primitive);
        repository.add(entity);
        primitive.setEntity(entity);
    }

    /*
     * Check if the primitive is incomplete.
     * Or any of it's members in case of a relation
     */
    public static boolean isIncomplete(OsmPrimitive primitive) {
        if (OsmPrimitiveType.RELATION != primitive.getType()) {
            return primitive.isIncomplete();
        }
        if (primitive.isIncomplete()) return true;
        for (OsmPrimitive member : ((Relation)primitive).getMemberPrimitives()) {
            if (isIncomplete(member)) return true;
        }
        return false;
    }

    @Override
    public void updateTags(OsmPrimitive primitive, Map<String, String> newTags) {
        ManagedPrimitive ods = getLayerManager().getManagedPrimitive(primitive);
        if (ods == null) return;
        @SuppressWarnings("unchecked")
        OsmEntity entity = (OsmEntity) ods.getEntity();
        if (entity == null) {
            return;
        }
        //        updateTags(entity, newTags);
        //        entity.getMatch().ifPresent(match -> {
        //            match.analyze();
        //            match.updateMatchTags();
        //        });
    }

    @Override
    public void updateGeometry(Way way) {
        ManagedPrimitive ods = getLayerManager().getManagedPrimitive(way);
        if (ods == null) return;
        ods.setPrimitive(way);
        OsmEntity entity = (OsmEntity) ods.getEntity();
        if (entity == null) {
            return;
        }
        //        updateGeometry(entity, way);
        //        entity.getMatch().ifPresent(match -> {
        //            match.analyze();
        //            match.updateMatchTags();
        //        });
    }

    @Override
    public void updateGeometry(Node node) {
        // Default behavior: do nothing
    }

    public void updateTags(E entity, Map<String, String> tags) {
        parseKeys(entity, tags);
    }

    protected void parseKeys(E entity, Map<String, String> tags) {
        entity.setReferenceId(parseReferenceId(tags));
        entity.setSource(tags.get("source"));
        String sourceDate = tags.get("source:date");
        if (sourceDate != null) {
            try {
                entity.setSourceDate(LocalDate.parse(sourceDate));
            } catch (DateTimeParseException e) {
                entity.setSourceDate(null);
            }
        }
        String value = tags.get("start_date");
        if (value != null) {
            entity.setStartDate(StartDate.parse(value));
        }
    }

    protected abstract Object parseReferenceId(Map<String, String> tags);

    @SuppressWarnings("static-method")
    protected void normalizeTags(ManagedPrimitive primitive) {
        return;
    }

    protected abstract void updateGeometry(E entity,
            OsmPrimitive primitive);
}
