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
package org.eclipse.viatra.query.patternlanguage.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.viatra.query.patternlanguage.annotations.PatternAnnotationProvider;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.xbase.typesystem.IExpressionScope.Anchor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class PatternLanguageProposalProvider extends AbstractPatternLanguageProposalProvider {

    @Inject
    private PatternAnnotationProvider annotationProvider;
    @Inject
    private IScopeProvider scopeProvider;
    @Inject
    private ReferenceProposalCreator crossReferenceProposalCreator;
    @Inject
    private ValidFeatureDescription featureDescriptionPredicate;

    @Override
    public void complete_Annotation(EObject model, RuleCall ruleCall, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        for (String annotationName : annotationProvider.getAllAnnotationNames()) {
            if (annotationProvider.isDeprecated(annotationName)) {
                continue;
            }
            String prefixedName = String.format("@%s", annotationName);
            String prefix = context.getPrefix();
            ContentAssistContext modifiedContext = context;
            INode lastNode = context.getLastCompleteNode();
            if ("".equals(prefix) && lastNode.getSemanticElement() instanceof Annotation) {
                Annotation previousNode = (Annotation) lastNode.getSemanticElement();
                String annotationPrefix = previousNode.getName();
                if (previousNode.getParameters().isEmpty()
                        && !annotationProvider.getAllAnnotationNames().contains(annotationPrefix)) {
                    modifiedContext = context.copy()
                            .setReplaceRegion(new Region(lastNode.getOffset(), lastNode.getLength() + prefix.length()))
                            .toContext();
                    prefixedName = annotationName;
                }
            }
            ICompletionProposal proposal = createCompletionProposal(prefixedName, prefixedName, null, modifiedContext);
            if (proposal instanceof ConfigurableCompletionProposal) {
                ((ConfigurableCompletionProposal) proposal).setAdditionalProposalInfo(annotationProvider
                        .getAnnotationObject(annotationName));
                ((ConfigurableCompletionProposal) proposal).setHover(getHover());
            }
            acceptor.accept(proposal);
        }
    }

    @Override
    public void complete_AnnotationParameter(EObject model, RuleCall ruleCall, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (model instanceof Annotation) {
            Annotation annotation = (Annotation) model;
            for (String paramName : annotationProvider.getAnnotationParameters(annotation.getName())) {
                String outputName = String.format("%s = ", paramName);
                ICompletionProposal proposal = createCompletionProposal(outputName, paramName, null, context);
                if (proposal instanceof ConfigurableCompletionProposal) {
                    ((ConfigurableCompletionProposal) proposal).setAdditionalProposalInfo(annotationProvider
                            .getAnnotationParameter(annotation.getName(), paramName));
                    ((ConfigurableCompletionProposal) proposal).setHover(getHover());
                }
                acceptor.accept(proposal);
            }
        }
    }

    @Override
    public void complete_VariableReference(EObject model, RuleCall ruleCall, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        IScope scope = scopeProvider.getScope(model, PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE);
        crossReferenceProposalCreator.lookupCrossReference(scope, model,
                PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE, acceptor,
                Predicates.<IEObjectDescription> alwaysTrue(),
                getProposalFactory(ruleCall.getRule().getName(), context));

    }

    @Override
    protected void createLocalVariableAndImplicitProposals(EObject context, Anchor anchor,
            ContentAssistContext contentAssistContext, ICompletionProposalAcceptor acceptor) {
        String prefix = contentAssistContext.getPrefix();
        if (prefix.length() > 0 && !Character.isJavaIdentifierStart(prefix.charAt(0))) {
            return;
        }

        final PatternBody body = EcoreUtil2.getContainerOfType(context, PatternBody.class);
        for (Variable v : body.getVariables()) {
            if (!v.getName().startsWith("_")) {
                ICompletionProposal proposal = createCompletionProposal(v.getName(), contentAssistContext);
                acceptor.accept(proposal);
            }
        }

        Function<IEObjectDescription, ICompletionProposal> proposalFactory = getProposalFactory(
                getFeatureCallRuleName(), contentAssistContext);
        proposeDeclaringTypeForStaticInvocation(context, null /* ignore */, contentAssistContext, acceptor);
    }

    @Override
    public void completePatternCall_PatternRef(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        /*
         * filter not local, private patterns (private patterns in other resources) this information stored in the
         * userdata of the EObjectDescription EObjectDescription only created for not local eObjects, so check for
         * resource equality is unnecessary.
         */
        lookupCrossReference(((CrossReference) assignment.getTerminal()), context, acceptor,
                Predicates.and(featureDescriptionPredicate, new Predicate<IEObjectDescription>() {

                    @Override
                    public boolean apply(IEObjectDescription input) {
                        return !("true".equals(input.getUserData("private")));
                    }
                }));
    }
}
