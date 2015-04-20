/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.uml;

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.incquery.uml.derivedfeatures.Fixed;
import org.eclipse.incquery.uml.derivedfeatures.Generated;
import org.eclipse.incquery.uml.derivedfeatures.Handwritten;
import org.eclipse.incquery.uml.derivedfeatures.UsedOperations;
import org.eclipse.incquery.uml.derivedfeatures.util.AssociationEndTypeQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ClassExtensionQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ClassSuperClassQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ClassifierGeneralQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ConnectableElementEndQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ConnectorKindQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.DeploymentTargetDeployedElementQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.EncapsulatedClassifierOwnedPortQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ExtensionMetaclassQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.NamedElementClientDependencyQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.NamedElementQualifiedNameQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.NamespaceImportedMemberQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.OpaqueExpressionResultQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.PackageNestedPackageQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.PackageOwnedStereotypeQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.PackageOwnedTypeQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.PropertyIsCompositeQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.ProtocolTransitionReferredQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.RedefinableTemplateSignatureInheritedParameterQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.StateIsCompositeQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.StateIsOrthogonalQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.StructuredClassifierPartQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.VertexIncomingQuerySpecification;
import org.eclipse.incquery.uml.derivedfeatures.util.VertexOutgoingQuerySpecification;
import org.eclipse.uml2.uml.UMLPackage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * A helper class to register all query specifications of the UML support
 * project to various internal registries of EMF-IncQuery in Eclipse-less
 * executions. It is not supported to call this registration multiple times.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class IncQueryUMLStandaloneSetup {

	/**
	 * Register the query specifications
	 * @throws IncQueryException if a query specifiation cannot be initialized or registered
	 */
	public static void doSetup() throws IncQueryException {
		for (IQuerySpecification<?> specification : getAllQuerySpecifications()) {
			QuerySpecificationRegistry.registerQuerySpecification(specification);
		}
		for (EStructuralFeature feature : getWellbehavingFeatures()) {
			WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedFeature(feature);
		}
		for (Map.Entry<EStructuralFeature, IQuerySpecification<?>> entry : getSurrogateQueries().entrySet()) {
			SurrogateQueryRegistry.instance().registerSurrogateQueryForFeature(
					new EStructuralFeatureInstancesKey(entry.getKey()),
					entry.getValue().getInternalQueryRepresentation());
		}
	}

	private static Iterable<IQuerySpecification<?>> getAllQuerySpecifications() throws IncQueryException {
		return Iterables.concat(Fixed.instance().getSpecifications(), Generated.instance().getSpecifications(),
				Handwritten.instance().getSpecifications(), UsedOperations.instance().getSpecifications());
	}

	private static Iterable<EStructuralFeature> getWellbehavingFeatures() {
		return ImmutableList.<EStructuralFeature> of(UMLPackage.Literals.ACTION__INPUT,
				UMLPackage.Literals.ACTION__OUTPUT, UMLPackage.Literals.ACTIVITY__GROUP,
				UMLPackage.Literals.ACTIVITY__NODE, UMLPackage.Literals.ACTIVITY_EDGE__IN_GROUP,
				UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_EDGE, UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_NODE,
				UMLPackage.Literals.ACTIVITY_GROUP__IN_ACTIVITY, UMLPackage.Literals.ACTIVITY_GROUP__SUBGROUP,
				UMLPackage.Literals.ACTIVITY_GROUP__SUPER_GROUP, UMLPackage.Literals.ACTIVITY_NODE__ACTIVITY,
				UMLPackage.Literals.ACTIVITY_NODE__IN_GROUP, UMLPackage.Literals.CLASSIFIER__ATTRIBUTE,
				UMLPackage.Literals.CLASSIFIER__FEATURE, UMLPackage.Literals.DIRECTED_RELATIONSHIP__SOURCE,
				UMLPackage.Literals.DIRECTED_RELATIONSHIP__TARGET, UMLPackage.Literals.ELEMENT__OWNED_ELEMENT,
				UMLPackage.Literals.ELEMENT__OWNER, UMLPackage.Literals.FEATURE__FEATURING_CLASSIFIER,
				UMLPackage.Literals.NAMED_ELEMENT__NAMESPACE, UMLPackage.Literals.NAMESPACE__MEMBER,
				UMLPackage.Literals.NAMESPACE__OWNED_MEMBER, UMLPackage.Literals.PACKAGE__NESTING_PACKAGE,
				UMLPackage.Literals.PROPERTY__DEFAULT, UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINED_ELEMENT,
				UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINITION_CONTEXT,
				UMLPackage.Literals.RELATIONSHIP__RELATED_ELEMENT, UMLPackage.Literals.STRUCTURED_CLASSIFIER__ROLE,
				UMLPackage.Literals.TYPE__PACKAGE);

	}

	private static Map<EStructuralFeature, IQuerySpecification<?>> getSurrogateQueries() throws IncQueryException {
		return ImmutableMap.<EStructuralFeature, IQuerySpecification<?>> builder()
				.put(UMLPackage.Literals.ASSOCIATION__END_TYPE, AssociationEndTypeQuerySpecification.instance())
				.put(UMLPackage.Literals.CLASS__EXTENSION, ClassExtensionQuerySpecification.instance())
				.put(UMLPackage.Literals.CLASS__SUPER_CLASS, ClassSuperClassQuerySpecification.instance())
				.put(UMLPackage.Literals.CLASSIFIER__GENERAL, ClassifierGeneralQuerySpecification.instance())
				.put(UMLPackage.Literals.CONNECTABLE_ELEMENT__END, ConnectableElementEndQuerySpecification.instance())
				.put(UMLPackage.Literals.CONNECTOR__KIND, ConnectorKindQuerySpecification.instance())
				.put(UMLPackage.Literals.DEPLOYMENT_TARGET__DEPLOYED_ELEMENT,
						DeploymentTargetDeployedElementQuerySpecification.instance())
				.put(UMLPackage.Literals.ENCAPSULATED_CLASSIFIER__OWNED_PORT,
						EncapsulatedClassifierOwnedPortQuerySpecification.instance())
				.put(UMLPackage.Literals.EXTENSION__METACLASS, ExtensionMetaclassQuerySpecification.instance())
				.put(UMLPackage.Literals.NAMED_ELEMENT__CLIENT_DEPENDENCY,
						NamedElementClientDependencyQuerySpecification.instance())
				.put(UMLPackage.Literals.NAMED_ELEMENT__QUALIFIED_NAME,
						NamedElementQualifiedNameQuerySpecification.instance())
				.put(UMLPackage.Literals.NAMESPACE__IMPORTED_MEMBER,
						NamespaceImportedMemberQuerySpecification.instance())
				.put(UMLPackage.Literals.OPAQUE_EXPRESSION__RESULT, OpaqueExpressionResultQuerySpecification.instance())
				.put(UMLPackage.Literals.PACKAGE__NESTED_PACKAGE, PackageNestedPackageQuerySpecification.instance())
				.put(UMLPackage.Literals.PACKAGE__OWNED_TYPE, PackageOwnedTypeQuerySpecification.instance())
				.put(UMLPackage.Literals.PACKAGE__OWNED_STEREOTYPE, PackageOwnedStereotypeQuerySpecification.instance())
				.put(UMLPackage.Literals.PROPERTY__IS_COMPOSITE, PropertyIsCompositeQuerySpecification.instance())
				.put(UMLPackage.Literals.PROTOCOL_TRANSITION__REFERRED,
						ProtocolTransitionReferredQuerySpecification.instance())
				.put(UMLPackage.Literals.REDEFINABLE_TEMPLATE_SIGNATURE__INHERITED_PARAMETER,
						RedefinableTemplateSignatureInheritedParameterQuerySpecification.instance())
				.put(UMLPackage.Literals.STATE__IS_COMPOSITE, StateIsCompositeQuerySpecification.instance())
				.put(UMLPackage.Literals.STATE__IS_ORTHOGONAL, StateIsOrthogonalQuerySpecification.instance())
				.put(UMLPackage.Literals.STRUCTURED_CLASSIFIER__PART,
						StructuredClassifierPartQuerySpecification.instance())
				.put(UMLPackage.Literals.VERTEX__INCOMING, VertexIncomingQuerySpecification.instance())
				.put(UMLPackage.Literals.VERTEX__OUTGOING, VertexOutgoingQuerySpecification.instance()).build();
	}
}
