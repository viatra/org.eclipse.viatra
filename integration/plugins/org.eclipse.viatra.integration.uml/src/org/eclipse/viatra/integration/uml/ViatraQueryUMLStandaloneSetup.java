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
package org.eclipse.viatra.integration.uml;

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.viatra.integration.uml.derivedfeatures.DerivedFeatures;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActionContextQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActionInputQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActionOutputQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityEdgeInGroupQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityGroupContainedEdgeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityGroupContainedNodeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityGroupInActivityQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityGroupQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityGroupSubgroupQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityGroupSuperGroupQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityNodeActivityQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityNodeInGroupQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ActivityNodeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.AssociationEndTypeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.BehaviorContextQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ClassExtensionQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ClassSuperClassQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ClassifierAttributeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ClassifierFeatureQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ClassifierGeneralQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ConnectableElementEndQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ConnectorKindQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.DeploymentTargetDeployedElementQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.DirectedRelationshipSourceQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.DirectedRelationshipTargetQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ElementOwnedElementQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ElementOwnerQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.EncapsulatedClassifierOwnedPortQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ExtensionMetaclassQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.FeatureFeaturingClassifierQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.MessageMessageKindQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.NamedElementClientDependencyQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.NamedElementNamespaceQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.NamedElementQualifiedNameQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.NamespaceImportedMemberQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.NamespaceMemberQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.NamespaceOwnedMemberQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.OpaqueExpressionResultQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.PackageNestedPackageQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.PackageNestingPackageQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.PackageOwnedStereotypeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.PackageOwnedTypeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.PropertyIsCompositeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.ProtocolTransitionReferredQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.RedefinableElementRedefinedElementQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.RedefinableElementRedefinitionContextQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.RedefinableTemplateSignatureInheritedParameterQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.RelationshipRelatedElementQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.StateIsCompositeQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.StateIsOrthogonalQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.StructuredClassifierPartQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.StructuredClassifierRoleQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.TypePackageQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.VertexIncomingQuerySpecification;
import org.eclipse.viatra.integration.uml.derivedfeatures.util.VertexOutgoingQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.extensibility.SingletonQueryGroupProvider;
import org.eclipse.viatra.query.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.connector.QueryGroupProviderSourceConnector;

import com.google.common.collect.ImmutableMap;

