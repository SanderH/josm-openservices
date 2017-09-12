package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Map;

public interface PreparedParameter extends PreparedExpression {

    void setParameters(Map<String, Object> parameters);
}