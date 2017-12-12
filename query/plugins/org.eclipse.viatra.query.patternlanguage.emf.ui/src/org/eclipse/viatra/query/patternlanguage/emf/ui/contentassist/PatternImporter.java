/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.contentassist;

import java.util.Objects;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.VQLImportSection;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ReplacementTextApplier;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.util.concurrent.IUnitOfWork.Void;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * 
 * A text applier for content assist that adds the short name of the pattern if possible, and adds a corresponding
 * import declaration as well.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
final class PatternImporter extends ReplacementTextApplier {
    private final Pattern targetPattern;
    private final String targetPackage;
    private ImportState importStatus;

    private enum ImportState {
        NONE, SAMEPACKAGE, FOUND, CONFLICTING
    }

    public PatternImporter(Pattern targetPattern) {
        this.targetPattern = targetPattern;
        targetPackage = CorePatternLanguageHelper.getPackageName(targetPattern);
    }

    @Override
    public void apply(final IDocument document, final ConfigurableCompletionProposal proposal)
            throws BadLocationException {
        if (document instanceof IXtextDocument) {
            IXtextDocument xtextDocument = (IXtextDocument) document;
            if (targetPattern == null || Strings.isNullOrEmpty(targetPattern.getName()))
                return;
            importStatus = ((IXtextDocument) document).readOnly(new IUnitOfWork<ImportState, XtextResource>() {

                @Override
                public ImportState exec(XtextResource state) throws Exception {
                    final PatternModel model = (PatternModel) Iterators.find(state.getAllContents(),
                            Predicates.instanceOf(PatternModel.class));
                    if (Objects.equals(targetPackage, model.getPackageName())) {
                        return ImportState.SAMEPACKAGE;
                    }
                    final VQLImportSection importSection = model.getImportPackages();
                    PatternImport relatedImport = Iterables.find(importSection.getPatternImport(),
                            new Predicate<PatternImport>() {

                                @Override
                                public boolean apply(PatternImport decl) {
                                    return targetPattern.equals(decl.getPattern())
                                            || targetPattern.getName().equals(decl.getPattern().getName());
                                }
                            }, null);
                    if (relatedImport == null) {

                        return ImportState.NONE;
                    }
                    // Checking whether found pattern definition equals to different pattern
                    if (targetPattern.equals(relatedImport.getPattern())) {
                        return ImportState.FOUND;
                    } else {
                        return ImportState.CONFLICTING;
                    }
                }
            });

            String replacementString = getActualReplacementString(proposal) + "();";
            ReplaceEdit edit = new ReplaceEdit(proposal.getReplacementOffset(), proposal.getReplacementLength(),
                    replacementString);
            edit.apply(document);
            //+2 is used to put the cursor inside the parentheses
            int cursorOffset = getActualReplacementString(proposal).length() + 2;
            if (importStatus == ImportState.NONE) {
                xtextDocument.modify(new Void<XtextResource>() {

                    @Override
                    public void process(XtextResource state) throws Exception {
                        VQLImportSection importSection = (VQLImportSection) Iterators.find(state.getAllContents(),
                                Predicates.instanceOf(VQLImportSection.class), null);
                        if (importSection.getImportDeclarations().size() + importSection.getPackageImport().size()
                                + importSection.getPatternImport().size() == 0) {
                            //Empty import sections need to be replaced to generate white space after the package declaration
                            VQLImportSection newSection = EMFPatternLanguageFactory.eINSTANCE.createVQLImportSection();
                            ((PatternModel) importSection.eContainer()).setImportPackages(newSection);
                            importSection = newSection;
                        }
                        PatternImport newImport = EMFPatternLanguageFactory.eINSTANCE.createPatternImport();
                        newImport.setPattern(targetPattern);
                        importSection.getPatternImport().add(newImport);

                    }
                });
                //Two new lines + "import " + pattern fqn
                cursorOffset += 2 + 7 + CorePatternLanguageHelper.getFullyQualifiedName(targetPattern).length();
            }
            proposal.setCursorPosition(cursorOffset);
        }
    }

    @Override
    public String getActualReplacementString(ConfigurableCompletionProposal proposal) {
        // Only use short name if import is available
        if (importStatus != ImportState.CONFLICTING) {
            return targetPattern.getName();
        } else {
            return CorePatternLanguageHelper.getFullyQualifiedName(targetPattern);
        }
    }
}