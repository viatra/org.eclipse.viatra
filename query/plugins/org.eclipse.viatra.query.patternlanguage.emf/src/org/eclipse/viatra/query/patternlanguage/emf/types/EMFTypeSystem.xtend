/** 
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.query.patternlanguage.emf.types

import com.google.common.base.Strings
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import com.google.inject.Inject
import java.util.HashSet
import java.util.Set
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrer
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody
import org.eclipse.viatra.query.patternlanguage.emf.vql.RelationType
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable
import org.eclipse.viatra.query.patternlanguage.emf.types.AbstractTypeSystem
import org.eclipse.viatra.query.runtime.emf.EMFQueryMetaContext
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.util.Primitives
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.diagnostics.Severity

import static com.google.common.base.Preconditions.checkArgument
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaType
import org.eclipse.viatra.query.patternlanguage.emf.types.BottomTypeKey
import org.eclipse.viatra.query.patternlanguage.emf.util.IClassLoaderProvider
import java.util.Optional
import org.eclipse.viatra.query.runtime.emf.types.EClassUnscopedTransitiveInstancesKey
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper

/** 
 * @author Zoltan Ujhelyi
 */
public class EMFTypeSystem extends AbstractTypeSystem {
    static final String NON_EMF_TYPE_ENCOUNTERED = "EMF Type System only supports EMF Types but %s found."

    /**
     * This function can be used to extract EClassifier instances from IInputKey instances.
     * If the IInputKey instance does not represent an EClassifier, null is returned 
     * @since 2.0
     */
    public static def EClassifier extractClassifierFromInputKey(IInputKey key) {
        switch (key) {
            EClassTransitiveInstancesKey:
                key.emfKey
            EDataTypeInSlotsKey:
                key.emfKey
            default:
                null
        }
    }
    
    /**
     * This function can be used to extract EClassifier instances from Type declaration.
     * If the IInputKey instance does not represent an EClassifier, null is returned 
     * @since 2.0
     */
    public static def EClassifier extractClassifierFromType(Type typeDeclaration) {
        switch (typeDeclaration) {
            ClassType:
                typeDeclaration.classname
            ReferenceType:
                typeDeclaration.refname?.EType
            default:
                null
        }
    }

    @Inject IMetamodelProvider metamodelProvider
    @Inject IErrorFeedback errorFeedback
    @Inject Primitives primitives
    @Inject TypeReferences typeReferences
    @Inject IClassLoaderProvider classLoaderProvider 

    @Inject new(Logger logger) {
        super(EMFQueryMetaContext.DEFAULT)
    }

    override IInputKey extractTypeDescriptor(Type type) {
        checkArgument(type instanceof ClassType || type instanceof ReferenceType || type instanceof JavaType,
             NON_EMF_TYPE_ENCOUNTERED, type.getClass())
        if (type instanceof ClassType) {
            val EClassifier classifier = type.getClassname()
            return classifierToInputKey(classifier)
        } else if (type instanceof ReferenceType) {
            return type.refname?.EType.classifierToInputKey
        } else if (type instanceof JavaType) {
            return new JavaTransitiveInstancesKey(type.classRef.identifier)
        }
        // Never executed
        throw new UnsupportedOperationException()
    }

    def IInputKey classifierToInputKey(EClassifier classifier) {
        switch (classifier) {
            case classifier.eIsProxy : BottomTypeKey.INSTANCE
            EClass: new EClassTransitiveInstancesKey(classifier)
            EDataType: new EDataTypeInSlotsKey(classifier)
            default: BottomTypeKey.INSTANCE
        }
    }

    /**
     * Returns the EClassifier stored in the IInputKey. If no EClassifier is represented in the key, 
     * the optional will be empty.
     * @since 2.0
     */
    def Optional<EClassifier> inputKeyToClassifier(IInputKey key) {
        switch(key) {
            EClassTransitiveInstancesKey: Optional.ofNullable(key.emfKey)
            EClassUnscopedTransitiveInstancesKey: Optional.ofNullable(key.emfKey)
            EDataTypeInSlotsKey: Optional.ofNullable(key.emfKey)
            default: Optional.empty
        }
    }

    override IInputKey extractColumnDescriptor(RelationType type, int columnIndex) {
        checkArgument(type instanceof ReferenceType, NON_EMF_TYPE_ENCOUNTERED, type.getClass())
        if (type instanceof ReferenceType) {
            val EStructuralFeature feature = type.getRefname()
            return extractColumnDescriptor(feature, columnIndex)
        }
        // Never executed
        throw new UnsupportedOperationException()
    }

