package org.openstreetmap.josm.plugins.ods.properties;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.properties.transform.TypeTransform;
import org.openstreetmap.josm.tools.I18n;

public class EntityMapperBuilder<T1, T2> {
    private final EntityType<T1> sourceType;
    private final EntityType<T2> targetType;
    private EntityFactory<T2> factory;
    private List<EntityAttributeMapper<T1, T2>> attributeMappers = new LinkedList<>();
    private List<ChildMapper<T1, T2>> childAttributeMappers = new LinkedList<>();
    private List<AttributeMapping> attributeMappings = new LinkedList<>();
    private List<ConstantMapping<?>> constantMappings = new LinkedList<>();
    private List<ChildMapping<T1, ?>> childMappings = new LinkedList<>();
    private List<String> issues = new LinkedList<>();
    
    public EntityMapperBuilder(EntityType<T1> sourceType,
            EntityType<T2> targetType) {
        super();
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public void setFactory(EntityFactory<T2> factory) {
        this.factory = factory;
    }
    
    public void setTargetClass(Class<? extends T2> targetClass) {
        factory = new SimpleEntityFactory<>(targetClass);
    }

    public void addAttributeMapping(String sourceAttribute, String targetAttribute) {
        attributeMappings.add(new AttributeMapping(sourceAttribute, targetAttribute));
    }
    
    public void addAttributeMapping(String sourceAttribute, String targetAttribute, boolean cast) {
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

    public SimpleEntityMapper<T1, T2> build() {
        buildAttributeMappers();
        if (!issues.isEmpty()) {
            throw new RuntimeException(String.join("\n", issues));
        }

        if (factory == null) {
            throw new RuntimeException();
        }
        return new SimpleEntityMapper<>(factory, attributeMappers, childAttributeMappers);
    }
    
    private void buildAttributeMappers() {
        for (AttributeMapping attributeMapping : attributeMappings) {
            EntityAttributeMapper<T1, T2> attrMapper = null;
            if (attributeMapping.getTransform() == null) {
                attrMapper = buildSimpleAttributeMapper(attributeMapping);
            }
            else {
                attrMapper = buildTransformingAttributeMapper(attributeMapping);
            }
            if (attrMapper != null) {
                attributeMappers.add(attrMapper);
            }
        }
        for (ConstantMapping<?> constantMapping : constantMappings) {
            EntityAttributeMapper<T1, T2> attrMapper = buildConstantAttributeMapper(constantMapping);
            if (attrMapper != null) {
                attributeMappers.add(attrMapper);
            }
        }
        for (ChildMapping<T1, ?> childMapping : childMappings) {
            ChildMapper<T1, T2> childMapper = buildChildMapper(childMapping);
            if (childMapper != null) {
                childAttributeMappers.add(childMapper);
            }
        }
    }
    
    private <A1> EntityAttributeMapper<T1, T2> buildSimpleAttributeMapper(AttributeMapping attributeMapping) {
        String sourceAttr = attributeMapping.getSourceAttribute();
        String targetAttr = attributeMapping.getTargetAttribute();
        
        @SuppressWarnings("unchecked")
        PropertyHandler<T1, A1> sourceAttributeHandler = 
                (PropertyHandler<T1, A1>) sourceType.createAttributeHandler(sourceAttr);
        if (sourceAttributeHandler == null) {
            addIssue(I18n.tr("The attribute ''{0}'' doesn't exist in source type ''{1}''", sourceAttr, sourceType));
        }
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A1> targetAttributeHandler = 
                (PropertyHandler<T2, A1>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue(I18n.tr("The attribute ''{0}'' doesn't exist in target type ''{1}''", targetAttr, targetType));
        }
        if (sourceAttributeHandler == null || targetAttributeHandler == null) {
            return null;
        }
        Class<?> sourceAttrType = sourceAttributeHandler.getType();
        Class<?> targetAttrType = targetAttributeHandler.getType();
        if (!targetAttrType.isAssignableFrom(sourceAttrType) && !attributeMapping.getCast()) {
            addIssue(I18n.tr("Target attribute ''{0}({1})' is not assignable from source attribute ''{2}({3})'." +
              " And no transform function was specified",
            targetAttr, targetAttrType, sourceAttr, sourceAttrType));
            return null;
        }
        return new SimpleEntityAttributeMapper<>(
            sourceAttributeHandler, targetAttributeHandler);
    }
    
    private <A1> EntityAttributeMapper<T1, T2> buildConstantAttributeMapper(ConstantMapping<A1> constantMapping) {
        String targetAttr = constantMapping.getTargetAttribute();
        A1 value = constantMapping.getValue();
        
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A1> targetAttributeHandler = 
                (PropertyHandler<T2, A1>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue(I18n.tr("The attribute ''{0}'' doesn't exist in target type ''{1}''", targetAttr, targetType));
        }
        if (targetAttributeHandler == null) {
            return null;
        }
        Class<?> targetAttrType = targetAttributeHandler.getType();
        if (!targetAttrType.isAssignableFrom(value.getClass())) {
            addIssue(I18n.tr("The type of the constant ({0}) is not compatible with the target attribute ''{1}'' ({2}).",
            value.getClass(), targetAttr, targetAttrType));
            return null;
        }
        return new ConstantAttributeMapper<>(
            targetAttributeHandler, value);
    }
    

    private <A1, A2> EntityAttributeMapper<T1, T2> buildTransformingAttributeMapper(AttributeMapping attributeMapping) {

        String sourceAttr = attributeMapping.getSourceAttribute();
        String targetAttr = attributeMapping.getTargetAttribute();
        @SuppressWarnings("unchecked")
        TypeTransform<A1, A2> transform = (TypeTransform<A1, A2>) attributeMapping.getTransform();
        
        @SuppressWarnings("unchecked")
        PropertyHandler<T1, A1> sourceAttributeHandler = 
                (PropertyHandler<T1, A1>) sourceType.createAttributeHandler(sourceAttr);
        if (sourceAttributeHandler == null) {
            addIssue(I18n.tr("The attribute ''{0}'' doesn't exist in source type ''{1}''", sourceAttr, sourceType));
        }
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A2> targetAttributeHandler = 
                (PropertyHandler<T2, A2>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue(I18n.tr("The attribute ''{0}'' doesn't exist the target type ''{1}''", targetAttr, targetType));
        }
        if (sourceAttributeHandler == null || targetAttributeHandler == null) {
            return null;
        }
        Class<?> sourceAttrType = sourceAttributeHandler.getType();
        Class<?> targetAttrType = targetAttributeHandler.getType();

        boolean hasIssues = false;
        if (!transform.getSourceType().isAssignableFrom(sourceAttrType)) {
            addIssue(I18n.tr("The input type of the transform function ({0}) is not applicable to the source attribute ''{1} ({2})'.",
                transform.getSourceType(), sourceAttr, sourceAttrType));
            hasIssues = true;
        }
        if (!targetAttrType.isAssignableFrom(transform.getTargetType())) {
            addIssue(I18n.tr("Target attribute ''{0} ({1})' is not assignable from the result type of the transform function ({2}).",
                targetAttr, targetAttrType.getName(), transform.getTargetType()));
            hasIssues = true;
        }
        if (hasIssues) {
            return null;
        }
        return new TransformingEntityAttributeMapper<>(
                sourceAttributeHandler, targetAttributeHandler, transform);
    }
    
    private <A1> ChildMapper<T1, T2> buildChildMapper(ChildMapping<T1, ?> childMapping) {
        EntityMapper<T1, ?> wrappedMapper = childMapping.getChildMapper();
        String targetAttr = childMapping.getTargetAttribute();
        @SuppressWarnings("unchecked")
        PropertyHandler<T2, A1> targetAttributeHandler = 
                (PropertyHandler<T2, A1>) targetType.createAttributeHandler(targetAttr);
        if (targetAttributeHandler == null) {
            addIssue(I18n.tr("The attribute ''{0}'' doesn't exist in target type ''{1}''", targetAttr, targetType));
        }
        @SuppressWarnings("unchecked")
        ChildMapper<T1, T2> childMapper = new ChildMapperImpl<>(
                (EntityMapper<T1, A1>) wrappedMapper, targetAttributeHandler);
        return childMapper;
    }
    
    public void addIssue(String issue) {
        this.issues.add(issue);
    }
    
    private class AttributeMapping {
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
    
    private static class ConstantMapping<T5> {
        private String targetAttribute;
        private T5 value;
        
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
    
    public static class ChildMapping<T3, T4> {
        private SimpleEntityMapper<T3, T4> childMapper;
        private String targetAttribute;
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
