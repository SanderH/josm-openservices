package org.openstreetmap.josm.plugins.ods.domains.miscellaneous;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Barrier extends Entity {
    public String getType();
    public String getDetail();
    public String getHeight();
    public String getWidth();

    public void setType(String type);
    public void setDetail(String detail);
    public void setHeight(String heigt);
    public void setWidth(String width);
}
