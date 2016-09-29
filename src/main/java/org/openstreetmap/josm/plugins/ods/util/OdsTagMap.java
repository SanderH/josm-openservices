package org.openstreetmap.josm.plugins.ods.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Mao for osm tag with special null value handling:
 * If a null value is added for a key, the key will be removed.
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsTagMap extends HashMap<String, String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsTagMap() {
        super();
    }
    
    public OdsTagMap(String[][] theTags) {
        for (String[] tag : theTags) {
            put(tag[0], tag[1]);
        }
    }

    @Override
    public String put(String key, String value) {
        if (value == null) {
            String oldValue = get(key);
            this.remove(key);
            return oldValue;
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> tags) {
        for (Entry<? extends String, ? extends String> entry : tags.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
}
