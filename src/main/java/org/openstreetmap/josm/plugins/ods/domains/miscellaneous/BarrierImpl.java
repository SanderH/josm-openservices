package org.openstreetmap.josm.plugins.ods.domains.miscellaneous;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public class BarrierImpl extends AbstractEntity<BarrierEntityType> implements Barrier {
    private String type;
    private String detail;
    private String height;
    private String width;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public String getHeight() {
        return height;
    }

    @Override
    public String getWidth() {
        return width;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
    }
}
