package org.openstreetmap.josm.plugins.ods.matching.geometry;

import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;

public class ManagedRingMatch {
    private final ManagedRing localRing;
    private final ManagedRing remoteRing;
    private MatchStatus status;
    
    public ManagedRingMatch(ManagedRing localRing, ManagedRing remoteRing, MatchStatus status) {
        super();
        this.localRing = localRing;
        this.remoteRing = remoteRing;
    }
    
    public ManagedRing getLocalRing() {
        return localRing;
    }
    
    public ManagedRing getRemoteRing() {
        return remoteRing;
    }
    
    public MatchStatus getStatus() {
        return status;
    }
}