/**
 * A helper class to register all query specifications of the UML support
 * project to various internal registries of VIATRA Query in Eclipse-less
 * executions. It is not supported to call this registration multiple times.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class ViatraQueryUMLStandaloneSetup {

	private static final String CONNECTOR_ID = "org.eclipse.viatra.integration.uml.standalone.connector";

    /**
	 * Register the query specifications
	 * @throws ViatraQueryException if a query specification cannot be initialized or registered
	 */
	public static void doSetup() throws ViatraQueryException {
	    // query specification registry
	    SingletonQueryGroupProvider groupProvider = new SingletonQueryGroupProvider(getQueryGroup());
        QueryGroupProviderSourceConnector sourceConnector = new QueryGroupProviderSourceConnector(CONNECTOR_ID, groupProvider);
        QuerySpecificationRegistry.getInstance().addSource(sourceConnector);
		
        // surrogate queries
        for (Map.Entry<EStructuralFeature, IQuerySpecification<?>> entry : getSurrogateQueries().entrySet()) {
			SurrogateQueryRegistry.instance().registerSurrogateQueryForFeature(
					new EStructuralFeatureInstancesKey(entry.getKey()),
					entry.getValue().getInternalQueryRepresentation());
		}
	}

	private static IQueryGroup getQueryGroup() throws ViatraQueryException {
	    return DerivedFeatures.instance();
	}

	private static Map<EStructuralFeature, IQuerySpecification<?>> getSurrogateQueries() throws ViatraQueryException {
		return ImmutableMap.<EStructuralFeature, IQuerySpecification<?>> builder()
			.put(UMLPackage.Literals.ACTION__CONTEXT, ActionContextQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTION__INPUT, ActionInputQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTION__OUTPUT, ActionOutputQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY__GROUP, ActivityGroupQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY__NODE, ActivityNodeQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_EDGE__IN_GROUP, ActivityEdgeInGroupQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_EDGE, ActivityGroupContainedEdgeQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_NODE, ActivityGroupContainedNodeQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_GROUP__IN_ACTIVITY, ActivityGroupInActivityQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_GROUP__SUBGROUP, ActivityGroupSubgroupQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_GROUP__SUPER_GROUP, ActivityGroupSuperGroupQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_NODE__ACTIVITY, ActivityNodeActivityQuerySpecification.instance())
			.put(UMLPackage.Literals.ACTIVITY_NODE__IN_GROUP, ActivityNodeInGroupQuerySpecification.instance())
			.put(UMLPackage.Literals.ASSOCIATION__END_TYPE, AssociationEndTypeQuerySpecification.instance())
			.put(UMLPackage.Literals.BEHAVIOR__CONTEXT, BehaviorContextQuerySpecification.instance())
			.put(UMLPackage.Literals.CLASS__EXTENSION, ClassExtensionQuerySpecification.instance())
			.put(UMLPackage.Literals.CLASS__SUPER_CLASS, ClassSuperClassQuerySpecification.instance())
			.put(UMLPackage.Literals.CLASSIFIER__ATTRIBUTE, ClassifierAttributeQuerySpecification.instance())
			.put(UMLPackage.Literals.CLASSIFIER__FEATURE, ClassifierFeatureQuerySpecification.instance())
			.put(UMLPackage.Literals.CLASSIFIER__GENERAL, ClassifierGeneralQuerySpecification.instance())
//			.put(UMLPackage.Literals.CLASSIFIER__INHERITED_MEMBER, ClassifierInheritedMemberQuerySpecification.instance())
//			.put(UMLPackage.Literals.COMPONENT__PROVIDED, ComponentProvidedQuerySpecification.instance())
//			.put(UMLPackage.Literals.COMPONENT__REQUIRED, ComponentRequiredQuerySpecification.instance())
			.put(UMLPackage.Literals.CONNECTABLE_ELEMENT__END, ConnectableElementEndQuerySpecification.instance())
			.put(UMLPackage.Literals.CONNECTOR__KIND, ConnectorKindQuerySpecification.instance())
//			.put(UMLPackage.Literals.CONNECTOR_END__DEFINING_END, ConnectorEndDefiningEndQuerySpecification.instance())
			.put(UMLPackage.Literals.DEPLOYMENT_TARGET__DEPLOYED_ELEMENT, DeploymentTargetDeployedElementQuerySpecification.instance())
			.put(UMLPackage.Literals.DIRECTED_RELATIONSHIP__SOURCE, DirectedRelationshipSourceQuerySpecification.instance())
			.put(UMLPackage.Literals.DIRECTED_RELATIONSHIP__TARGET, DirectedRelationshipTargetQuerySpecification.instance())
			.put(UMLPackage.Literals.ELEMENT__OWNED_ELEMENT, ElementOwnedElementQuerySpecification.instance())
			.put(UMLPackage.Literals.ELEMENT__OWNER, ElementOwnerQuerySpecification.instance())
			.put(UMLPackage.Literals.ENCAPSULATED_CLASSIFIER__OWNED_PORT, EncapsulatedClassifierOwnedPortQuerySpecification.instance())
//			.put(UMLPackage.Literals.EXTENSION__IS_REQUIRED, ExtensionIsRequiredQuerySpecification.instance())
			.put(UMLPackage.Literals.EXTENSION__METACLASS, ExtensionMetaclassQuerySpecification.instance())
			.put(UMLPackage.Literals.FEATURE__FEATURING_CLASSIFIER, FeatureFeaturingClassifierQuerySpecification.instance())
			.put(UMLPackage.Literals.MESSAGE__MESSAGE_KIND, MessageMessageKindQuerySpecification.instance())
//			.put(UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER, MultiplicityElementLowerQuerySpecification.instance())
//			.put(UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER, MultiplicityElementUpperQuerySpecification.instance())
			.put(UMLPackage.Literals.NAMED_ELEMENT__CLIENT_DEPENDENCY, NamedElementClientDependencyQuerySpecification.instance())
			.put(UMLPackage.Literals.NAMED_ELEMENT__NAMESPACE, NamedElementNamespaceQuerySpecification.instance())
			.put(UMLPackage.Literals.NAMED_ELEMENT__QUALIFIED_NAME, NamedElementQualifiedNameQuerySpecification.instance())
			.put(UMLPackage.Literals.NAMESPACE__IMPORTED_MEMBER, NamespaceImportedMemberQuerySpecification.instance())
			.put(UMLPackage.Literals.NAMESPACE__MEMBER, NamespaceMemberQuerySpecification.instance())
			.put(UMLPackage.Literals.NAMESPACE__OWNED_MEMBER, NamespaceOwnedMemberQuerySpecification.instance())
			.put(UMLPackage.Literals.OPAQUE_EXPRESSION__RESULT, OpaqueExpressionResultQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__IS_ORDERED, OperationIsOrderedQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__IS_UNIQUE, OperationIsUniqueQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__LOWER, OperationLowerQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__TYPE, OperationTypeQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__UPPER, OperationUpperQuerySpecification.instance())
			.put(UMLPackage.Literals.PACKAGE__NESTED_PACKAGE, PackageNestedPackageQuerySpecification.instance())
			.put(UMLPackage.Literals.PACKAGE__NESTING_PACKAGE, PackageNestingPackageQuerySpecification.instance())
			.put(UMLPackage.Literals.PACKAGE__OWNED_STEREOTYPE, PackageOwnedStereotypeQuerySpecification.instance())
			.put(UMLPackage.Literals.PACKAGE__OWNED_TYPE, PackageOwnedTypeQuerySpecification.instance())
//			.put(UMLPackage.Literals.PARAMETER__DEFAULT, ParameterDefaultQuerySpecification.instance())
//			.put(UMLPackage.Literals.PORT__PROVIDED, PortProvidedQuerySpecification.instance())
//			.put(UMLPackage.Literals.PORT__REQUIRED, PortRequiredQuerySpecification.instance())
//			.put(UMLPackage.Literals.PROPERTY__DEFAULT, PropertyDefaultQuerySpecification.instance())
			.put(UMLPackage.Literals.PROPERTY__IS_COMPOSITE, PropertyIsCompositeQuerySpecification.instance())
//			.put(UMLPackage.Literals.PROPERTY__OPPOSITE, PropertyOppositeQuerySpecification.instance())
			.put(UMLPackage.Literals.PROTOCOL_TRANSITION__REFERRED, ProtocolTransitionReferredQuerySpecification.instance())
			.put(UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINED_ELEMENT, RedefinableElementRedefinedElementQuerySpecification.instance())
			.put(UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINITION_CONTEXT, RedefinableElementRedefinitionContextQuerySpecification.instance())
			.put(UMLPackage.Literals.REDEFINABLE_TEMPLATE_SIGNATURE__INHERITED_PARAMETER, RedefinableTemplateSignatureInheritedParameterQuerySpecification.instance())
			.put(UMLPackage.Literals.RELATIONSHIP__RELATED_ELEMENT, RelationshipRelatedElementQuerySpecification.instance())
			.put(UMLPackage.Literals.STATE__IS_COMPOSITE, StateIsCompositeQuerySpecification.instance())
			.put(UMLPackage.Literals.STATE__IS_ORTHOGONAL, StateIsOrthogonalQuerySpecification.instance())
//			.put(UMLPackage.Literals.STATE__IS_SIMPLE, StateIsSimpleQuerySpecification.instance())
//			.put(UMLPackage.Literals.STATE__IS_SUBMACHINE_STATE, StateIsSubmachineStateQuerySpecification.instance())
//			.put(UMLPackage.Literals.STEREOTYPE__PROFILE, StereotypeProfileQuerySpecification.instance())
			.put(UMLPackage.Literals.STRUCTURED_CLASSIFIER__PART, StructuredClassifierPartQuerySpecification.instance())
			.put(UMLPackage.Literals.STRUCTURED_CLASSIFIER__ROLE, StructuredClassifierRoleQuerySpecification.instance())
			.put(UMLPackage.Literals.TYPE__PACKAGE, TypePackageQuerySpecification.instance())
			.put(UMLPackage.Literals.VERTEX__INCOMING, VertexIncomingQuerySpecification.instance())
			.put(UMLPackage.Literals.VERTEX__OUTGOING, VertexOutgoingQuerySpecification.instance())
			.build();
	}
}
