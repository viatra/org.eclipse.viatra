/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.codemining;

import java.util.Objects;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.viatra.query.patternlanguage.emf.services.EMFPatternLanguageGrammarAccess;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguagePreferenceConstants;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.codemining.AbstractXtextCodeMiningProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.IAcceptor;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageCodeMiningProvider extends AbstractXtextCodeMiningProvider {
	
    @Inject
    EMFPatternLanguageGrammarAccess grammar;
    @Inject
    EMFTypeInferrer typeInferrer;
    @Inject
    EMFTypeSystem typeSystem;
    
    boolean isMiningEnabled = false;
    
    IPropertyChangeListener listener = event -> {
        if(Objects.equals(event.getProperty(), EMFPatternLanguagePreferenceConstants.P_ENABLE_VQL_CODEMINING)) {
            isMiningEnabled = (Boolean) event.getNewValue();
        }
    };
    private final IPreferenceStore preferenceStore;
    
    public EMFPatternLanguageCodeMiningProvider() {
        preferenceStore = EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore();
        preferenceStore.addPropertyChangeListener(listener);
        isMiningEnabled = preferenceStore.getBoolean(EMFPatternLanguagePreferenceConstants.P_ENABLE_VQL_CODEMINING);
    }
    
    @Override
	protected void createCodeMinings(IDocument document, XtextResource resource, CancelIndicator indicator,
		IAcceptor<? super ICodeMining> acceptor) throws BadLocationException {
        if (!isMiningEnabled) {
            return;
        }
        
		// Only consider the main contents, not the inferred ones
		final TreeIterator<EObject> it = resource.getContents().get(0).eAllContents();
		while (it.hasNext()) {
		    final EObject obj = it.next();
		    if (obj instanceof Parameter) {
		        createCodeMinings((Parameter) obj, acceptor);
		    }
		    if (obj instanceof PatternCall) {
		        createCodeMinings((PatternCall) obj, acceptor);
		        it.prune();
		    }
		    if (indicator.isCanceled()) return;
		}
	}
    
    protected void createCodeMinings(PatternCall call,
            IAcceptor<? super ICodeMining> acceptor) throws BadLocationException {
        final RuleCall paramCall1 = grammar.getPatternCallAccess().getParametersValueReferenceParserRuleCall_4_0_0();
        final RuleCall paramCall2 = grammar.getPatternCallAccess().getParametersValueReferenceParserRuleCall_4_1_1_0();
        
        final Pattern calledPattern = call.getPatternRef();
        if (calledPattern != null && !calledPattern.eIsProxy() &&
                call.getParameters().size() >= 2
                && call.getParameters().size() == calledPattern.getParameters().size()) {
            for (int i = 0; i < call.getParameters().size(); i++) {
                final ValueReference actualValue = call.getParameters().get(i);
                final Variable parameter = calledPattern.getParameters().get(i);
                
                StreamSupport.stream(NodeModelUtils.findActualNodeFor(actualValue).getAsTreeIterable().spliterator(), false)
                .filter(n -> Objects.equals(paramCall1, n.getGrammarElement()) || Objects.equals(paramCall2, n.getGrammarElement()))
                .findFirst()
                .ifPresent(child -> acceptor.accept(createNewLineContentCodeMining(child.getOffset(),
                        parameter.getName() + " -> ")));
            }
            
        }
    }
    
    protected void createCodeMinings(Parameter parameter,
            IAcceptor<? super ICodeMining> acceptor) throws BadLocationException {
        final RuleCall nameCall = grammar.getParameterAccess().getNameIDTerminalRuleCall_1_0();
        if (parameter.getType() == null) {
            final IInputKey inferredType = typeInferrer.getInferredType(parameter);
            if (inferredType == null) {
                return;
            }
            final ICompositeNode node = NodeModelUtils.findActualNodeFor(parameter);
            StreamSupport.stream(node.getAsTreeIterable().spliterator(), false)
                .filter(n -> Objects.equals(nameCall, n.getGrammarElement()))
                .findFirst()
                .ifPresent(child -> acceptor.accept(createNewLineContentCodeMining(child.getEndOffset(),
                            " : " + calculateTypeString(inferredType))));
            
        }
    }
    
    private String calculateTypeString(IInputKey input) {
        if (input instanceof EClassTransitiveInstancesKey) {
            return ((EClassTransitiveInstancesKey) input).getEmfKey().getName();
        } else if (input instanceof EDataTypeInSlotsKey) {
            EDataType datatype = ((EDataTypeInSlotsKey) input).getEmfKey();
            if (datatype instanceof EEnum) {
                return datatype.getName();
            } else {
                // In case of non-enum datatypes use corresponding Java type instead
                return "java " + typeSystem.getJavaClassName((EDataTypeInSlotsKey) input);
            }
        } else if (input instanceof JavaTransitiveInstancesKey) {
            return "java " + ((JavaTransitiveInstancesKey) input).getWrappedKey();
        }
        return null;
    }

    @Override
    public void dispose() {
        preferenceStore.removePropertyChangeListener(listener);
        super.dispose();
    }
}
