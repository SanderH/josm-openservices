package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Map;

public class QueryParameterImpl implements QueryParameter {
    private final String name;

    public QueryParameterImpl(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> PreparedParameter prepare(Class<T> type) {
        return new PreparedParameterImpl();
    }

    public class PreparedParameterImpl implements PreparedParameter {
        private Object value;

        /* (non-Javadoc)
         * @see org.openstreetmap.josm.plugins.ods.storage.query.PreparedParameter#setParameters(java.util.Map)
         */
        @Override
        public void setParameters(Map<String, Object> parameters) {
            value = parameters.get(getName());
        }

        /* (non-Javadoc)
         * @see org.openstreetmap.josm.plugins.ods.storage.query.PreparedParameter#evaluate()
         */
        @Override
        public Object evaluate(Object o) {
            return value;
        }
    }
}
