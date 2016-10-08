package org.openstreetmap.josm.plugins.ods.geotools.filter;

public class LastValueAggregator<T> implements Aggregator<T, T> {
    private T value = null;
    private Class<T> sourceType;
    
    public LastValueAggregator(Class<T> sourceType) {
        super();
        this.sourceType = sourceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(Object o) {
            this.value = (T)o;
    }

    @Override
    public Class<T> getSourceType() {
        return sourceType;
    }

    @Override
    public T getResult() {
        return value;
    }

}
