package org.openstreetmap.josm.plugins.ods.storage;

import java.lang.reflect.Method;

public class StorageUtil {
    public static boolean hasProperty(Class<?> clazz, String property) {
        return createGetter(clazz, property) != null;
    }

    public static Method createGetter(Class<?> clazz, String property) {
        for (Method method : clazz.getMethods()) {
            if (method.getParameterCount() > 0) {
                continue;
            }
            Class<?> returnType = method.getReturnType();
            if (returnType == Void.class) {
                continue;
            }
            String name = method.getName();
            if (name.startsWith("get") && name.substring(3).equalsIgnoreCase(property)) {
                return method;
            }
            if (name.startsWith("is") && name.substring(2).equalsIgnoreCase(property)) {
                return method;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Propert '%s' in class '%s' could not be found" , clazz.getName(), property));
    }

}
