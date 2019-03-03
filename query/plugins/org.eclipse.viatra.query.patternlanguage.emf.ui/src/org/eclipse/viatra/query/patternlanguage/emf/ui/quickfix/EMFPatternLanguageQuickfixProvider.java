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
package org.eclipse.viatra.query.patternlanguage.emf.ui.quickfix;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VQLImportSection;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.ui.quickfix.XbaseQuickfixProvider;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageQuickfixProvider extends XbaseQuickfixProvider {
    
    private static final String WHITELIST_CONTEXT = "org.eclipse.viatra.documentation.help.whitelist";
    private static final String USAGECOUNTING_CONTEXT = "org.eclipse.viatra.documentation.help.usagecounting";
    private static final String AGGREGATED_CHAIN_CONTEXT = "org.eclipse.viatra.documentation.help.aggregated-chain";

    private static final class AddDependency implements IModification {

        private final Issue issue;

        private AddDependency(Issue issue) {
            this.issue = issue;
        }

        @Override
        public void apply(IModificationContext context) throws CoreException, BadLocationException {
            URI uriToProblem = issue.getUriToProblem();
            if (uriToProblem.isPlatform()) {
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                IFile file = root.getFile(new Path(uriToProblem.toPlatformString(true)));
                if (file.exists() && !file.isReadOnly())
                    ProjectGenerationHelper.ensureBundleDependencies(file.getProject(),
                            Arrays.asList(issue.getData()));
                // The following change changes the document thus
                // triggers its parsing
                IXtextDocument document = context.getXtextDocument();
                document.replace(issue.getOffset(), 1, document.get(issue.getOffset(), 1));
            }
        }
    }

    @Inject
    private IJvmTypeProvider.Factory typeProviderFactory;
    @Inject
    private EMFTypeInferrer typeInferrer;
    @Inject
    private EMFTypeSystem typeSystem;
    
    @Fix(IssueCodes.IDENTIFIER_AS_KEYWORD)
    public void escapeKeywordAsIdentifier(final Issue issue, IssueResolutionAcceptor acceptor) {
        acceptor.accept(issue, "Prefix Identifier", "Adds a ^ prefix to the identifier", null, new IModification() {

            @Override
            public void apply(IModificationContext context) throws BadLocationException {
                IXtextDocument document = context.getXtextDocument();
                document.replace(issue.getOffset(), 0, "^");
            }
        });
    }

    @Fix(IssueCodes.MISSING_PARAMETER_TYPE)
    public void inferMissingParameterType(final Issue issue, IssueResolutionAcceptor acceptor) {
        for (final String data : issue.getData()) {
            if (data.startsWith(IssueCodes.JAVA_TYPE_PREFIX)) {
                final String typeName = data.substring(IssueCodes.JAVA_TYPE_PREFIX.length());
                acceptor.accept(issue, "Insert Java type '" + typeName + "'",
                        "Declares the inferred type " + typeName + " for the variable.",
                        null, new ISemanticModification() {

                            @Override
                            public void apply(EObject element, IModificationContext context) throws Exception {
                                Variable var = (Variable) element;
                                final JavaType typeRef = PatternLanguageFactory.eINSTANCE.createJavaType();
                                
                                final JvmType jvmType = typeProviderFactory.findOrCreateTypeProvider(element.eResource().getResourceSet()).findTypeByName(typeName);
                                typeRef.setClassRef((JvmDeclaredType) jvmType);
                                var.setType(typeRef);
                                
                            }

                        });
            } else {
                acceptor.accept(issue, "Insert EMF type '" + data + "'",
                        "Declares the inferred type " + data + " for the variable. \n\n",
                        null, new IModification() {

                            @Override
                            public void apply(IModificationContext context) throws Exception {
                                IXtextDocument document = context.getXtextDocument();
                                document.replace(issue.getOffset() + issue.getLength(), 0, " : " + data);
                            }

                        });
            }
        }
    }
    
    @Fix(IssueCodes.PARAMETER_TYPE_AMBIGUOUS)
    public void addAmbiguousParameterType(final Issue issue, IssueResolutionAcceptor acceptor) {
        for (final String data : issue.getData()) {
            acceptor.accept(issue, "Insert type '" + data + "'", 
                    "Declares the inferred type " + data + " for the variable. \n\n" +
                            "Warning! When not matching the entire ResourceSet, \n" + 
                            "this might slightly change the results of the pattern; \n" + 
                            "look at the documentation of Query Scopes for details.", 
                            null, new IModification() {
                
                @Override
                public void apply(IModificationContext context) throws Exception {
                    IXtextDocument document = context.getXtextDocument();
                    document.replace(issue.getOffset() + issue.getLength(), 0, " : " + data);
                }
                
            });
        }
    }
    
    @Fix(IssueCodes.MISSING_PACKAGE_IMPORT)
    public void addMissingPackageImport(final Issue issue, IssueResolutionAcceptor acceptor) {
        
        acceptor.accept(issue, "Add missing import", "Add missing import", null, (IModification) context -> {
            final IXtextDocument document = context.getXtextDocument();
            Integer offset = document.readOnly(state -> {
                final VQLImportSection importSection = (VQLImportSection) 
                        Iterators.find(state.getAllContents(), Predicates.instanceOf(VQLImportSection.class), null);
                final ICompositeNode node = NodeModelUtils.getNode(importSection);
                
                return Integer.valueOf(node.getTotalEndOffset());
            });
            if (offset != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n");
                sb.append("import \"");
                sb.append(issue.getData()[0]);
                sb.append("\"");
                document.replace(offset, 0, sb.toString());
            }
        });
    }

    @Fix(IssueCodes.IMPORT_DEPENDENCY_MISSING)
    public void addDependency(final Issue issue, IssueResolutionAcceptor acceptor) {
        acceptor.accept(issue, "Add dependency", "Add the required bundle to the manifest.mf file.", null,
                new AddDependency(issue));
    }
    
    @Fix(IssueCodes.IQR_NOT_ON_CLASSPATH)
    public void addLibrary(final Issue issue, IssueResolutionAcceptor acceptor) {
        if (issue.getData().length > 1) {
            return;
        }
        acceptor.accept(issue, "Add dependency", 
                String.format("Add the required bundle '%s' to the manifest.mf file.", issue.getData()[0]), 
                null,
                new AddDependency(issue));
    }
    
    @Fix(IssueCodes.LOCAL_VARIABLE_REFERENCED_ONCE)
    public void handleSingleUseVariables(final Issue issue, IssueResolutionAcceptor acceptor) {
        acceptor.accept(issue, "Prefix Variable", "Adds a _ prefix to the variable to mark it as single-use", null, new IModification() {

            @Override
            public void apply(IModificationContext context) throws BadLocationException {
                IXtextDocument document = context.getXtextDocument();
                document.replace(issue.getOffset(), 0, "_");
            }
        });
        
        acceptor.accept(issue, "Add as parameter", "Adds the local variable as a parameter", null,
                new ISemanticModification() {

                    @Override
                    public void apply(EObject element, IModificationContext context) throws Exception {
                        VariableReference varRef = (VariableReference) element;
                        Pattern containingPattern = EcoreUtil2.getContainerOfType(varRef, Pattern.class);
                      
                        final Parameter parameter = PatternLanguageFactory.eINSTANCE.createParameter();
                        containingPattern.getParameters().add(parameter);
                        parameter.setName(varRef.getVar());
                        final Type type = typeSystem.convertToVQLType(varRef, typeInferrer.getType(varRef));
                        parameter.setType(type);
                        
                        for (PatternBody body : containingPattern.getBodies()) {
                            final ParameterRef reference = PatternLanguageFactory.eINSTANCE.createParameterRef();
                            reference.setName(varRef.getVar());
                            reference.setReferredParam(parameter);
                            final Optional<Variable> variable = body.getVariables().stream()
                                    .filter(v -> Objects.equals(v.getName(), varRef.getVar())).findFirst();
                            if (variable.isPresent()) {
                                EcoreUtil.replace(variable.get(), reference);
                            } else {
                                body.getVariables().add(reference);
                            }
                        }
                    }

                });
    }
    
    @Fix(IssueCodes.AGGREGATED_FEATURE_CHAIN)
    public void explainAggregatedChain(final Issue issue, IssueResolutionAcceptor acceptor) {
        explainWithHelp(issue, acceptor, AGGREGATED_CHAIN_CONTEXT);
    }
    @Fix(IssueCodes.LOCAL_VARIABLE_READONLY)
    public void explainUsageCounting1(final Issue issue, IssueResolutionAcceptor acceptor) {
        explainWithHelp(issue, acceptor, USAGECOUNTING_CONTEXT);
    }
    @Fix(IssueCodes.LOCAL_VARIABLE_NO_POSITIVE_REFERENCE)
    public void explainUsageCounting2(final Issue issue, IssueResolutionAcceptor acceptor) {
        explainWithHelp(issue, acceptor, USAGECOUNTING_CONTEXT);
    }
    @Fix(IssueCodes.LOCAL_VARIABLE_QUANTIFIED_REFERENCE)
    public void explainUsageCounting3(final Issue issue, IssueResolutionAcceptor acceptor) {
        explainWithHelp(issue, acceptor, USAGECOUNTING_CONTEXT);
    }
    @Fix(IssueCodes.SYMBOLIC_VARIABLE_NEVER_REFERENCED)
    public void explainUsageCounting4(final Issue issue, IssueResolutionAcceptor acceptor) {
        explainWithHelp(issue, acceptor, USAGECOUNTING_CONTEXT);
    }
    @Fix(IssueCodes.CHECK_WITH_IMPURE_JAVA_CALLS)
    public void explainImpureCall(final Issue issue, IssueResolutionAcceptor acceptor) {
        explainWithHelp(issue, acceptor, WHITELIST_CONTEXT);
    }

    private void explainWithHelp(final Issue issue, IssueResolutionAcceptor acceptor, final String helpContextID) {
        acceptor.accept(issue, "Explain message", "", null,
                (IModification) context -> PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContextID));
    }
    
}
