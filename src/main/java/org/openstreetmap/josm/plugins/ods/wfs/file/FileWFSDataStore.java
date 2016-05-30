package org.openstreetmap.josm.plugins.ods.wfs.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultServiceInfo;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.LockingManager;
import org.geotools.data.Query;
import org.geotools.data.ServiceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.v1_1_0.WFSStrategy;
import org.geotools.data.wfs.v1_1_0.parsers.EmfAppSchemaParser;
import org.geotools.feature.NameImpl;
//import org.geotools.data.wfs.internal.WFSStrategy;
//import org.geotools.data.wfs.internal.parsers.EmfAppSchemaParser;
import org.geotools.xml.Configuration;
import org.geotools.xml.DOMParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.w3c.dom.Document;

import net.opengis.ows10.KeywordsType;
import net.opengis.wfs.FeatureTypeType;
import net.opengis.wfs.WFSCapabilitiesType;

public class FileWFSDataStore implements DataStore {
    private final File directory;
    private final WFSStrategy strategy;
    private WFSCapabilitiesType parsedCapabilities;
//    private final String ns = "";
    private final Configuration configuration;
    private final Map<String, FeatureTypeType> featureTypeTypes = new HashMap<>();
    private final Map<Name, SimpleFeatureType> featureTypesByName = new HashMap<>();
    private final Map<String, SimpleFeatureType> featureTypesByLocalName = new HashMap<>();
    private List<Name> names;
    private String[] typeNames;
    private DefaultServiceInfo info;
    
    public FileWFSDataStore(org.geotools.data.wfs.v1_1_0.WFSStrategy strategy, File dir) throws IOException {
        this.strategy = strategy;
        System.setProperty("org.geotools.xml.forceSchemaImport", "true");
        this.directory = dir;
        assert dir.isDirectory();
        this.configuration = strategy.getWfsConfiguration();
        getCapabilities();
    }
    
    public File getDirectory() {
        return directory;
    }
    
    public WFSStrategy getStrategy() {
        return strategy;
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
    
    private Document parseXml(InputStream is) throws IOException {
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
    
    private URL getDescribeFeatureTypeURL() throws IOException {
        File file = new File(directory, "featureTypes.xsd");
        if (!file.exists()) {
            throw new IOException("The featuretypes.xsd file could not be found.");
        }
        return file.toURI().toURL();
    }
    
    private void collectFeatureTypes() {
        @SuppressWarnings("unchecked")
        EList<FeatureTypeType> list = parsedCapabilities.getFeatureTypeList().getFeatureType();
        for (FeatureTypeType ftt : list) {
            featureTypeTypes.put(ftt.getTitle(), ftt);
            String srs = ftt.getDefaultSRS();
            try {
                CoordinateReferenceSystem crs = CRSUtil.getCrs(srs);
                SimpleFeatureType type = EmfAppSchemaParser.parseSimpleFeatureType(configuration, ftt.getName(), 
                        getDescribeFeatureTypeURL(), crs, strategy.getNamespaceURIMappings(), strategy.getFieldTypeMappings(), true);
                QName qName = ftt.getName();
                featureTypesByLocalName.put(qName.getLocalPart(), type);
                Name name = new NameImpl(ftt.getName().getNamespaceURI(), ftt.getName().getLocalPart());
                featureTypesByName.put(name, type);
            } catch (CRSException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
//    public SimpleFeatureType getFeatureType(QName typeName) throws IOException, CRSException {
//        SimpleFeatureType type = featureTypes.get(typeName);
//        if (type == null) {
//            FeatureTypeType ftt = featureTypeTypes.get(typeName.getLocalPart());
//            if (ftt == null) {
//                return null;
//            }
//            String srs = ftt.getDefaultSRS();
//            CoordinateReferenceSystem crs = CRSUtil.getCrs(srs);
//            type = EmfAppSchemaParser.parseSimpleFeatureType(configuration, typeName, schemaLocation, crs, strategy.getNamespaceURIMappings(), strategy.getFieldTypeMappings(), true);
//        }
//        return type;
//    }

    @Override
    public ServiceInfo getInfo() {
        if (info == null) {
            info = new DefaultServiceInfo();
            @SuppressWarnings("unchecked")
            List<KeywordsType> capsKeywords = parsedCapabilities.getServiceIdentification().getKeywords();
            info.setKeywords(extractKeywords(capsKeywords));
            info.setSchema(getDirectory().toURI());
        }
        // TODO Auto-generated method stub
        return info;
    }

    @Override
    public void createSchema(SimpleFeatureType featureType) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSchema(Name typeName, SimpleFeatureType featureType)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSchema(Name typeName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Name> getNames() throws IOException {
        if (names == null) {
            names = new ArrayList<>(featureTypesByName.size());
            names.addAll(featureTypesByName.keySet());
        }
        return names;
    }

    @Override
    public SimpleFeatureType getSchema(Name name) throws IOException {
        return featureTypesByName.get(name);
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateSchema(String typeName, SimpleFeatureType featureType)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSchema(String typeName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getTypeNames() throws IOException {
        if (typeNames == null) {
            List<String> names = new ArrayList<>(getNames().size());
            for (Name name : getNames()) {
                names.add(name.getLocalPart());
            }
            typeNames = names.toArray(new String[0]);
        }
        return typeNames;
    }

    @Override
    public SimpleFeatureType getSchema(String typeName) throws IOException {
        return featureTypesByLocalName.get(typeName);
    }

    // TODO consider caching feature sources.
    @Override
    public SimpleFeatureSource getFeatureSource(String typeName)
            throws IOException {
        SimpleFeatureType featureType = featureTypesByLocalName.get(typeName);
        return new FileWFSSimpleFeatureSource(this, featureType);
    }

    @Override
    public SimpleFeatureSource getFeatureSource(Name typeName)
            throws IOException {
        return getFeatureSource(typeName.getLocalPart());
    }

    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(
            Query query, Transaction transaction) throws IOException {
        SimpleFeatureType featureType = getSchema(query.getTypeName());
        return new FileWFSSimpleFeatureReader(this, featureType);
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(
            String typeName, Filter filter, Transaction transaction)
                    throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(
            String typeName, Transaction transaction) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriterAppend(
            String typeName, Transaction transaction) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LockingManager getLockingManager() {
        // TODO Auto-generated method stub
        return null;
    }
    
    private Set<String> extractKeywords(List<KeywordsType> keywordsList) {
        Set<String> keywords = new HashSet<String>();
        for (KeywordsType keys : keywordsList) {
            for (Object key : keys.getKeyword()) {
                keywords.add((String) key);
            }
        }
        return keywords;
    }
}
