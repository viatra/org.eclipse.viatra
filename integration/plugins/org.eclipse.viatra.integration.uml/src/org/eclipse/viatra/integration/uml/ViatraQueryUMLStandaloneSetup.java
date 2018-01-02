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

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.eclipse.viatra.query.runtime.extensibility.SingletonQueryGroupProvider;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.connector.QueryGroupProviderSourceConnector;

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
     * @throws ViatraQueryRuntimeException if a query specification cannot be initialized or registered
     */
    public static void doSetup() {
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

    private static IQueryGroup getQueryGroup() {
        return DerivedFeatures.instance();
    }

    private static Map<EStructuralFeature, IQuerySpecification<?>> getSurrogateQueries() {
        
        return Collections.unmodifiableMap(Stream.of(
            new SimpleEntry<>(UMLPackage.Literals.ACTION__CONTEXT, ActionContext.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTION__INPUT, ActionInput.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTION__OUTPUT, ActionOutput.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY__GROUP, ActivityGroup.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY__NODE, ActivityNode.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_EDGE__IN_GROUP, ActivityEdgeInGroup.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_EDGE, ActivityGroupContainedEdge.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_GROUP__CONTAINED_NODE, ActivityGroupContainedNode.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_GROUP__IN_ACTIVITY, ActivityGroupInActivity.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_GROUP__SUBGROUP, ActivityGroupSubgroup.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_GROUP__SUPER_GROUP, ActivityGroupSuperGroup.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_NODE__ACTIVITY, ActivityNodeActivity.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ACTIVITY_NODE__IN_GROUP, ActivityNodeInGroup.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ASSOCIATION__END_TYPE, AssociationEndType.instance()),
            new SimpleEntry<>(UMLPackage.Literals.BEHAVIOR__CONTEXT, BehaviorContext.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CLASS__EXTENSION, ClassExtension.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CLASS__SUPER_CLASS, ClassSuperClass.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CLASSIFIER__ATTRIBUTE, ClassifierAttribute.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CLASSIFIER__FEATURE, ClassifierFeature.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CLASSIFIER__GENERAL, ClassifierGeneral.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.CLASSIFIER__INHERITED_MEMBER, ClassifierInheritedMemberQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.COMPONENT__PROVIDED, ComponentProvidedQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.COMPONENT__REQUIRED, ComponentRequiredQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CONNECTABLE_ELEMENT__END, ConnectableElementEnd.instance()),
            new SimpleEntry<>(UMLPackage.Literals.CONNECTOR__KIND, ConnectorKind.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.CONNECTOR_END__DEFINING_END, ConnectorEndDefiningEndQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.DEPLOYMENT_TARGET__DEPLOYED_ELEMENT, DeploymentTargetDeployedElement.instance()),
            new SimpleEntry<>(UMLPackage.Literals.DIRECTED_RELATIONSHIP__SOURCE, DirectedRelationshipSource.instance()),
            new SimpleEntry<>(UMLPackage.Literals.DIRECTED_RELATIONSHIP__TARGET, DirectedRelationshipTarget.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ELEMENT__OWNED_ELEMENT, ElementOwnedElement.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ELEMENT__OWNER, ElementOwner.instance()),
            new SimpleEntry<>(UMLPackage.Literals.ENCAPSULATED_CLASSIFIER__OWNED_PORT, EncapsulatedClassifierOwnedPort.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.EXTENSION__IS_REQUIRED, ExtensionIsRequiredQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.EXTENSION__METACLASS, ExtensionMetaclass.instance()),
            new SimpleEntry<>(UMLPackage.Literals.FEATURE__FEATURING_CLASSIFIER, FeatureFeaturingClassifier.instance()),
            new SimpleEntry<>(UMLPackage.Literals.MESSAGE__MESSAGE_KIND, MessageMessageKind.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER, MultiplicityElementLowerQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER, MultiplicityElementUpperQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.NAMED_ELEMENT__CLIENT_DEPENDENCY, NamedElementClientDependency.instance()),
            new SimpleEntry<>(UMLPackage.Literals.NAMED_ELEMENT__NAMESPACE, NamedElementNamespace.instance()),
            new SimpleEntry<>(UMLPackage.Literals.NAMED_ELEMENT__QUALIFIED_NAME, NamedElementQualifiedName.instance()),
            new SimpleEntry<>(UMLPackage.Literals.NAMESPACE__IMPORTED_MEMBER, NamespaceImportedMember.instance()),
            new SimpleEntry<>(UMLPackage.Literals.NAMESPACE__MEMBER, NamespaceMember.instance()),
            new SimpleEntry<>(UMLPackage.Literals.NAMESPACE__OWNED_MEMBER, NamespaceOwnedMember.instance()),
            new SimpleEntry<>(UMLPackage.Literals.OPAQUE_EXPRESSION__RESULT, OpaqueExpressionResult.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.OPERATION__IS_ORDERED, OperationIsOrderedQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.OPERATION__IS_UNIQUE, OperationIsUniqueQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.OPERATION__LOWER, OperationLowerQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.OPERATION__TYPE, OperationTypeQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.OPERATION__UPPER, OperationUpperQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.PACKAGE__NESTED_PACKAGE, PackageNestedPackage.instance()),
            new SimpleEntry<>(UMLPackage.Literals.PACKAGE__NESTING_PACKAGE, PackageNestingPackage.instance()),
            new SimpleEntry<>(UMLPackage.Literals.PACKAGE__OWNED_STEREOTYPE, PackageOwnedStereotype.instance()),
            new SimpleEntry<>(UMLPackage.Literals.PACKAGE__OWNED_TYPE, PackageOwnedType.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.PARAMETER__DEFAULT, ParameterDefaultQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.PORT__PROVIDED, PortProvidedQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.PORT__REQUIRED, PortRequiredQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.PROPERTY__DEFAULT, PropertyDefaultQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.PROPERTY__IS_COMPOSITE, PropertyIsComposite.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.PROPERTY__OPPOSITE, PropertyOppositeQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.PROTOCOL_TRANSITION__REFERRED, ProtocolTransitionReferred.instance()),
            new SimpleEntry<>(UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINED_ELEMENT, RedefinableElementRedefinedElement.instance()),
            new SimpleEntry<>(UMLPackage.Literals.REDEFINABLE_ELEMENT__REDEFINITION_CONTEXT, RedefinableElementRedefinitionContext.instance()),
            new SimpleEntry<>(UMLPackage.Literals.REDEFINABLE_TEMPLATE_SIGNATURE__INHERITED_PARAMETER, RedefinableTemplateSignatureInheritedParameter.instance()),
            new SimpleEntry<>(UMLPackage.Literals.RELATIONSHIP__RELATED_ELEMENT, RelationshipRelatedElement.instance()),
            new SimpleEntry<>(UMLPackage.Literals.STATE__IS_COMPOSITE, StateIsComposite.instance()),
            new SimpleEntry<>(UMLPackage.Literals.STATE__IS_ORTHOGONAL, StateIsOrthogonal.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.STATE__IS_SIMPLE, StateIsSimpleQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.STATE__IS_SUBMACHINE_STATE, StateIsSubmachineStateQuerySpecification.instance()),
//			new SimpleEntry<>(UMLPackage.Literals.STEREOTYPE__PROFILE, StereotypeProfileQuerySpecification.instance()),
            new SimpleEntry<>(UMLPackage.Literals.STRUCTURED_CLASSIFIER__PART, StructuredClassifierPart.instance()),
            new SimpleEntry<>(UMLPackage.Literals.STRUCTURED_CLASSIFIER__ROLE, StructuredClassifierRole.instance()),
            new SimpleEntry<>(UMLPackage.Literals.TYPE__PACKAGE, TypePackage.instance()),
            new SimpleEntry<>(UMLPackage.Literals.VERTEX__INCOMING, VertexIncoming.instance()),
            new SimpleEntry<>(UMLPackage.Literals.VERTEX__OUTGOING, VertexOutgoing.instance()))
        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    }
}
