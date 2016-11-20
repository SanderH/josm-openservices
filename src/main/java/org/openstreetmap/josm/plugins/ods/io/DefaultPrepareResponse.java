package org.openstreetmap.josm.plugins.ods.io;

public class DefaultPrepareResponse implements PrepareResponse {
    private boolean outsideBoundary = false;
    private boolean maxFeatureExceeded = false;
    
    @Override
    public boolean isOutsideBoundary() {
        return outsideBoundary;
    }

    @Override
    public boolean isMaxFeatureExceeded() {
        return maxFeatureExceeded;
    }

    public void setOutsideBoundary(boolean outsideBoundary) {
        this.outsideBoundary = outsideBoundary;
    }

    public void setMaxFeatureExceeded(boolean maxFeatureExceeded) {
        this.maxFeatureExceeded = maxFeatureExceeded;
    }
}
