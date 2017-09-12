package org.openstreetmap.josm.plugins.ods.properties;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.properties.transform.TypeTransform;
import org.openstreetmap.josm.tools.I18n;

public class EntityMapping<T1, T2> {
    private OdsEntityType<T1> sourceType;
    private OdsEntityType<T2> targetType;
    private EntityFactory<T2> factory;
    private final List<AttributeMapping> attributeMappings = new LinkedList<>();
    private final List<ConstantMapping<?>> constantMappings = new LinkedList<>();
    private final List<ChildMapping<T1, ?>> childMappings = new LinkedList<>();
    private final List<String> issues = new LinkedList<>();

    public EntityMapping() {
        super();
    }

    public void setSourceType(OdsEntityType<T1> sourceType) {
        this.sourceType = sourceType;
    }

    public void setTargetType(OdsEntityType<T2> targetType) {
        this.targetType = targetType;
    }

    public OdsEntityType<T1> getSourceType() {
        return sourceType;
    }

    public OdsEntityType<T2> getTargetType() {
        return targetType;
    }


    public List<AttributeMapping> getAttributeMappings() {
        return attributeMappings;
    }


    public List<ConstantMapping<?>> getConstantMappings() {
        return constantMappings;
    }


    public List<ChildMapping<T1, ?>> getChildMappings() {
        return childMappings;
    }


    public void setFactory(EntityFactory<T2> factory) {
        this.factory = factory;
    }

    public void setTargetClass(Class<? extends T2> targetClass) {
        factory = new SimpleEntityFactory<>(targetClass);
    }

    public void bindAttribute(String attribute) {
        attributeMappings.add(new AttributeMapping(attribute, attribute));
    }

    public void bindAttribute(String sourceAttribute, String targetAttribute) {
        attributeMappings.add(new AttributeMapping(sourceAttribute, targetAttribute));
    }

    public void bindAttribute(String sourceAttribute, String targetAttribute, boolean cast) {
        attributeMappings.add(new AttributeMapping(sourceAttribute, targetAttribute, cast));
    }

    public void addAttributeMapping(String sourceAttribute, String targetAttribute, TypeTransform<?, ?> transform) {
        attributeMappings.add(new AttributeMapping(sourceAttribute, targetAttribute, transform));
    }

    public <A1> void addConstant(String targetAttribute, A1 value) {
        constantMappings.add(new ConstantMapping<>(targetAttribute, value));
    }

    public <T> void addChildMapper(
            SimpleEntityMapper<T1, T> childMapper, String targetAttribute) {
        ChildMapping<T1, T> childMapping = new ChildMapping<>(childMapper, targetAttribute);
        childMappings.add(childMapping);
    }

    public boolean isValid() {
        if (sourceType == null) {
            addIssue("Missing source type for entity mappping");
        }
        if (targetType == null) {
            addIssue("Missing target type for entity mapping");
        }
        if (!issues.isEmpty()) return false;
        verifyAttributeMappings();
        return issues.isEmpty();
    }

    private void verifyAttributeMappings() {
        for (AttributeMapping attributeMapping : attributeMappings) {
            if (attributeMapping.getTransform() == null) {
                verifySimpleAttributeMapping(attributeMapping);
            }
            else {
                verifyTransformingAttributeMapping(attributeMapping);
            }
        }
        for (ConstantMapping<?> constantMapping : constantMappings) {
            verifyConstantAttributeMapping(constantMapping);
        }
        for (ChildMapping<T1, ?> childMapping : childMappings) {
            verifyChildMapping(childMapping);
        }
    }

    private <A1> void verifySimpleAttributeMapping(AttributeMapping attributeMapping) {
        String sourceAttr = attributeMapping.getSourceAttribute();
        String targetAttr = attributeMapping.getTargetAttribute();

        @SuppressWarnings("unchecked")
        PropertyHandler<T1, A1> sourceAttributeHandler =
        (PropertyHandler<T1, A1>) sourceType.createAttributeHandler(sourceAttr);
        if (sourceAttributeHandler == null) {
            addIssue("The attribute ''{0}'' doesn''t exist in source type ''{1}''", sourceAttr, sourceType);
        }
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A1> targetAttributeHandler =
        (PropertyHandler<T2, A1>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue("The attribute ''{0}'' doesn''t exist in target type ''{1}''", targetAttr, targetType);
        }
        if (sourceAttributeHandler == null || targetAttributeHandler == null) {
            return;
        }
        Class<?> sourceAttrType = sourceAttributeHandler.getType();
        Class<?> targetAttrType = targetAttributeHandler.getType();
        if (!targetAttrType.isAssignableFrom(sourceAttrType) && !attributeMapping.getCast()) {
            addIssue("Target attribute ''{0}({1})' is not assignable from source attribute ''{2}({3})'." +
                    " And no transform function was specified",
                    targetAttr, targetAttrType, sourceAttr, sourceAttrType);
            return;
        }
        return;
    }

