package org.openstreetmap.josm.plugins.ods.io;

/**
 * Response values for a download preparation.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface PrepareResponse {
    /**
     * Report if the requested boundary is outside the reported bounds of
     * the service that is being accessed.
     * If so, further processing makes no sense as no features will be
     * found anyway.
     * 
     * @return true if the requested boundary is outside the reported bounds.
     */
    public boolean isOutsideBoundary();
    
    /**
     * Report if the query would exceed the maximum number of features the
     * service can provide.
     * The service must support counting for this to work.
     * 
     * @return
     */
    public boolean isMaxFeatureExceeded();
    
}
