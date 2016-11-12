package org.openstreetmap.josm.plugins.ods.wfs.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.geotools.data.wfs.internal.WFSStrategy;
import org.geotools.data.wfs.internal.v2_0.StrictWFS_2_0_Strategy;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

public class TestFileWFSSimpleFeatureReader {
    private static SimpleFeatureType verblijfsObjectFeatureType;
    
    @Test
    public void test() throws IOException {
        SimpleFeatureType featureType = getVerblijfsObjectFeatureType();
        WFSStrategy strategy = new StrictWFS_2_0_Strategy();
        File file = new File(getClass().getResource("inktpot_1_1_0/verblijfsobject.xml").getFile());
        try (
            FileWFSSimpleFeatureReader reader = 
                new FileWFSSimpleFeatureReader(strategy, file, featureType);
        ) {
            SimpleFeature feature = reader.next();
            assertEquals("3511EV", feature.getAttribute("postcode"));
        }
    }
    
    private static SimpleFeatureType getVerblijfsObjectFeatureType() {
        if (verblijfsObjectFeatureType == null) {
            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.setName("verblijfsobject");
            builder.setNamespaceURI("http://bag.geonovum.nl");
            builder.add("identificatie", BigDecimal.class);
            builder.add("oppervlakte", BigDecimal.class);
            builder.add("status", String.class);
            builder.add("gebruiksdoel", String.class);
            builder.add("openbare_ruimte", String.class);
            builder.add("huisnummer", BigDecimal.class);
            builder.add("huisletter", String.class);
            builder.add("toevoeging", String.class);
            builder.add("postcode", String.class);
            builder.add("woonplaats", String.class);
            builder.add("actualiteitsdatum", Date.class);
            builder.add("geometrie", Geometry.class, "EPSG:28992");
            builder.add("bouwjaar", BigDecimal.class);
            builder.add("pandidentificatie", BigDecimal.class);
            builder.add("pandstatus", String.class);
            builder.add("pandgeometrie", Geometry.class, "EPSG:28992");
            verblijfsObjectFeatureType = builder.buildFeatureType();
        }
        return verblijfsObjectFeatureType;
    }
}
