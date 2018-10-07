package org.openstreetmap.josm.plugins.ods.wfs.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.geotools.data.DataSourceException;
import org.geotools.data.wfs.internal.WFSStrategy;
import org.geotools.data.wfs.internal.parsers.EmfAppSchemaParser;
//import org.geotools.data.wfs.internal.WFSStrategy;
//import org.geotools.data.wfs.internal.parsers.EmfAppSchemaParser;
import org.geotools.xml.Configuration;
import org.geotools.xml.DOMParser;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.ServiceException;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.io.AbstractHost;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.w3c.dom.Document;

import net.opengis.wfs.FeatureTypeType;
import net.opengis.wfs.WFSCapabilitiesType;

/**
 * Implementation of {@link Host} that reads data from (a collection of) files
 * containing WFS data.
 * Intended to be used to for test data.
 * This class is still work in progress.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class FileWFSHost extends AbstractHost {
    private final File directory;
    private final URL schemaLocation;
    private final WFSStrategy strategy;
    private WFSCapabilitiesType parsedCapabilities;
    private final Configuration configuration;
    private final Map<String, FeatureTypeType> featureTypeTypes = new HashMap<>();
    private final Map<String, SimpleFeatureType> featureTypes = new HashMap<>();

    public FileWFSHost(WFSStrategy strategy, File dir) throws IOException {
        // TODO should we extend AbstractHost ?
        super("FileWFS", dir.toString());
        this.strategy = strategy;
        System.setProperty("org.geotools.xml.forceSchemaImport", "true");
        this.directory = dir;
        assert dir.isDirectory();
        this.configuration = strategy.getWfsConfiguration();
        //        configuration.
        getCapabilities();
        try {
            schemaLocation = new File(dir, "featureTypes.xsd").toURI().toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //        CRSUtil.
        //        this.crs = crs;
        //        collectFeaturTypes();
    }

    private void getCapabilities() throws IOException {
        URL url = getGetCapabilitiesURL();
        try (
                InputStream is = url.openStream();
                ) {
            Document rawDocument = parseXml(is);
            is.close();
            parsedCapabilities = (WFSCapabilitiesType) parseCapabilities(rawDocument);
            collectFeatureTypes();
            //            this.capabilities = WFSGetCapabilities.create(parsedCapabilities, rawDocument);
        }
    }

    private static Document parseXml(InputStream is) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setValidating(false);
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            return documentBuilder.parse(is);
        } catch (Exception e) {
            throw new IOException("Error parsing capabilities document: " + e.getMessage(), e);
        }
    }

    private EObject parseCapabilities(final Document document)
            throws IOException {

        // final Parser parser = new Parser(wfsConfig);
        DOMParser parser = new DOMParser(configuration, document);
        final Object parsed;
        try {
            parsed = parser.parse();
        } catch (Exception e) {
            throw new DataSourceException("Exception parsing WFS capabilities", e);
        }
        if (parsed == null) {
            throw new DataSourceException("WFS capabilities was not parsed");
        }
        if (!(parsed instanceof WFSCapabilitiesType) && !(parsed instanceof net.opengis.wfs20.WFSCapabilitiesType)) {
            throw new DataSourceException("Expected WFS Capabilities, got " + parsed);
        }
        EObject object = (EObject) parsed;
        return object;
    }

    private URL getGetCapabilitiesURL() throws IOException {
        File capabilitiesFile = new File(directory, "capabilities.xml");
        if (!capabilitiesFile.exists()) {
            throw new IOException("The capabilities.xml file could not be found.");
        }
        return capabilitiesFile.toURI().toURL();
    }

    private void collectFeatureTypes() {
        @SuppressWarnings("unchecked")
        EList<FeatureTypeType> list = parsedCapabilities.getFeatureTypeList().getFeatureType();
        for (FeatureTypeType featureTypeType : list) {
            featureTypeTypes.put(featureTypeType.getTitle(), featureTypeType);
        }
    }

    public SimpleFeatureType getFeatureType(QName typeName) throws IOException, CRSException {
        SimpleFeatureType type = featureTypes.get(typeName);
        if (type == null) {
            FeatureTypeType ftt = featureTypeTypes.get(typeName.getLocalPart());
            if (ftt == null) {
                return null;
            }
            String srs = ftt.getDefaultSRS();
            CoordinateReferenceSystem crs = CRSUtil.getCrs(srs);
            type = EmfAppSchemaParser.parseSimpleFeatureType(configuration, typeName, schemaLocation, crs, strategy.getFieldTypeMappings());
        }
        return type;
    }

    @Override
    public boolean hasFeatureType(String feature) throws ServiceException {
        return featureTypeTypes.containsKey(feature);
    }
}
