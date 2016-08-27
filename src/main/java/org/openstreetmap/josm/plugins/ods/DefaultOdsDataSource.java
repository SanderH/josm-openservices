package org.openstreetmap.josm.plugins.ods;

import java.util.List;

import org.geotools.data.Query;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
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
    private List<String> requiredProperties;
    private final EntityMapperFactory entityMapperFactory;
    private SimpleEntityMapper<?, ?> entityMapper;
    
    private boolean required = false;
    private Query query;
    private IdFactory idFactory;
    private boolean initialized;

    public <T extends OdsFeatureSource> DefaultOdsDataSource(T odsFeatureSource, Query query, 
            EntityMapperFactory entityMapperFactory) {
        this(odsFeatureSource, query, entityMapperFactory, null);
    }
    
    public <T extends OdsFeatureSource> DefaultOdsDataSource(T odsFeatureSource, Query query, 
            EntityMapperFactory entityMapperFactory, List<String> requiredProperties) {
        super();
        this.odsFeatureSource = odsFeatureSource;
        this.query = query;
        this.requiredProperties = requiredProperties;
        this.entityMapperFactory = entityMapperFactory;
    }

    @Override
    public final OdsFeatureSource getOdsFeatureSource() {
        return odsFeatureSource;
    }
    
    public List<String> getRequiredProperties() {
        return requiredProperties;
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
            initializePropertyNames();
            initialized = true;
        }
    }

    void initializePropertyNames() throws OdsException {
        if (getRequiredProperties() != null) {
            FeatureType featureType = getOdsFeatureSource().getFeatureType();
            for (String propertyName : getRequiredProperties()) {
                PropertyDescriptor descriptor = featureType.getDescriptor(propertyName);
                if (descriptor == null) {
                    throw new OdsException(String.format(
                        "Property '%s' doesn't exist for feature type '%s'",
                        featureType.getName(), propertyName));
                }
            }
            query.setPropertyNames(getRequiredProperties());
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
