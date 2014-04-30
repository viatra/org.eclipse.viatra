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
package org.eclipse.incquery.patternlanguage.emf.ui.contentassist;

import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternImport;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.XImportSection;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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
    private final Pattern pattern;
    private ImportState importAvailable;

    private enum ImportState {
        NONE, FOUND, CONFLICTING
    }

    public PatternImporter(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void apply(IDocument document, ConfigurableCompletionProposal proposal) throws BadLocationException {
        if (document instanceof IXtextDocument) {
            if (pattern == null || Strings.isNullOrEmpty(pattern.getName()))
                return;
            importAvailable = ((IXtextDocument) document)
                    .readOnly(new IUnitOfWork<ImportState, XtextResource>() {

                        @Override
                        public ImportState exec(XtextResource state) throws Exception {
                            final XImportSection importSection = (XImportSection) Iterators.find(
                                    state.getAllContents(), Predicates.instanceOf(XImportSection.class), null);
                            PatternImport relatedImport = Iterables.find(importSection.getPatternImport(),
                                    new Predicate<PatternImport>() {

                                        @Override
                                        public boolean apply(PatternImport decl) {
                                            return pattern.equals(decl.getPattern())
                                                    || pattern.getName().equals(decl.getPattern().getName());
                                        }
                                    }, null);
                            if (relatedImport == null) {

                                return ImportState.NONE;
                            }
                            // Checking whether found pattern definition equals to different pattern
                            if (pattern.equals(relatedImport.getPattern())) {
                                return ImportState.FOUND;
                            } else {
                                return ImportState.CONFLICTING;
                            }
                        }
                    });
            super.apply(document, proposal);
            if (importAvailable == ImportState.NONE) {
                ((IXtextDocument) document).modify(new Void<XtextResource>() {

                    @Override
                    public void process(XtextResource state) throws Exception {
                        final XImportSection importSection = (XImportSection) Iterators.find(state.getAllContents(),
                                Predicates.instanceOf(XImportSection.class), null);
                        PatternImport newImport = EMFPatternLanguageFactory.eINSTANCE.createPatternImport();
                        newImport.setPattern(pattern);
                        importSection.getPatternImport().add(newImport);

                    }
                });
            }
        }
    }

    @Override
    public String getActualReplacementString(ConfigurableCompletionProposal proposal) {
        // Only use short name if import is available
        if (importAvailable != ImportState.CONFLICTING) {
            return pattern.getName();
        } else {
            return CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        }
    }
}