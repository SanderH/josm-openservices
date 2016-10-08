package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.Query;
import org.geotools.filter.FilterFactoryImpl;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 * Extension to Query that supports a group by functionality.
 * The current implementation is a bit clumsy. The actual grouping is done in
 * a feature iterator.
 * Using a Filter would allow for a much neater solution.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class UniqueIdQuery extends Query {
    // TODO Use Hints to discover the default filterFactory 
    private final GtFeatureSource featureSource;
    private final FilterFactory ff = new FilterFactoryImpl();
    private final String idProperty;

    public UniqueIdQuery(GtFeatureSource featureSource, String[] propertyNames, String idProperty) {
        super(featureSource.getFeatureName(), Filter.INCLUDE, propertyNames);
        this.featureSource = featureSource;
        this.idProperty = idProperty;
        this.sortBy = createSortBy();
    }
    
    public String getIdProperty() {
        return idProperty;
    }

    public void initialize() throws InvalidQueryException {
        checkAttributes();
    }

    private SortBy[] createSortBy() {
        return new SortBy[] {ff.sort(idProperty, SortOrder.ASCENDING)};
    }
    
    private void checkAttributes() throws InvalidQueryException {
//        FeatureType featureType = featureSource.getFeatureType();
//        List<String> unknownAttributes = new LinkedList<>();
//        for (String attribute : groupBy) {
//            if (featureType.getDescriptor(attribute) == null) {
//                unknownAttributes.add(attribute);
//            }
//        }
//        if (unknownAttributes.size() > 0) {
//            StringBuilder sb = new StringBuilder(I18n.tr(
//                    "One or more query attributes are unkown for feature ''{0}'': ", featureType.getName()));
//            sb.append(String.join(", ", unknownAttributes));
//            throw new InvalidQueryException(sb.toString());
//        }
    }
}
