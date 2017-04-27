package org.openstreetmap.josm.plugins.ods.storage;

import java.util.Arrays;
import java.util.function.Function;

public class IndexKeyFactory {
    public static <T> IndexKey<T> createPropertyIndexKey(Class<T> baseClass,
            String ...properties) {
        Function<T, Object> indexFunction =
                new PropertiesIndexFunction<>(baseClass, Arrays.asList(properties));
        return new IndexKeyImpl<>(baseClass, indexFunction);
    }
}
