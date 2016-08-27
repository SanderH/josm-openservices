package org.openstreetmap.josm.plugins.ods.properties.pojo;

import java.lang.reflect.Method;

public class PojoUtils {
    public static String getPropertyName(Method method, String prefix) {
        String methodName = method.getName();
        assert methodName.startsWith(prefix);
        assert methodName.length() > prefix.length();
        int pos = prefix.length();
        return methodName.substring(pos, 1).toLowerCase()
                + methodName.substring(pos + 1);
    }

    public static Method getAttributeGetter(Class<?> classType, String attribute) {
        String methodName = getMethodName(attribute, "get");
        Method method = getGetter(classType, methodName);
        if (method == null) {
            methodName = getMethodName(attribute, "is");
            method = getGetter(classType, methodName);
        }
        return method;
    }

    public static Method getAttributeGetter(Class<?> classType, String attribute, Class<?> attrType) {
        String methodName = getMethodName(attribute, "get");
        Method method = getGetter(classType, methodName, attrType);
        if (method == null && attrType.isInstance(Boolean.class)) {
            methodName = getMethodName(attribute, "is");
            method = getGetter(classType, methodName, attrType);
        }
        return method;
    }
    
    public static Method getGetter(Class<?> classType, String methodName, Class<?> attrType) {
        try {
            Method method = classType.getMethod(methodName, new Class<?>[] {});
            if (attrType.isAssignableFrom(method.getReturnType())) {
                return method;
            }
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    public static Method getGetter(Class<?> classType, String methodName) {
        try {
            return classType.getMethod(methodName, new Class<?>[] {});
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    public static Method getAttributeSetter(Class<?> classType, String attribute, Class<?> attrType) {
        String methodName = getMethodName(attribute, "set");
        return getSetter(classType, methodName, attrType);
    }
    
//    public static Method getSetter(Class<?> classType, String methodName, Class<?> attrType) {
//        Class<?> clazz = classType;
//        while (clazz != Object.class) {
//            try {
//                return clazz.getMethod(methodName, new Class<?>[] {attrType});
//            }
//            catch (@SuppressWarnings("unused") NoSuchMethodException e) {
//                clazz = clazz.getSuperclass();
//            }
//        }
//        return null;
//    }
    
    public static Method getSetter(Class<?> classType, String methodName, Class<?> attrType) {
        // First check if there is a setter for the exact type of the attribute
        try {
            return classType.getMethod(methodName, new Class<?>[] {attrType});
        } catch (NoSuchMethodException e) {
            // No action required.
        }
        // If not, iterate over all methods to see if we can find a setter for an enclosing type
        for (Method method : classType.getMethods()) {
            if (method.getName().equals(methodName) && method.getReturnType() == Void.TYPE && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(getNonPrimitiveClass(attrType))) {
                return method;
            }
        }
        return null;
    }

    public static String getMethodName(String property, String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(property.substring(0, 1).toUpperCase())
                .append(property.substring(1));
        return sb.toString();
    }
    
    public static Class<?> getNonPrimitiveClass(Class<?> clazz) {
        if (!clazz.isPrimitive()) return clazz;
        switch (clazz.getName()) {
        case "boolean":
            return Boolean.class;
        case "byte":
            return Byte.class;
        case "short":
            return Short.class;
        case "int":
            return Integer.class;
        case "long":
            return Long.class;
        case "char":
            return Character.class;
        case "float":
            return Float.class;
        case "double":
            return Double.class;
        }
        return null;
    }
}
