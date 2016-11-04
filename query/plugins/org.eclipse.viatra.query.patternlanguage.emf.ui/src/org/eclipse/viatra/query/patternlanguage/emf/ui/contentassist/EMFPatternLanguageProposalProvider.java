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
package org.eclipse.viatra.query.patternlanguage.emf.ui.contentassist;

import static org.eclipse.emf.ecore.util.EcoreUtil.getRootContainer;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra.query.patternlanguage.emf.ResolutionException;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.services.EMFPatternLanguageGrammarAccess;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionElement;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EnumRule;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor.Delegate;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.xbase.ui.hover.XbaseInformationControlInput;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class EMFPatternLanguageProposalProvider extends AbstractEMFPatternLanguageProposalProvider {

    private static final Set<String> FILTERED_KEYWORDS = Sets.newHashSet("pattern");

    @Inject
    IScopeProvider scopeProvider;
    @Inject
    ReferenceProposalCreator crossReferenceProposalCreator;
    @Inject
    IQualifiedNameConverter nameConverter;
    @Inject
    private EMFPatternLanguageGrammarAccess ga;

    @Override
    public void createProposals(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        // suppress content assist in comments
        if (context.getCurrentNode().getGrammarElement() == ga.getML_COMMENTRule()
                || context.getCurrentNode().getGrammarElement() == ga.getSL_COMMENTRule()) {
            return;
        }
        super.createProposals(context, acceptor);
    }

    @Override
    protected Function<IEObjectDescription, ICompletionProposal> getProposalFactory(String ruleName,
            ContentAssistContext contentAssistContext) {
        
        Function<IEObjectDescription, ICompletionProposal> factory = super.getProposalFactory(ruleName, contentAssistContext);
        if (contentAssistContext.getCurrentNode().getSemanticElement() instanceof PatternCall && ga.getQualifiedNameRule().getName().equals(ruleName)) {
            factory = Functions.compose(new Function<ICompletionProposal, ICompletionProposal>(){

                @Override
                public ICompletionProposal apply(ICompletionProposal original) {
                    if (original instanceof ConfigurableCompletionProposal) {
                        ConfigurableCompletionProposal prop = (ConfigurableCompletionProposal) original;
                        Object info = prop.getAdditionalProposalInfo(new NullProgressMonitor());
                        if (info instanceof XbaseInformationControlInput) {
                            XbaseInformationControlInput input = (XbaseInformationControlInput) info;
                            if (input.getElement() instanceof Pattern) {
                                final Pattern pattern = (Pattern) input.getElement();
                                prop.setTextApplier(new PatternImporter(pattern));
                            }
                        }
                        
                    }
                    return original;
                }
                
            }, factory);
        }
        
        return factory;
    }
    
    @SuppressWarnings("restriction")
    @Override
    public void completeKeyword(Keyword keyword, ContentAssistContext contentAssistContext,
            ICompletionProposalAcceptor acceptor) {
        // ignore keywords in FILTERED set
        if (FILTERED_KEYWORDS.contains(keyword.getValue())) {
            return;
        }
        super.completeKeyword(keyword, contentAssistContext, acceptor);
    }

    @Override
    public void complete_ValueReference(EObject model, RuleCall ruleCall, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        super.complete_ValueReference(model, ruleCall, context, acceptor);
        if (model instanceof PathExpressionHead) {
            PathExpressionHead head = (PathExpressionHead) model;
            try {
                // XXX The following code re-specifies scoping instead of reusing the scope provider
                EClassifier typeClassifier = EMFPatternLanguageScopeHelper.calculateExpressionType(head);
                if (typeClassifier instanceof EEnum) {
                    // In case of EEnums add Enum Literal constants
                    EEnum type = (EEnum) typeClassifier;

                    ContentAssistContext.Builder myContextBuilder = context.copy();
                    myContextBuilder.setMatcher(new EnumPrefixMatcher(type.getName()));

                    for (EEnumLiteral literal : type.getELiterals()) {
                        ICompletionProposal completionProposal = createCompletionProposal("::" + literal.getName(),
                                type.getName() + "::" + literal.getName(), null, myContextBuilder.toContext());
                        acceptor.accept(completionProposal);
                    }
                }
                // XXX The following code re-specifies scoping instead of reusing the scope provider
                // Always refer to existing variables
                PatternBody body = (PatternBody) head.eContainer()/* PathExpression */.eContainer()/* PatternBody */;
                for (Variable var : body.getVariables()) {
                    acceptor.accept(createCompletionProposal(var.getName(), context));
                }
                Pattern pattern = (Pattern) body.eContainer();
                for (Variable var : pattern.getParameters()) {
                    acceptor.accept(createCompletionProposal(var.getName(), context));
                }
            } catch (ResolutionException e) {
                // If resolution fails, simply don't return anything
            }
        }
    }

    @Override
    public void completeType_Typename(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        PatternModel pModel = null;
        EObject root = getRootContainer(model);
        if (root instanceof PatternModel) {
            pModel = (PatternModel) root;
        }
        ContentAssistContext.Builder myContextBuilder = context.copy();
        myContextBuilder.setMatcher(new ClassifierPrefixMatcher(context.getMatcher(), getQualifiedNameConverter()));
        ClassType type = null;
        if (model instanceof Variable) {
            type = (ClassType) ((Variable) model).getType();
        } else {
            return;
        }

        ICompositeNode node = NodeModelUtils.getNode(type);
        int offset = node.getOffset();
        Region replaceRegion = new Region(offset, context.getReplaceRegion().getLength()
                + context.getReplaceRegion().getOffset() - offset);
        myContextBuilder.setReplaceRegion(replaceRegion);
        myContextBuilder.setLastCompleteNode(node);
        StringBuilder availablePrefix = new StringBuilder(4);
        for (ILeafNode leaf : node.getLeafNodes()) {
            if (leaf.getGrammarElement() != null && !leaf.isHidden()) {
                if ((leaf.getTotalLength() + leaf.getTotalOffset()) < context.getOffset())
                    availablePrefix.append(leaf.getText());
                else
                    availablePrefix.append(leaf.getText().substring(0, context.getOffset() - leaf.getTotalOffset()));
            }
            if (leaf.getTotalOffset() >= context.getOffset())
                break;
        }
        myContextBuilder.setPrefix(availablePrefix.toString());

        ContentAssistContext myContext = myContextBuilder.toContext();
        for (PackageImport declaration : EMFPatternLanguageHelper.getPackageImportsIterable(pModel)) {
            if (declaration.getEPackage() != null) {
                createClassifierProposals(declaration, model, myContext, acceptor);
            }
        }
    }

    private void createClassifierProposals(PackageImport declaration, EObject model, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        // String alias = declaration.getAlias();
        // QualifiedName prefix = (!Strings.isEmpty(alias))
        // ? QualifiedName.create(getValueConverter().toString(alias,"ID"))
        // : null;
        boolean createDatatypeProposals = modelOrContainerIs(model, Variable.class);
        boolean createEnumProposals = modelOrContainerIs(model, EnumRule.class);
        boolean createClassProposals = modelOrContainerIs(model, Variable.class);
        Function<IEObjectDescription, ICompletionProposal> factory = getProposalFactory(null, context);
        for (EClassifier classifier : declaration.getEPackage().getEClassifiers()) {
            if (classifier instanceof EDataType && createDatatypeProposals || classifier instanceof EEnum
                    && createEnumProposals || classifier instanceof EClass && createClassProposals) {
                String classifierName = getValueConverter().toString(classifier.getName(), "ID");
                QualifiedName proposalQualifiedName = /* (prefix != null) ? prefix.append(classifierName) : */QualifiedName
                        .create(classifierName);
                IEObjectDescription description = EObjectDescription.create(proposalQualifiedName, classifier);
                ConfigurableCompletionProposal proposal = (ConfigurableCompletionProposal) factory.apply(description);
                if (proposal != null) {
                    /*
                     * if (prefix != null) proposal.setDisplayString(classifier.getName() + " - " + alias);
                     */
                    proposal.setPriority(proposal.getPriority() * 2);
                }
                acceptor.accept(proposal);
            }
        }
    }

    private boolean modelOrContainerIs(EObject model, Class<?>... types) {
        for (Class<?> type : types) {
            if (type.isInstance(model) || type.isInstance(model.eContainer()))
                return true;
        }
        return false;
    }

    public void complete_RefType(PathExpressionElement model, RuleCall ruleCall, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        IScope scope = scopeProvider.getScope(model.getTail(),
                EMFPatternLanguagePackage.Literals.REFERENCE_TYPE__REFNAME);
        crossReferenceProposalCreator.lookupCrossReference(scope, model,
                EMFPatternLanguagePackage.Literals.REFERENCE_TYPE__REFNAME, acceptor,
                Predicates.<IEObjectDescription> alwaysTrue(),
                getProposalFactory(ruleCall.getRule().getName(), context));
    }

    @Override
    public void completeRefType_Refname(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        // This method is deliberately empty.
        // This override prohibits the content assist to suggest incorrect parameters.
    }

    @Override
    public void completePackageImport_EPackage(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        super.completePackageImport_EPackage(model, assignment, context, new StringProposalDelegate(acceptor, context));
    }
    
    /**
     * Remove extra double quote from proposal if the next character is already a double quote.
     * Based on the work of Christian Dietrich in {@link https://christiandietrich.wordpress.com/2015/03/19/xtext-and-strings-as-cross-references/} 
     * 
     * @author Abel Hegedus
     *
     */
    static class StringProposalDelegate extends Delegate {
        
        ContentAssistContext ctx;
 
        StringProposalDelegate(ICompletionProposalAcceptor delegate, ContentAssistContext ctx) {
            super(delegate);
            this.ctx = ctx;
        }
 
        public void accept(ICompletionProposal proposal) {
            if (proposal instanceof ConfigurableCompletionProposal) {
                ConfigurableCompletionProposal prop = (ConfigurableCompletionProposal) proposal;
                int replacementLength = prop.getReplacementLength();
                int endPos = prop.getReplacementOffset() + replacementLength; 
                IXtextDocument document = ctx.getDocument();
                if (document != null && document.getLength() > endPos) {
                    // We are not at the end of the file
                    try {
                        if ("\"".equals(document.get(endPos, 1))) {
                            prop.setReplacementLength(replacementLength-1);
                            String replacementString = prop.getReplacementString();
                            prop.setReplacementString(replacementString.substring(0,replacementString.length()-1));
                        }
                    } catch (BadLocationException e) {
                        // never happens, as we already checked that the given range is valid 
                        throw new IllegalStateException("No content at position inside the document", e);
                    }
                }
            }
            super.accept(proposal);
        }
 
    }
}
