package org.openstreetmap.josm.plugins.ods.properties.transform;

public class TypeTransforms {
    public static class DoubleToLong extends SimpleTypeTransform<Double, Long> {

        public DoubleToLong() {
            super(Double.class, Long.class, null);
        }
    
        @Override
        public Long apply(Double source) {
            if (source == null) {
                return null;
            }
            return source.longValue();
        }
    }
    
    public static class DoubleToInteger extends SimpleTypeTransform<Double, Integer> {

        public DoubleToInteger() {
            super(Double.class, Integer.class, null);
        }
    
        @Override
        public Integer apply(Double source) {
            if (source == null) {
                return null;
            }
            return source.intValue();
        }
    }
    
    public static class ObjectToString extends SimpleTypeTransform<Object, String> {

        public ObjectToString() {
            super(Object.class, String.class, null);
        }
    
        @Override
        public String apply(Object source) {
            if (source == null) {
                return null;
            }
            return source.toString();
        }
    }
}
