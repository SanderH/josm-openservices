package org.openstreetmap.josm.plugins.ods.geotools.filter;

public class FirstValueAggregator<T> implements Aggregator<T, T> {
    T value = null;
    boolean visited = false;
    private Class<T> sourceType;
    
    public FirstValueAggregator(Class<T> sourceType) {
        super();
        this.sourceType = sourceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(Object o) {
        if (!visited) {
            this.value = (T)o;
            visited = true;
        }
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
