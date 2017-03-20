package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openstreetmap.josm.data.osm.MultipolygonBuilder;
import org.openstreetmap.josm.data.osm.MultipolygonBuilder.JoinedPolygon;
import org.openstreetmap.josm.data.osm.MultipolygonBuilder.JoinedPolygonCreationException;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.visitor.paint.relations.Multipolygon;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.crs.InvalidMultiPolygonException;
import org.openstreetmap.josm.plugins.ods.primitives.ComplexManagedRing.RingMember;
import org.openstreetmap.josm.tools.I18n;

public class ManagedPrimitiveFactory {
    private final LayerManager layerManager;
    
    public ManagedPrimitiveFactory(LayerManager layerManager) {
        super();
        this.layerManager = layerManager;
    }

    public ManagedPrimitive createArea(OsmPrimitive primitive) throws InvalidGeometryException {
        switch (primitive.getDisplayType()) {
        case CLOSEDWAY:
            return createSimplePolygon((Way)primitive);
        case MULTIPOLYGON:
        case RELATION:
            return createArea((Relation)primitive);
        default:
            throw new IllegalArgumentException(I18n.tr("Invalid area. Type of primitive {0} is {1}",
                    primitive.getId(), primitive.getDisplayType()));
        }
    }
    
    public ManagedPrimitive createArea(Relation multiPolygon) throws InvalidGeometryException {
        // Extract outer/inner members from multipolygon relation
        final Multipolygon mpm = new Multipolygon(multiPolygon);
        boolean incomplete = false;
        for (Way way : mpm.getOuterWays()) {
            incomplete |= way.isIncomplete();
        }
        for (Way way : mpm.getInnerWays()) {
            incomplete |= way.isIncomplete();
        }
        if (incomplete) {
            return new ManagedJosmMultiPolygonImpl(layerManager, multiPolygon, true);
        }
        // Construct complete rings for the inner/outer members
        final List<MultipolygonBuilder.JoinedPolygon> outerRings;
        final List<MultipolygonBuilder.JoinedPolygon> innerRings;
            try {
                outerRings = MultipolygonBuilder.joinWays(mpm.getOuterWays());
            } catch (JoinedPolygonCreationException e) {
                throw new InvalidMultiPolygonException(multiPolygon, 
                    I18n.tr("The outer ways of (multi)polygon {0} don't form 1 or more closed ways", multiPolygon.getId()));
            }
            try {
                innerRings = MultipolygonBuilder.joinWays(mpm.getInnerWays());
            } catch (JoinedPolygonCreationException e) {
                throw new InvalidMultiPolygonException(multiPolygon, 
                    I18n.tr("The inner ways of (multi)polygon {0} don't form 1 or more closed ways", multiPolygon.getId()));
            }
            List<ManagedRing> managedOuterRings = new ArrayList<>(outerRings.size());
            List<ManagedRing> managedInnerRings = new ArrayList<>(innerRings.size());
            for (JoinedPolygon outer : outerRings) {
                managedOuterRings.add(createRing(outer.ways, outer.reversed));
            }
            for (JoinedPolygon inner : innerRings) {
                managedInnerRings.add(createRing(inner.ways, inner.reversed));
            }
            return new ManagedJosmMultiPolygonImpl(layerManager, managedOuterRings, managedInnerRings, multiPolygon);
    }
    
    public ManagedNode createNode(Node node) {
        // TODO Handle class cast exception
        ManagedNode managedNode = (ManagedNode) layerManager.getManagedPrimitive(node);
        if (managedNode == null) {
            managedNode = new ManagedNodeImpl(layerManager, node);
            layerManager.register(node, managedNode);
        }
        return managedNode;
    }

    public ManagedWay createWay(Way way) {
        return createWay(way, true);
    }

    public ManagedWay createWay(Way osmWay, boolean register) {
        ManagedWay odsWay = (ManagedWay) layerManager.getManagedPrimitive(osmWay);
        if (odsWay == null) {
            odsWay = new SimpleManagedWay(layerManager, osmWay);
//            int i=0;
//            for (ManagedNode odsNode : odsNodes) {
//                odsNode.addReferrer(odsWay);
//            }
            if (register) {
                layerManager.register(osmWay, odsWay);
            }
        }
        return odsWay;
    }
    
    public SimpleManagedPolygon createSimplePolygon(Way way) {
        return createManagedPolygon(way, true);
    }

    public SimpleManagedPolygon createManagedPolygon(Way osmWay, boolean register) {
        assert osmWay.getDisplayType() == OsmPrimitiveType.CLOSEDWAY;
        SimpleManagedPolygon odsPolygon = (SimpleManagedPolygon) layerManager.getManagedPrimitive(osmWay);
        if (odsPolygon == null) {
            ManagedWay odsWay = createWay(osmWay, false);
            odsPolygon = new SimpleManagedPolygon(odsWay, osmWay.getKeys());
            if (register) {
                layerManager.register(osmWay, odsPolygon);
            }
        }
        return odsPolygon;
    }
    
    public SimpleManagedRing createRing(Way way) {
        assert way.getDisplayType() == OsmPrimitiveType.CLOSEDWAY;
        SimpleManagedRing managedRing = (SimpleManagedRing) layerManager.getManagedPrimitive(way);
        if (managedRing == null) {
            ManagedWay managedWay = createWay(way, false);
            managedRing = new SimpleManagedRing(managedWay);
        }
        layerManager.register(way, managedRing);
        return managedRing;
    }
    
    public ManagedRing createRing(List<Way> ways, List<Boolean> reversed) {
        assert ways.size() == reversed.size();
        if (ways.size() == 1) {
            return createRing(ways.get(0));
        }
        Iterator<Boolean> revIterator = reversed.iterator();
        List<RingMember> ringMembers = new ArrayList<>(ways.size());
        for (Way way : ways) {
            Boolean isReversed = revIterator.next();
            ManagedWay managedWay = createWay(way, false);
            ringMembers.add(new RingMember(managedWay, isReversed));
        }
        return new ComplexManagedRing(layerManager, ringMembers);
    }
    
    public void update(ManagedWay odsWay) {
//        Set<ManagedNode> oldNodes = new HashSet<>(odsWay.getNodes());
//        if (odsWay instanceof SimpleManagedWay) {
//            Way osmWay = ((SimpleManagedWay)odsWay).getWay();
//            setWayNodes(odsWay, osmWay);
//        }
//        oldNodes.removeAll(odsWay.getNodes());
//        for (ManagedNode odsNode : oldNodes) {
//            odsNode.removeReferrer(odsWay);
//        }
    }
}