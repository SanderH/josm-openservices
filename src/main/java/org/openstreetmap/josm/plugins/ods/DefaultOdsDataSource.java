package org.openstreetmap.josm.plugins.ods;

import java.util.List;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.entities.EntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;

/**
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DefaultOdsDataSource implements OdsDataSource {
    private final OdsFeatureSource odsFeatureSource;
    private final EntityMapperFactory entityMapperFactory;
    private SimpleEntityMapper<?, ?> entityMapper;
    
    private boolean required = false;
    private Query query;
    private IdFactory idFactory;
    private boolean initialized;

    public <T extends OdsFeatureSource> DefaultOdsDataSource(T odsFeatureSource, Query query, 
            EntityMapperFactory entityMapperFactory) {
        super();
        this.odsFeatureSource = odsFeatureSource;
        this.query = query;
        this.entityMapperFactory = entityMapperFactory;
    }

    @Override
    public final OdsFeatureSource getOdsFeatureSource() {
        return odsFeatureSource;
    }
    
    @Override
    public final SimpleEntityMapper<?, ?> getEntityMapper() {
        return entityMapper;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public void initialize() throws OdsException {
        if (!initialized) {
            odsFeatureSource.initialize();
            entityMapper = entityMapperFactory.create(odsFeatureSource);
            initialized = true;
        }
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public void setIdFactory(DefaultIdFactory idFactory) {
        this.idFactory = idFactory;
    }

    /**
     * @see org.openstreetmap.josm.plugins.ods.OdsDataSource#getIdFactory()
     */
    @Override
    public IdFactory getIdFactory() {
        if (idFactory == null) {
            idFactory = new DefaultIdFactory(
                    getOdsFeatureSource().getIdAttribute());
        }
        return idFactory;
    }

    @Override
    public String getFeatureType() {
        return odsFeatureSource.getFeatureName();
    }

    @Override
    public MetaData getMetaData() {
        return getOdsFeatureSource().getMetaData();
    }

    @Override
    public List<String> getRequiredAttributes() {
        // TODO Auto-generated method stub
        return null;
    }
}
