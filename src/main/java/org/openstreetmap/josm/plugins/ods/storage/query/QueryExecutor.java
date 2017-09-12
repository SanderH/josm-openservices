package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Map;

public interface QueryExecutor<T> {
    //    public Class<T> getResultType();

    public ResultSet<T> run(Map<String, Object> parameters);

}
