package org.openstreetmap.josm.plugins.ods.wfs.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.geotools.data.FeatureReader;
import org.geotools.data.wfs.impl.WFSDataAccessFactory;
import org.geotools.data.wfs.internal.WFSStrategy;
import org.geotools.data.wfs.internal.parsers.XmlSimpleFeatureParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

public class FileWFSSimpleFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {
    private final SimpleFeatureType featureType;
    
    private InputStream is;
    private XmlSimpleFeatureParser parser;
    private SimpleFeature next;

    public FileWFSSimpleFeatureReader(FileWFSDataStore dataStore,
            SimpleFeatureType featureType) throws IOException {
        
        this(dataStore.getStrategy(), toFile(dataStore, featureType), featureType);
    }
    
    protected FileWFSSimpleFeatureReader(WFSStrategy strategy, File file,
            SimpleFeatureType featureType) throws IOException {
        this.featureType = featureType;
        QName qName = toQName(featureType.getName());
        if (!file.exists()) {
            throw new RuntimeException("Data file not available");
        }
        try {
            is = new FileInputStream(file);
            parser = new XmlSimpleFeatureParser(is, featureType,
                qName, WFSDataAccessFactory.AXIS_ORDER_COMPLIANT);
            next = parser.parse();
        }
        catch (IOException e) {
            if (parser != null) {
                parser.close();
            }
            if (is != null) {
                try {
                    is.close();
                }
                finally {
                    // Ignore the exception
                }
            }
            throw e;
        }
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public SimpleFeature next() throws IOException, IllegalArgumentException,
            NoSuchElementException {
        if (next == null) {
            throw new NoSuchElementException();
        }
        SimpleFeature result = next;
        next = parser.parse();
        return result;
    }

    @Override
    public boolean hasNext() throws IOException {
        return next != null;
    }

    @Override
    public void close() throws IOException {
        parser.close();
    }
    
    private static File toFile(FileWFSDataStore dataStore, SimpleFeatureType _featureType) {
        String fileName = _featureType.getTypeName() + ".xml";
        return new File(dataStore.getDirectory(), fileName);
    }
    
    private static QName toQName(Name name) {
        return new QName(name.getNamespaceURI(), name.getLocalPart());
    }
}
