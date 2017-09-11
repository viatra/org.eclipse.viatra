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
import org.eclipse.viatra.integration.uml.derivedfeatures.ActionContext;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActionInput;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActionOutput;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityEdgeInGroup;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityGroupContainedEdge;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityGroupContainedNode;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityGroupInActivity;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityGroup;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityGroupSubgroup;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityGroupSuperGroup;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityNodeActivity;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityNodeInGroup;
import org.eclipse.viatra.integration.uml.derivedfeatures.ActivityNode;
import org.eclipse.viatra.integration.uml.derivedfeatures.AssociationEndType;
import org.eclipse.viatra.integration.uml.derivedfeatures.BehaviorContext;
import org.eclipse.viatra.integration.uml.derivedfeatures.ClassExtension;
import org.eclipse.viatra.integration.uml.derivedfeatures.ClassSuperClass;
import org.eclipse.viatra.integration.uml.derivedfeatures.ClassifierAttribute;
import org.eclipse.viatra.integration.uml.derivedfeatures.ClassifierFeature;
import org.eclipse.viatra.integration.uml.derivedfeatures.ClassifierGeneral;
import org.eclipse.viatra.integration.uml.derivedfeatures.ConnectableElementEnd;
import org.eclipse.viatra.integration.uml.derivedfeatures.ConnectorKind;
import org.eclipse.viatra.integration.uml.derivedfeatures.DeploymentTargetDeployedElement;
import org.eclipse.viatra.integration.uml.derivedfeatures.DirectedRelationshipSource;
import org.eclipse.viatra.integration.uml.derivedfeatures.DirectedRelationshipTarget;
import org.eclipse.viatra.integration.uml.derivedfeatures.ElementOwnedElement;
import org.eclipse.viatra.integration.uml.derivedfeatures.ElementOwner;
import org.eclipse.viatra.integration.uml.derivedfeatures.EncapsulatedClassifierOwnedPort;
import org.eclipse.viatra.integration.uml.derivedfeatures.ExtensionMetaclass;
import org.eclipse.viatra.integration.uml.derivedfeatures.FeatureFeaturingClassifier;
import org.eclipse.viatra.integration.uml.derivedfeatures.MessageMessageKind;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementClientDependency;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementNamespace;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementQualifiedName;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamespaceImportedMember;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamespaceMember;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamespaceOwnedMember;
import org.eclipse.viatra.integration.uml.derivedfeatures.OpaqueExpressionResult;
import org.eclipse.viatra.integration.uml.derivedfeatures.PackageNestedPackage;
import org.eclipse.viatra.integration.uml.derivedfeatures.PackageNestingPackage;
import org.eclipse.viatra.integration.uml.derivedfeatures.PackageOwnedStereotype;
import org.eclipse.viatra.integration.uml.derivedfeatures.PackageOwnedType;
import org.eclipse.viatra.integration.uml.derivedfeatures.PropertyIsComposite;
import org.eclipse.viatra.integration.uml.derivedfeatures.ProtocolTransitionReferred;
import org.eclipse.viatra.integration.uml.derivedfeatures.RedefinableElementRedefinedElement;
import org.eclipse.viatra.integration.uml.derivedfeatures.RedefinableElementRedefinitionContext;
import org.eclipse.viatra.integration.uml.derivedfeatures.RedefinableTemplateSignatureInheritedParameter;
import org.eclipse.viatra.integration.uml.derivedfeatures.RelationshipRelatedElement;
import org.eclipse.viatra.integration.uml.derivedfeatures.StateIsComposite;
import org.eclipse.viatra.integration.uml.derivedfeatures.StateIsOrthogonal;
import org.eclipse.viatra.integration.uml.derivedfeatures.StructuredClassifierPart;
import org.eclipse.viatra.integration.uml.derivedfeatures.StructuredClassifierRole;
import org.eclipse.viatra.integration.uml.derivedfeatures.TypePackage;
import org.eclipse.viatra.integration.uml.derivedfeatures.VertexIncoming;
import org.eclipse.viatra.integration.uml.derivedfeatures.VertexOutgoing;
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

    private ViatraQueryUMLStandaloneSetup() {}
    
    /**
     * Register the query specifications
     * @throws ViatraQueryException if a query specification cannot be initialized or registered
     */
    public static void doSetup() throws ViatraQueryException {
        // query specification registry
        SingletonQueryGroupProvider groupProvider = new SingletonQueryGroupProvider(getQueryGroup());
        QueryGroupProviderSourceConnector sourceConnector = new QueryGroupProviderSourceConnector(CONNECTOR_ID, groupProvider, true);
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
            .put(UMLPackage.Literals.ACTION__CONTEXT, ActionContext.instance())
            .put(UMLPackage.Literals.ACTION__INPUT, ActionInput.instance())
            .put(UMLPackage.Literals.ACTION__OUTPUT, ActionOutput.instance())
            .put(UMLPackage.Literals.ACTIVITY__GROUP, ActivityGroup.instance())
            .put(UMLPackage.Literals.ACTIVITY__NODE, ActivityNode.instance())
            .put(UMLPackage.Literals.ACTIVITY_EDGE__IN_GROUP, ActivityEdgeInGroup.instance())
            .put(UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_EDGE, ActivityGroupContainedEdge.instance())
            .put(UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_NODE, ActivityGroupContainedNode.instance())
            .put(UMLPackage.Literals.ACTIVITY_GROUP__IN_ACTIVITY, ActivityGroupInActivity.instance())
            .put(UMLPackage.Literals.ACTIVITY_GROUP__SUBGROUP, ActivityGroupSubgroup.instance())
            .put(UMLPackage.Literals.ACTIVITY_GROUP__SUPER_GROUP, ActivityGroupSuperGroup.instance())
            .put(UMLPackage.Literals.ACTIVITY_NODE__ACTIVITY, ActivityNodeActivity.instance())
            .put(UMLPackage.Literals.ACTIVITY_NODE__IN_GROUP, ActivityNodeInGroup.instance())
            .put(UMLPackage.Literals.ASSOCIATION__END_TYPE, AssociationEndType.instance())
            .put(UMLPackage.Literals.BEHAVIOR__CONTEXT, BehaviorContext.instance())
            .put(UMLPackage.Literals.CLASS__EXTENSION, ClassExtension.instance())
            .put(UMLPackage.Literals.CLASS__SUPER_CLASS, ClassSuperClass.instance())
            .put(UMLPackage.Literals.CLASSIFIER__ATTRIBUTE, ClassifierAttribute.instance())
            .put(UMLPackage.Literals.CLASSIFIER__FEATURE, ClassifierFeature.instance())
            .put(UMLPackage.Literals.CLASSIFIER__GENERAL, ClassifierGeneral.instance())
//			.put(UMLPackage.Literals.CLASSIFIER__INHERITED_MEMBER, ClassifierInheritedMemberQuerySpecification.instance())
//			.put(UMLPackage.Literals.COMPONENT__PROVIDED, ComponentProvidedQuerySpecification.instance())
//			.put(UMLPackage.Literals.COMPONENT__REQUIRED, ComponentRequiredQuerySpecification.instance())
            .put(UMLPackage.Literals.CONNECTABLE_ELEMENT__END, ConnectableElementEnd.instance())
            .put(UMLPackage.Literals.CONNECTOR__KIND, ConnectorKind.instance())
//			.put(UMLPackage.Literals.CONNECTOR_END__DEFINING_END, ConnectorEndDefiningEndQuerySpecification.instance())
            .put(UMLPackage.Literals.DEPLOYMENT_TARGET__DEPLOYED_ELEMENT, DeploymentTargetDeployedElement.instance())
            .put(UMLPackage.Literals.DIRECTED_RELATIONSHIP__SOURCE, DirectedRelationshipSource.instance())
            .put(UMLPackage.Literals.DIRECTED_RELATIONSHIP__TARGET, DirectedRelationshipTarget.instance())
            .put(UMLPackage.Literals.ELEMENT__OWNED_ELEMENT, ElementOwnedElement.instance())
            .put(UMLPackage.Literals.ELEMENT__OWNER, ElementOwner.instance())
            .put(UMLPackage.Literals.ENCAPSULATED_CLASSIFIER__OWNED_PORT, EncapsulatedClassifierOwnedPort.instance())
//			.put(UMLPackage.Literals.EXTENSION__IS_REQUIRED, ExtensionIsRequiredQuerySpecification.instance())
            .put(UMLPackage.Literals.EXTENSION__METACLASS, ExtensionMetaclass.instance())
            .put(UMLPackage.Literals.FEATURE__FEATURING_CLASSIFIER, FeatureFeaturingClassifier.instance())
            .put(UMLPackage.Literals.MESSAGE__MESSAGE_KIND, MessageMessageKind.instance())
//			.put(UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER, MultiplicityElementLowerQuerySpecification.instance())
//			.put(UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER, MultiplicityElementUpperQuerySpecification.instance())
            .put(UMLPackage.Literals.NAMED_ELEMENT__CLIENT_DEPENDENCY, NamedElementClientDependency.instance())
            .put(UMLPackage.Literals.NAMED_ELEMENT__NAMESPACE, NamedElementNamespace.instance())
            .put(UMLPackage.Literals.NAMED_ELEMENT__QUALIFIED_NAME, NamedElementQualifiedName.instance())
            .put(UMLPackage.Literals.NAMESPACE__IMPORTED_MEMBER, NamespaceImportedMember.instance())
            .put(UMLPackage.Literals.NAMESPACE__MEMBER, NamespaceMember.instance())
            .put(UMLPackage.Literals.NAMESPACE__OWNED_MEMBER, NamespaceOwnedMember.instance())
            .put(UMLPackage.Literals.OPAQUE_EXPRESSION__RESULT, OpaqueExpressionResult.instance())
//			.put(UMLPackage.Literals.OPERATION__IS_ORDERED, OperationIsOrderedQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__IS_UNIQUE, OperationIsUniqueQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__LOWER, OperationLowerQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__TYPE, OperationTypeQuerySpecification.instance())
//			.put(UMLPackage.Literals.OPERATION__UPPER, OperationUpperQuerySpecification.instance())
            .put(UMLPackage.Literals.PACKAGE__NESTED_PACKAGE, PackageNestedPackage.instance())
            .put(UMLPackage.Literals.PACKAGE__NESTING_PACKAGE, PackageNestingPackage.instance())
            .put(UMLPackage.Literals.PACKAGE__OWNED_STEREOTYPE, PackageOwnedStereotype.instance())
            .put(UMLPackage.Literals.PACKAGE__OWNED_TYPE, PackageOwnedType.instance())
//			.put(UMLPackage.Literals.PARAMETER__DEFAULT, ParameterDefaultQuerySpecification.instance())
//			.put(UMLPackage.Literals.PORT__PROVIDED, PortProvidedQuerySpecification.instance())
//			.put(UMLPackage.Literals.PORT__REQUIRED, PortRequiredQuerySpecification.instance())
//			.put(UMLPackage.Literals.PROPERTY__DEFAULT, PropertyDefaultQuerySpecification.instance())
            .put(UMLPackage.Literals.PROPERTY__IS_COMPOSITE, PropertyIsComposite.instance())
//			.put(UMLPackage.Literals.PROPERTY__OPPOSITE, PropertyOppositeQuerySpecification.instance())
            .put(UMLPackage.Literals.PROTOCOL_TRANSITION__REFERRED, ProtocolTransitionReferred.instance())
            .put(UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINED_ELEMENT, RedefinableElementRedefinedElement.instance())
            .put(UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINITION_CONTEXT, RedefinableElementRedefinitionContext.instance())
            .put(UMLPackage.Literals.REDEFINABLE_TEMPLATE_SIGNATURE__INHERITED_PARAMETER, RedefinableTemplateSignatureInheritedParameter.instance())
            .put(UMLPackage.Literals.RELATIONSHIP__RELATED_ELEMENT, RelationshipRelatedElement.instance())
            .put(UMLPackage.Literals.STATE__IS_COMPOSITE, StateIsComposite.instance())
            .put(UMLPackage.Literals.STATE__IS_ORTHOGONAL, StateIsOrthogonal.instance())
//			.put(UMLPackage.Literals.STATE__IS_SIMPLE, StateIsSimpleQuerySpecification.instance())
//			.put(UMLPackage.Literals.STATE__IS_SUBMACHINE_STATE, StateIsSubmachineStateQuerySpecification.instance())
//			.put(UMLPackage.Literals.STEREOTYPE__PROFILE, StereotypeProfileQuerySpecification.instance())
            .put(UMLPackage.Literals.STRUCTURED_CLASSIFIER__PART, StructuredClassifierPart.instance())
            .put(UMLPackage.Literals.STRUCTURED_CLASSIFIER__ROLE, StructuredClassifierRole.instance())
            .put(UMLPackage.Literals.TYPE__PACKAGE, TypePackage.instance())
            .put(UMLPackage.Literals.VERTEX__INCOMING, VertexIncoming.instance())
            .put(UMLPackage.Literals.VERTEX__OUTGOING, VertexOutgoing.instance())
            .build();
    }
}
