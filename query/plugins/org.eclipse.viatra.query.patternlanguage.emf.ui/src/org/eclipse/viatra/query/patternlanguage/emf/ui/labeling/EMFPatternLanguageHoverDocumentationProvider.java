/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.labeling;

import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.PatternAnnotationProvider;
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverDocumentationProvider;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageHoverDocumentationProvider extends XbaseHoverDocumentationProvider {

    @Inject
    private IVQGenmodelProvider genmodelProvider;
    @Inject
    private PatternAnnotationProvider annotationProvider;
    @Inject
    private ITypeSystem typeSystem;
    @Inject
    private ITypeInferrer typeInferrer;
    @Inject
    private EMFPatternLanguageJvmModelInferrerUtil inferrerUtil;

    @Override
    public String computeDocumentation(EObject object) {
        if (object instanceof Annotation) {
            String description = annotationProvider.getDescription((Annotation) object);
            if (annotationProvider.isDeprecated((Annotation) object)) {
                return "<b>@deprecated</b></p></p>" + description;
            } else {
                return description;
            }
        } else if (object instanceof AnnotationParameter) {
            String description = annotationProvider.getDescription((AnnotationParameter) object);
            if (annotationProvider.isDeprecated((AnnotationParameter) object)) {
                return "<b>@deprecated</b></p></p>" + description;
            } else {
                return description;
            }
        } else if (object instanceof PackageImport) {
            PackageImport packageImport = (PackageImport) object;
            GenPackage genPackage = genmodelProvider.findGenPackage(packageImport, packageImport.getEPackage());
            if (genPackage != null) {
                return String.format("<b>Genmodel found</b>: %s<br/><b>Package uri</b>: %s", genPackage.eResource()
                        .getURI().toString(), genPackage.getEcorePackage().eResource().getURI().toString());
            }
        } else if (object instanceof Variable) {
            Variable variable = (Variable) object;
            return calculateVariableHover(variable);
        } else if (object instanceof VariableReference) {
            VariableReference reference = (VariableReference) object;
            return calculateVariableHover(reference.getVariable());
        } else if (object instanceof Pattern) {
            Pattern pattern = (Pattern) object;
            StringBuilder sb = new StringBuilder();
            sb.append(inferrerUtil.getPatternComment(pattern));
            sb.append("<p><strong>Parameters:</strong></p>");
            sb.append("<ul>");
            for (Variable variable : pattern.getParameters()) {
                sb.append("<li>");
                sb.append("<strong>Parameter</strong> ");
                sb.append(variable.getName());
                
                final IInputKey type = typeInferrer.getType(variable);
                if (type != null) {
                    sb.append(": ");
                    sb.append("<i>");
                    sb.append(typeSystem.typeString(type));
                    sb.append("</i>");
                }
                sb.append("</li>");
            }
            sb.append("</ul>");
            return sb.toString();
        } else if (object instanceof ClassType) {
            ClassType type = (ClassType) object;
            StringBuilder sb = new StringBuilder();
            sb.append(super.computeDocumentation(type));
            EClassifier classifier = type.getClassname();
            appendEClassifierHover(sb, classifier); 
            return sb.toString();
        } else if (object instanceof ReferenceType) {
            ReferenceType type = (ReferenceType) object;
            StringBuilder sb = new StringBuilder();
            sb.append(super.computeDocumentation(type));
            EStructuralFeature feature = type.getRefname();
            appendStructuralFeatureHover(sb, feature); 
            return sb.toString();
        } else if (object instanceof EClassifier) {
            EClassifier classifier = (EClassifier) object;
            StringBuilder sb = new StringBuilder();
            sb.append(super.computeDocumentation(classifier));
            appendEClassifierHover(sb, classifier); 
            return sb.toString();
        } else if (object instanceof EStructuralFeature) {
            EStructuralFeature feature = (EStructuralFeature) object;
            StringBuilder sb = new StringBuilder();
            sb.append(super.computeDocumentation(feature));
            appendStructuralFeatureHover(sb, feature); 
            return sb.toString();
        }
        return super.computeDocumentation(object);
    }
    
    private void appendStructuralFeatureHover(StringBuilder sb, EStructuralFeature feature) {
        sb.append("<p><strong>Defined in:</strong> " + feature.getEContainingClass().getName() + "</p>");
        sb.append("<p><strong>Takes value of:</strong> " + feature.getEType().getName() + "</p>");
        if (feature.isMany())
            sb.append("<p><strong>Multiplicity:</strong> <em>to-many</em></p>");
        else
            sb.append("<p><strong>Multiplicity:</strong> <em>to-one</em></p>");
        sb.append("<p><strong>Derived:</strong> " + feature.isDerived() + "</p>");
        sb.append("<p><strong>Transient:</strong> " + feature.isTransient() + "</p>");
        if (feature instanceof EReference) {
            EReference reference = (EReference) feature;
            sb.append("<p><strong>Containment:</strong> " + reference.isContainment() + "</p>");
            sb.append("<p><strong>Container:</strong> " + reference.isContainer() + "</p>");
            EReference eOpposite = reference.getEOpposite();
            if (eOpposite != null) {
                sb.append("<p><strong>EOpposite:</strong> " + eOpposite.getName() + "</p>");
            }
        }
    }

    private void appendEClassifierHover(StringBuilder sb, EClassifier classifier) {
        if (classifier instanceof EClass) {
            EClass eClass = (EClass) classifier;
            List<EClass> eAllSuperTypes = eClass.getEAllSuperTypes();
            if (eAllSuperTypes.isEmpty()) {
                sb.append("<p><strong>EAllSupertypes:</strong> (none)</p>");
            } else {
                sb.append("<p><strong>EAllSupertypes:</strong></p>");
                sb.append("<ul>");
                for (EClass superType : eAllSuperTypes) {
                    sb.append("<li>");
                    sb.append(superType.getName());
                    sb.append("</li>");
                }
                sb.append("</ul>");
            }
        }
    }

    private String calculateVariableHover(Variable variable) {
        JvmTypeReference type = typeInferrer.getJvmType(variable, variable); 
        IInputKey emfType = typeInferrer.getType(variable); 
        String javaTypeString = type.getQualifiedName();
        String emfTypeString;
        if (emfType instanceof EClassTransitiveInstancesKey) {
            emfTypeString = getTypeString(((EClassTransitiveInstancesKey) emfType).getEmfKey());
        } else if (emfType instanceof EDataTypeInSlotsKey) {
            emfTypeString = getTypeString(((EDataTypeInSlotsKey) emfType).getEmfKey());
        } else {
            emfTypeString = "Not applicable";
        }
        return String.format("<b>EMF Type</b>: %s<br /><b>Java Type</b>: %s", emfTypeString, javaTypeString);
    }

    private String getTypeString(EClassifier emfType) {
        String emfTypeString;
        final String packageUri = emfType.getEPackage() != null ? "(<i>" + emfType.getEPackage().getNsURI() + "</i>)" : "";
        emfTypeString = String.format("%s %s", emfType.getName(), packageUri);
        return emfTypeString;
    }

    @Override
    protected String getDerivedElementInformation(EObject o) {
        return (o instanceof Pattern) ? null : super.getDerivedElementInformation(o);
    }

}