    def private IInputKey extractColumnDescriptor(EStructuralFeature feature, int columnIndex) {
        if (feature === null || feature.eIsProxy) {
            return BottomTypeKey.INSTANCE
        }
        if (0 === columnIndex) {
            return new EClassTransitiveInstancesKey(feature.getEContainingClass())
        } else {
            if (feature instanceof EReference) {
                return new EClassTransitiveInstancesKey(feature.getEReferenceType())
            } else {
                return new EDataTypeInSlotsKey((feature as EAttribute).getEAttributeType())
            }
        }
    }

    override boolean isConformant(IInputKey expectedType, IInputKey actualType) {
        if (expectedType instanceof BottomTypeKey) {
            return false;
        } else if (actualType instanceof BottomTypeKey) {
            return false;
        } else if (expectedType instanceof EClassTransitiveInstancesKey) {
            if (actualType instanceof EClassTransitiveInstancesKey)
                return isConform(expectedType.getEmfKey(), actualType.getEmfKey())
        } else if (expectedType instanceof EDataTypeInSlotsKey) {
            if (actualType instanceof EDataTypeInSlotsKey) {
                return isConform(expectedType.getEmfKey(), actualType.getEmfKey())
            } else if (actualType instanceof JavaTransitiveInstancesKey) {
                val expectedJavaClass = expectedType.javaClass
                val actualJavaClass = actualType.javaClass
                return expectedJavaClass !== null && actualJavaClass !== null
                     && expectedJavaClass.isAssignableFrom(actualJavaClass)
            }
        } else if (expectedType instanceof JavaTransitiveInstancesKey) {
            if (actualType instanceof JavaTransitiveInstancesKey) {
                val expectedJavaClass = expectedType.instanceClass
                val actualJavaClass = actualType.instanceClass
                return expectedJavaClass !== null && actualJavaClass !== null
                    && expectedJavaClass.isAssignableFrom(actualJavaClass)
            } else if (actualType instanceof EDataTypeInSlotsKey) {
                val expectedJavaClass = expectedType.javaClass
                val actualJavaClass = actualType.javaClass
                return expectedJavaClass !== null && actualJavaClass !== null
                    && expectedJavaClass.isAssignableFrom(actualJavaClass)
            }
        }
        // This means inconsistent type settings that is reported elsewhere
        return false
    }

    /**
     * @since 1.5
     */
    def Class<?> getJavaClass(EDataTypeInSlotsKey key) {
        var dataTypeClass = key.getEmfKey().instanceClass
        if (dataTypeClass === null) {
            dataTypeClass = classLoaderProvider.getClassLoader(key.emfKey).loadClass(key.emfKey.instanceClassName)
        }
        if(dataTypeClass.isPrimitive) dataTypeClass = dataTypeClass?.wrapperClassForType
        return dataTypeClass
    }
    
    /**
     * @since 2.1
     */
    def String getJavaClassName(EDataTypeInSlotsKey key) {
        key.getEmfKey().instanceClassName.wrapperClassNameForTypeName
    }

    /**
     * @since 1.5
     */
    def Class<?> getJavaClass(JavaTransitiveInstancesKey javaKey) {
        var javaTypeClass = javaKey.instanceClass
        if(javaTypeClass.isPrimitive) javaTypeClass = javaTypeClass.wrapperClassForType
        return javaTypeClass
    }

    /**
     * @since 2.0
     */
    def boolean isConformant(ClassType expectedType, ClassType actualType) {
        val IInputKey expectedClassifier = extractTypeDescriptor(expectedType)
        val IInputKey actualClassifier = extractTypeDescriptor(actualType)
        return isConformant(expectedClassifier, actualClassifier)
    }

    def private boolean isConform(EClassifier expectedClassifier, EClassifier actualClassifier) {
        if (actualClassifier instanceof EClass) {
            return EcoreUtil2.getCompatibleTypesOf(actualClassifier).contains(expectedClassifier)
        } else {
            // TODO make sure this is correct wrt bug 398911
            return expectedClassifier.equals(actualClassifier)
        }
    }

    override boolean isConformToRelationColumn(IInputKey relationType, int columnIndex, IInputKey columnType) {
        if (relationType instanceof EStructuralFeatureInstancesKey) {
            val EStructuralFeature feature = relationType.getEmfKey()
            return isConformant(extractColumnDescriptor(feature, columnIndex), columnType)
        } else {
            return false
        }
    }

    /**
     * @since 2.0
     */
    def boolean isConformToRelationSource(ReferenceType relationType, ClassType sourceType) {
        val EStructuralFeature featureType = relationType.getRefname()
        val EClassifier classifier = sourceType.getClassname()
        val EClass sourceClass = featureType.getEContainingClass()
        return isConform(sourceClass, classifier)
    }