    private <A1> void verifyConstantAttributeMapping(ConstantMapping<A1> constantMapping) {
        String targetAttr = constantMapping.getTargetAttribute();
        A1 value = constantMapping.getValue();

        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A1> targetAttributeHandler =
        (PropertyHandler<T2, A1>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue("The attribute ''{0}'' doesn''t exist in target type ''{1}''", targetAttr, targetType);
            return;
        }
        Class<?> targetAttrType = targetAttributeHandler.getType();
        if (!targetAttrType.isAssignableFrom(value.getClass())) {
            addIssue("The type of the constant ({0}) is not compatible with the target attribute ''{1}'' ({2}).",
                    value.getClass(), targetAttr, targetAttrType);
            return;
        }
        return;
    }

    private <A1, A2> void verifyTransformingAttributeMapping(AttributeMapping attributeMapping) {
        String sourceAttr = attributeMapping.getSourceAttribute();
        String targetAttr = attributeMapping.getTargetAttribute();
        @SuppressWarnings("unchecked")
        TypeTransform<A1, A2> transform = (TypeTransform<A1, A2>) attributeMapping.getTransform();

        @SuppressWarnings("unchecked")
        PropertyHandler<T1, A1> sourceAttributeHandler =
        (PropertyHandler<T1, A1>) sourceType.createAttributeHandler(sourceAttr);
        if (sourceAttributeHandler == null) {
            addIssue("The attribute ''{0}'' doesn''t exist in source type ''{1}''", sourceAttr, sourceType.toString());
        }
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A2> targetAttributeHandler =
        (PropertyHandler<T2, A2>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue("The attribute ''{0}'' doesn''t exist in the target type ''{1}''", targetAttr, targetType);
        }
        if (sourceAttributeHandler == null || targetAttributeHandler == null) {
            return;
        }
        Class<?> sourceAttrType = sourceAttributeHandler.getType();
        Class<?> targetAttrType = targetAttributeHandler.getType();

        if (!transform.getSourceType().isAssignableFrom(sourceAttrType)) {
            addIssue(I18n.tr("The input type of the transform function ({0}) is not applicable to the source attribute ''{1} ({2})'.",
                    transform.getSourceType(), sourceAttr, sourceAttrType));
        }
        if (!targetAttrType.isAssignableFrom(transform.getTargetType())) {
            addIssue(I18n.tr("Target attribute ''{0} ({1})' is not assignable from the result type of the transform function ({2}).",
                    targetAttr, targetAttrType.getName(), transform.getTargetType()));
        }
        return;
    }

    private <A1> void verifyChildMapping(ChildMapping<T1, ?> childMapping) {
        String targetAttr = childMapping.getTargetAttribute();
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A1> targetAttributeHandler =
        (PropertyHandler<T2, A1>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue("The attribute ''{0}'' doesn''t exist in target type ''{1}''", targetAttr, targetType);
        }
    }

    public void addIssue(String text, Object... objects) {
        this.issues.add(I18n.tr(text, objects));
    }

    protected class AttributeMapping {
        String sourceAttribute;
        String targetAttribute;
        boolean cast = false;
        TypeTransform<?, ?> transform;

        public AttributeMapping(String sourceAttribute, String targetAttribute) {
            this(sourceAttribute, targetAttribute, null);
        }

        public AttributeMapping(String sourceAttribute,
                String targetAttribute, boolean cast) {
            this(sourceAttribute, targetAttribute);
            this.cast = cast;
        }

        public AttributeMapping(String sourceAttribute, String targetAttribute,
                TypeTransform<?, ?> transform) {
            super();
            this.sourceAttribute = sourceAttribute;
            this.targetAttribute = targetAttribute;
            this.transform = transform;
        }

        public String getSourceAttribute() {
            return sourceAttribute;
        }

        public String getTargetAttribute() {
            return targetAttribute;
        }

        public boolean getCast() {
            return cast;
        }

        public TypeTransform<?, ?> getTransform() {
            return transform;
        }
    }

    protected static class ConstantMapping<T5> {
        private final String targetAttribute;
        private final T5 value;

        public ConstantMapping(String targetAttribute, T5 value) {
            super();
            this.targetAttribute = targetAttribute;
            this.value = value;
        }

        public String getTargetAttribute() {
            return targetAttribute;
        }

        public T5 getValue() {
            return value;
        }
    }

    protected static class ChildMapping<T3, T4> {
        private final SimpleEntityMapper<T3, T4> childMapper;
        private final String targetAttribute;
        //        TypeTransform<?, ?> transform;

        public ChildMapping(SimpleEntityMapper<T3, T4> childMapper, String targetAttribute) {
            super();
            this.childMapper = childMapper;
            this.targetAttribute = targetAttribute;
        }

        public SimpleEntityMapper<T3, T4> getChildMapper() {
            return childMapper;
        }

        public String getTargetAttribute() {
            return targetAttribute;
        }
    }
}
