package org.openstreetmap.josm.plugins.ods.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaUtil {
    public static <T> List<T> collectOptional(Stream<Optional<T>> stream) {
        return stream.filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
