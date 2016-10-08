package org.openstreetmap.josm.plugins.ods.geotools.filter;

public class DoubleSumAggregator implements Aggregator<Double, Double> {
    Double value = 0.0;
    
    @Override
    public void accept(Object o) {
        this.value += (Double)o;
    }

    
    @Override
    public Class<Double> getSourceType() {
        return Double.class;
    }

    @Override
    public Double getResult() {
        return value;
    }
}
