package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;

public class OdsNodeImpl implements OdsNode {
    private final Node osm;
    private NodeMatch nodeMatch;
    private Entity entity;
    private List<NodeReferrer> referrers = new LinkedList<>();
    
    public OdsNodeImpl(Node osm) {
        super();
        this.osm = osm;
    }

    @Override
    public void addReferrer(NodeReferrer referrer) {
        referrers.add(referrer);
    }

    @Override
    public List<NodeReferrer> getReferrers() {
        return referrers;
    }

    @Override
    public Integer getIndex(OdsWay referringWay) {
        for (NodeReferrer referrer : getReferrers()) {
            if (referrer.getWay().equals(referringWay)) {
                return referrer.getIndex();
            }
        }
        return null;
    }
    
    @Override
    public NodeMatch getMatch() {
        return nodeMatch;
    }

    @Override
    public void setMatch(NodeMatch nodeMatch) {
        this.nodeMatch = nodeMatch;
    }

    @Override
    public Node getPrimitive() {
        return osm;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}
