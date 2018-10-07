package org.openstreetmap.josm.plugins.ods.domains.miscellaneous;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataEntrance;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class EntrancePrimitiveBuilder extends AbstractEntityPrimitiveBuilder<OpenDataEntrance> {

    public EntrancePrimitiveBuilder(OdsModule module, EntityDao<OpenDataEntrance> dao) {
        super(module, dao);
    }

    @Override
    public void createPrimitive(OpenDataEntrance entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
            Map<String, String> tags = new HashMap<>();
            tags.put("entrance", entity.getType());
            ManagedPrimitive primitive = getPrimitiveFactory().create(entity.getGeometry(), tags);
            if (primitive != null) {
                entity.setPrimitive(primitive);
                primitive.setEntity(entity);
            }
            else {
                Logging.warn(I18n.tr("Primitive is null for ''{0}''.", entity.getClass().getSimpleName()));
            }
        }
    }
}
