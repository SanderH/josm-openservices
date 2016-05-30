package org.openstreetmap.josm.plugins.ods.geotools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;

public class FeaturePropertyHandlerFactoryTest {
    private static SimpleFeatureType featureType;
    private static SimpleFeature feature;

    @BeforeClass
    public static void setupBeforeClass() {
        featureType = buildFeatureType();
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        feature = builder.buildFeature("test", new Object[] {"naam", 2, 33333333, 4.4, '5'});
    }
    
    @Test
    public void testConstructor() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Integer> handler = factory.createPropertyHandler(featureType, Integer.class, 1);
        assertNotNull("The handler should exist", handler);
    }
    
    @Test
    public void testGetIntegerById() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Integer> handler = factory.createPropertyHandler(featureType, Integer.class, 1);
        assertEquals((Integer)2, handler.get(feature));
    }
    
    @Test
    public void testGetIntegerByName() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Integer> handler = factory.createPropertyHandler(featureType, Integer.class, "integer");
        assertEquals((Integer)2, handler.get(feature));
    }
    
    @Test
    public void testGetString() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, String> handler = factory.createPropertyHandler(featureType, String.class, "name");
        assertEquals("naam", handler.get(feature));
    }
    
    @Test
    public void testGetDouble() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Double> handler = factory.createPropertyHandler(featureType, Double.class, "double");
        assertEquals((Double)4.4, handler.get(feature));
    }
    
    @Test
    public void testGetLong() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Long> handler = factory.createPropertyHandler(featureType, Long.class, "long");
        assertEquals((Long)33333333L, handler.get(feature));
    }
    
    @Test
    public void testGetCharacter() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Character> handler = factory.createPropertyHandler(featureType, Character.class, "char");
        assertEquals((Character)'5', handler.get(feature));
    }
    
    @Test
    public void testSetIntegerById() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Integer> handler = factory.createPropertyHandler(featureType, Integer.class, 1);
        handler.set(feature, -7);
        assertEquals((Integer)(-7), handler.get(feature));
    }
    
    @Test
    public void testSetIntegerByName() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Integer> handler = factory.createPropertyHandler(featureType, Integer.class, "integer");
        handler.set(feature, 345);
        assertEquals((Integer)345, handler.get(feature));
    }
    
    @Test
    public void testSetString() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, String> handler = factory.createPropertyHandler(featureType, String.class, "name");
        handler.set(feature, "foo");
        assertEquals("foo", handler.get(feature));
    }
    
    @Test
    public void testSetDouble() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Double> handler = factory.createPropertyHandler(featureType, Double.class, "double");
        handler.set(feature, -7.8);
        assertEquals((Double)(-7.8), handler.get(feature));
    }
    
    @Test
    public void testSetLong() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Long> handler = factory.createPropertyHandler(featureType, Long.class, "long");
        handler.set(feature, -56L);
        assertEquals((Long)(-56L), handler.get(feature));
    }
    
    @Test
    public void testSetCharacter() {
        FeaturePropertyHandlerFactory factory = new FeaturePropertyHandlerFactory();
        PropertyHandler<SimpleFeature, Character> handler = factory.createPropertyHandler(featureType, Character.class, "char");
        handler.set(feature, 'C');
        assertEquals((Character)'C', handler.get(feature));
    }
    
    private static SimpleFeatureType buildFeatureType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("testType");
        builder.setSRS("EPSG:28992");

        builder.add("name", String.class);
        builder.add("integer", Integer.class);
        builder.add("long", Long.class);
        builder.add("double", Double.class);
        builder.add("char", Character.class);
        return builder.buildFeatureType();
    }
}
