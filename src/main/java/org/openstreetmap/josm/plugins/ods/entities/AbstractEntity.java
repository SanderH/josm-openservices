package org.openstreetmap.josm.plugins.ods.entities;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.issues.Issue;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractEntity implements Entity {
    private Object primaryId;
    private Object referenceId;
    private DownloadResponse response;
    private LocalDate sourceDate;
    private String source;
    private StartDate startDate;
    private Geometry geometry;
    private EntityStatus status = EntityStatus.UNKNOWN;
    private boolean incomplete = true;
    private ManagedPrimitive primitive;
    private Map<String, Issue> issues = null;
    private final Map<Class<? extends Entity>, Match<? extends Entity>> matches = new HashMap<>();

    @Override
    public void setPrimaryId(Object primaryId) {
        this.primaryId = primaryId;
    }

    @Override
    public Object getPrimaryId() {
        return primaryId;
    }

    @Override
    public Object getReferenceId() {
        return referenceId;
    }

    @Override
    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public void setDownloadResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public DownloadResponse getDownloadResponse() {
        return response;
    }

    @Override
    public void setSourceDate(LocalDate date) {
        this.sourceDate = date;
    }

    @Override
    public LocalDate getSourceDate() {
        return sourceDate;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setStartDate(StartDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public StartDate getStartDate() {
        return startDate;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    @Override
    public void setPrimitive(ManagedPrimitive primitive) {
        this.primitive = primitive;
    }

    @Override
    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    @Override
    public EntityStatus getStatus() {
        return status;
    }

    @Override
    public Long getPrimitiveId() {
        return (primitive == null ? null : primitive.getUniqueId());
    }

    @Override
    public ManagedPrimitive getPrimitive() {
        return primitive;
    }

    @Override
    public Collection<Match<? extends Entity>> getMatches() {
        return matches.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> Match<E> getMatch(Class<E> role) {
        return (Match<E>) matches.get(role);
    }

    @Override
    public <E extends Entity> void addMatch(Match<E> match) {
        assert match.getRole() != null;
        this.matches.put(match.getRole(),  match);
    }

    @Override
    public Collection<? extends Issue> getIssues() {
        if (issues != null) {
            return issues.values();
        }
        return Collections.emptySet();
    }

    @Override
    public void addIssue(Issue issue) {
        if (issues == null) {
            issues = new HashMap<>();
        }
        issues.put(issue.getClass().getCanonicalName(), issue);
    }

    @Override
    public boolean hasIssues() {
        return issues != null;
    }

    @Override
    public Issue getIssue(Class<? extends Issue> type) {
        return issues.get(type.getCanonicalName());
    }
}
