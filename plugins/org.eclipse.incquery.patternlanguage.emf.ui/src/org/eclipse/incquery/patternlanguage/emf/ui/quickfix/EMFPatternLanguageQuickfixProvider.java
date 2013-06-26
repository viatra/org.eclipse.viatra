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
package org.eclipse.incquery.patternlanguage.emf.ui.quickfix;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.XImportSection;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.ui.quickfix.XbaseQuickfixProvider;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

public class EMFPatternLanguageQuickfixProvider extends XbaseQuickfixProvider {

    @Fix(EMFIssueCodes.IDENTIFIER_AS_KEYWORD)
    public void escapeKeywordAsIdentifier(final Issue issue, IssueResolutionAcceptor acceptor) {
        acceptor.accept(issue, "Prefix Identifier", "Adds a ^ prefix to the identifier", null, new IModification() {

            @Override
            public void apply(IModificationContext context) throws BadLocationException {
                IXtextDocument document = context.getXtextDocument();
                document.replace(issue.getOffset(), 0, "^");
            }
        });
    }

    @Fix(EMFIssueCodes.MISSING_PACKAGE_IMPORT)
    public void addMissingPackageImport(final Issue issue, IssueResolutionAcceptor acceptor) {
        
        acceptor.accept(issue, "Add missing import", "Add missing import", null, new IModification() {
            
            @Override
            public void apply(IModificationContext context) throws BadLocationException {
                final IXtextDocument document = context.getXtextDocument();
                Integer offset = document.readOnly(new IUnitOfWork<Integer, XtextResource>() {

                    @Override
                    public Integer exec(XtextResource state) {
                        final XImportSection importSection = (XImportSection) 
                                Iterators.find(state.getAllContents(), Predicates.instanceOf(XImportSection.class), null);
                        final ICompositeNode node = NodeModelUtils.getNode(importSection);
                        
                        return Integer.valueOf(node.getTotalEndOffset());
                    }
                });
                if (offset != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\n");
                    sb.append("import \"");
                    sb.append(issue.getData()[0]);
                    sb.append("\"");
                    document.replace(offset, 0, sb.toString());
                }
            }
        });
    }

    @Fix(EMFIssueCodes.IMPORT_DEPENDENCY_MISSING)
    public void addDependency(final Issue issue, IssueResolutionAcceptor acceptor) {
        acceptor.accept(issue, "Add dependency", "Add the required bundle to the manifest.mf file.", null,
                new IModification() {

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
                });
    }
}
