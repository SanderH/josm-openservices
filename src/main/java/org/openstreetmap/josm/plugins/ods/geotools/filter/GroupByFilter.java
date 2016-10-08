package org.openstreetmap.josm.plugins.ods.geotools.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.openstreetmap.josm.plugins.ods.geotools.GroupByQuery;

public class GroupByFilter implements FeatureVisitor {
    final GroupSettings settings;
    private final SimpleFeatureType sourceFeatureType;
//    private final SimpleFeatureType targetFeatureType;
    private Map<Object[], Group> groups  = new HashMap<>();

    public GroupByFilter(SimpleFeatureType type, GroupByQuery query) {
        sourceFeatureType = type;
        settings = createSettings(type, query);
    }

    @Override
    public void visit(Feature f) {
        SimpleFeature feature = (SimpleFeature)f;
        assert feature.getFeatureType().equals(sourceFeatureType);
        Object[] key = getKey(feature);
        Group group = getGroup(key, true);
        group.add(feature);
    }
    
    private Object[] getKey(SimpleFeature feature) {
        int keySize = settings.getKeyIndexes().length;
        Object[] key = new Object[keySize];
        for (int i = 0; i < keySize; i++) {
            key[i] = feature.getAttribute(i);
        }
        return key;
    }
    
    private Group getGroup(Object[] key, boolean create) {
        Group group = groups.get(key);
        if (group == null && create) {
            group = settings.createGroup(key);
            groups.put(key, group);
        }
        return group;
    }
    
    private GroupSettings createSettings(SimpleFeatureType featureType, GroupByQuery query) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        List<String> properties = Arrays.asList(query.getPropertyNames());
        int[] keyIndexes = new int[query.getGroupBy().size()];
        int i=0;
        for (String propertyName : query.getGroupBy()) {
            properties.remove(propertyName);
            int index = featureType.indexOf(propertyName);
            AttributeDescriptor descriptor = featureType.getDescriptor(index);
            keyIndexes[i++] = index;
            builder.add(descriptor);
        }
        SimpleFeatureType targetType = builder.buildFeatureType();
        return new GroupSettings(keyIndexes, targetType);
    }
    
    class GroupSettings {
//        private SimpleFeatureType targetType;
        private SimpleFeatureBuilder featureBuilder;
        private int[] keyIndexes;
        AggregatorSetting<?, ?>[] aggregatorSettings;
        
        public GroupSettings(int[] keyIndexes, SimpleFeatureType targetType) {
            super();
            this.keyIndexes = keyIndexes;
//            this.targetType = targetType;
            this.featureBuilder = new SimpleFeatureBuilder(targetType);
        }

//        public SimpleFeatureType getTargetType() {
//            return targetType;
//        }
//
        public Group createGroup(Object[] key) {
            return new Group(key, createAggregators());
        }

        public int[] getKeyIndexes() {
            return keyIndexes;
        }
        
        private Aggregator<?, ?>[] createAggregators() {
            int size = aggregatorSettings.length;
            Aggregator<?, ?>[] aggregators = new Aggregator<?, ?>[size];
            for (int i = 0; i < size; i++) {
                aggregators[i] = aggregatorSettings[i].createAggregator();
            }
            return aggregators;
        }
        
        public SimpleFeatureBuilder getFeatureBuilder() {
            return featureBuilder;
        }
    }
    
    class AggregatorSetting<T, S> {
        private Class<T> sourceClass;
        private Class<? extends Aggregator<T, S>> aggregatorClass;
        private int index;
        
        public AggregatorSetting(Class<T> sourceClass,
                Class<? extends Aggregator<T, S>> aggregatorClass, int index) {
            super();
            this.sourceClass = sourceClass;
            this.aggregatorClass = aggregatorClass;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
        
        public Class<T> getSourceClass() {
            return sourceClass;
        }

        public Aggregator<T, S> createAggregator() {
            try {
                return aggregatorClass.newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
    
    class Group {
        private Object[] key;
        private Aggregator<?, ?>[] aggregators;

        public Group(Object[] key, Aggregator<?, ?>[] aggregators) {
            super();
            this.key = key;
            this.aggregators = aggregators;
        }
        
        void add(SimpleFeature feature) {
            int i = 0;
            for (AggregatorSetting<?, ?> setting : settings.aggregatorSettings) {
                Object value = feature.getAttribute(setting.getIndex());
                aggregators[i++].accept(value);
            }
        }
        
        public SimpleFeature getAggregate() {
            settings.getFeatureBuilder().addAll(key);
            for (Aggregator<?, ?> aggregator : aggregators) {
                settings.getFeatureBuilder().add(aggregator.getResult());
            }
            return settings.getFeatureBuilder().buildFeature(null);
        }
    }
}
