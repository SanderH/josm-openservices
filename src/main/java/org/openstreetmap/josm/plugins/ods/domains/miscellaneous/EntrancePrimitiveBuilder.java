package org.openstreetmap.josm.plugins.ods.domains.miscellaneous;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Entrance;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.tools.I18n;

public class EntrancePrimitiveBuilder extends AbstractEntityPrimitiveBuilder<Entrance> {
    
    public EntrancePrimitiveBuilder(LayerManager layerManager) {
        super(layerManager, Entrance.class);
    }

    @Override
    public void createPrimitive(Entrance entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
            Map<String, String> tags = new HashMap<>();
            tags.put("entrance", entity.getType());
            ManagedPrimitive primitive = getPrimitiveFactory().create(entity.getGeometry(), tags);
            if (primitive != null) {
                entity.setPrimitive(primitive);
                primitive.setEntity(entity);
            }
            else {
                Main.warn(I18n.tr("Primitive is null for ''{0}''.", entity.getClass().getSimpleName()));
            }
        }
    }
}