    /**
     * @since 2.0
     */
    def boolean isConformToRelationTarget(ReferenceType relationType, ClassType targetType) {
        val EStructuralFeature featureType = relationType.getRefname()
        val EClassifier classifier = targetType.getClassname()
        val EClassifier targetClassifier = featureType.getEType()
        return isConform(targetClassifier, classifier)
    }

    override JvmTypeReference toJvmTypeReference(IInputKey type, EObject context) {
        if (type instanceof EClassTransitiveInstancesKey) {
            return getJvmType(type.getEmfKey(), context)
        } else if (type instanceof EDataTypeInSlotsKey) {
            return getJvmType(type.getEmfKey(), context)
        } else if (type instanceof JavaTransitiveInstancesKey) {
            val wrappedKey = type.getWrappedKey()
            if (!wrappedKey.nullOrEmpty) {
                return typeReferences.getTypeForName(wrappedKey, context)
            }
        }
        return typeReferences.getTypeForName(Object, context)
    }

    private def JvmTypeReference getJvmType(EClassifier classifier, EObject context) {
        if (classifier !== null) {
            val className = metamodelProvider.getQualifiedClassName(classifier, context);
            if (!Strings.isNullOrEmpty(className)) {
                return getTypeReferenceForTypeName(className, context);
            }
        }
        // Return Object or EObject if no classifier can be found 
        val clazz = if (classifier instanceof EClass) typeof(EObject) else typeof(Object)
        typeReferences.getTypeForName(clazz, context);
    }

    private def JvmTypeReference getTypeReferenceForTypeName(String typeName, EObject context) {
        val typeRef = typeReferences.getTypeForName(typeName, context);
        val typeReference = primitives.asWrapperTypeIfPrimitive(typeRef);

        if (typeReference === null) {
            var errorContext = context;
            var contextName = context.toString();
            if (context instanceof Variable && (context as Variable).eContainer() instanceof PatternBody) {
                val variable = context as Variable
                val reference = PatternLanguageHelper.getReferences(variable).findAny
                if (reference.isPresent) {
                    contextName = variable.getName();
                    errorContext = reference.get;
                }
            }
            errorFeedback.reportError(errorContext,
                String.format(
                    "Cannot resolve corresponding Java type for variable %s. Are the required bundle dependencies set?",
                    contextName), EMFPatternLanguageJvmModelInferrer.INVALID_TYPEREF_CODE, Severity.WARNING,
                    IErrorFeedback.JVMINFERENCE_ERROR_TYPE);
            }
            return typeReference;
        }

        /**
         * @since 1.3
         */
        override minimizeTypeInformation(Set<IInputKey> types, boolean mergeWithSupertypes) {
            if(types.size == 1) return types
            val eJavaTypes = minimizeJavaTypeList(types.filter(JavaTransitiveInstancesKey).filterNull)
            val eDataTypes = minimizeEDataTypeList(types.filter(EDataTypeInSlotsKey).filterNull).filter[
                val javaType = new JavaTransitiveInstancesKey(it.emfKey.instanceClass.wrapperClassForType)
                !eJavaTypes.exists[it == javaType]
            ]
            val eClassTypes = minimizeEClassKeyList(types.filter(EClassTransitiveInstancesKey).filterNull,
                mergeWithSupertypes)
            return Iterables.concat(eClassTypes, Iterables.<IInputKey>concat(eDataTypes, eJavaTypes)).toSet
        }

        def private Iterable<EClassTransitiveInstancesKey> minimizeEClassKeyList(Iterable<EClassTransitiveInstancesKey> types,
            boolean mergeWithSupertypes) {
            val emfTypes = <EClass>newHashSet(types.map[emfKey])
            return emfTypes.minimizeEClassList(mergeWithSupertypes).map[new EClassTransitiveInstancesKey(it)]
        }

        def private Iterable<EClass> minimizeEClassList(Iterable<EClass> types, boolean mergeWithSupertypes) {
            val nonTopTypes = types.filter["EObject" != name || EPackage.nsURI != EcorePackage.eNS_URI]

            val emfTypes = <EClass>newHashSet(nonTopTypes)
            nonTopTypes.forEach [ key |
                emfTypes.removeAll(key.EAllSuperTypes)
            ]
            if (mergeWithSupertypes && emfTypes.size > 1) {
                val compatibleTypes = emfTypes.map [ key |
                    emfTypes.map [ current |
                        val type = EcoreUtil2.getCompatibleType(key, current, null)
                        if(type instanceof EClass) type else current
                    ]
                ].flatten
                
                val filteredTypes = compatibleTypes.filter[!EcoreUtil2.getAllSuperTypes(it).exists[supertype|compatibleTypes.exists[it == supertype]]].toSet
                if (filteredTypes.size > 1) {
                    // Short circuit: all EClasses extend EObject
                    return newHashSet(EcorePackage.Literals.EOBJECT)
                } else {
                    return filteredTypes
                }
            }
            return emfTypes
        }

