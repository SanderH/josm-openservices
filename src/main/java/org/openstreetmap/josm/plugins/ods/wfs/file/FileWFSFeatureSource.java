package org.openstreetmap.josm.plugins.ods.wfs.file;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

class FileWFSFeatureSource implements OdsFeatureSource {
    
    /**
     * 
     */
    private final FileWFSHost host;
    private final CoordinateReferenceSystem crs;
    private final String srs;
    private final Long srid;
    private final SimpleFeatureType featureType;

    
    public FileWFSFeatureSource(FileWFSHost host, String feature) {
        super();
        this.host = host;
        try {
            this.featureType = host.getFeatureType(new QName(feature));
        } catch (IOException | CRSException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.crs = featureType.getCoordinateReferenceSystem();
        srs = CRSUtil.getSrs(crs);
        srid = CRSUtil.getSrid(crs);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void initialize() throws OdsException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSRS() {
        return srs;
    }

    @Override
    public Long getSRID() {
        return srid;
    }

    @Override
    public MetaData getMetaData() {
        return new MetaData(host.getMetaData());
    }

    @Override
    public String getIdAttribute() {
        return "id";
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public String getFeatureName() {
        return featureType.getName().getLocalPart();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }
}