        def private Iterable<EDataTypeInSlotsKey> minimizeEDataTypeList(Iterable<EDataTypeInSlotsKey> types) {
            val emfTypes = <EDataType>newHashSet(types.map[emfKey].filter[!instanceClassName.nullOrEmpty])
            val result = emfTypes.clone as HashSet<EDataType>
            val it = emfTypes.iterator
            while (it.hasNext) {
                val dataType = it.next
                result.removeAll(emfTypes.filter [
                    it.instanceClassName == dataType.instanceClassName
                ].drop(1))
            }

            return result.map[new EDataTypeInSlotsKey(it)]
        }

        def private Iterable<JavaTransitiveInstancesKey> minimizeJavaTypeList(Iterable<JavaTransitiveInstancesKey> types) {
            val nonTopTypes = types.filter[instanceClass !== null && instanceClass != Object].map[instanceClass].
                filterNull
            val javaTypes = <Class<?>>newHashSet(nonTopTypes)
            nonTopTypes.forEach [ key |
                javaTypes.removeAll(javaTypes.filter [
                    it != key && key.isAssignableFrom(it)
                ])
            ]
            return javaTypes.map[new JavaTransitiveInstancesKey(it)]
        }

        /**
         * @since 1.3
         */
        override Set<IInputKey> addTypeInformation(Set<IInputKey> types, IInputKey newType) {
            return minimizeTypeInformation(ImmutableSet.<IInputKey>builder().addAll(types).add(newType).build(), false)
        }

        /**
         * @since 1.3
         */
        override Set<IInputKey> addTypeInformation(Set<IInputKey> types, Set<IInputKey> newTypes) {
            return minimizeTypeInformation(ImmutableSet.<IInputKey>builder().addAll(types).addAll(newTypes).build(),
                false)
        }

        /**
         * @since 1.3
         * @return True if the given classifiers has a common subtype in the selected EPackages.
         */
        def boolean hasCommonSubtype(Set<IInputKey> typeKeys, Iterable<EPackage> ePackages) {
            val knownTypes = ePackages.map[allEClassifiers].flatten
            if (typeKeys.forall[it instanceof EClassTransitiveInstancesKey]) {
                val classifiers = typeKeys.map[(it as EClassTransitiveInstancesKey).emfKey].toSet
                knownTypes.exists[it.EAllSuperTypes.containsAll(classifiers)]
            } else {
                return false;
            }
        }

        private static def Set<EClass> getAllEClassifiers(EPackage ePackage) {
            return newHashSet(ePackage.EClassifiers.filter(EClass))
        }

        override typeString(IInputKey type) {
            switch(type) {
                case type === null : "«null»"
                case type instanceof EClassTransitiveInstancesKey && !(type as EClassTransitiveInstancesKey).emfKey.eIsProxy: 
                    '''«(type as EClassTransitiveInstancesKey).emfKey.EPackage.nsURI»::«(type as EClassTransitiveInstancesKey).emfKey.name»''' 
                EClassTransitiveInstancesKey : '''«type.emfKey.toString»''' 
                EDataTypeInSlotsKey : '''«type.emfKey.EPackage.nsURI»::«type.emfKey.name»'''
                default: super.typeString(type)
            }
        }
        
        /**
         * @since 1.3
         */
        override getCompatibleSupertypes(Set<IInputKey> types) {
            if (types.forall[it instanceof EClassTransitiveInstancesKey]) {
                getCompatibleEClasses(types.filter(typeof(EClassTransitiveInstancesKey)))
            } else if (types.forall[it instanceof JavaTransitiveInstancesKey]) {
                // TODO Do we want to have a similar implementation for Java types as well?
                return types
            } else {
                // There is no combined supertype, return original set
                return types
            }
        }
        
        private def Set<IInputKey> getCompatibleEClasses(Iterable<EClassTransitiveInstancesKey> types) {
            val candidates = types.map[EcoreUtil2.getCompatibleTypesOf(it.emfKey)]
            val iterator = candidates.iterator
            if (iterator.hasNext) {
                val Set<EClass> compatibleTypes = newHashSet(iterator.next)
                while(iterator.hasNext) {
                    compatibleTypes.retainAll(iterator.next)
                }
                return compatibleTypes.map[new EClassTransitiveInstancesKey(it) as IInputKey].toSet
            }
            return #{}
        }
        
        override isValidType(Type type) {
            if (type instanceof ClassType) {
                val classifier = type.classname
                return classifier !== null && !classifier.eIsProxy
            } else if (type instanceof ReferenceType) {
                val feature = type.refname
                return feature !== null && !feature.eIsProxy
            }
            super.isValidType(type)
        }
        
    }
    