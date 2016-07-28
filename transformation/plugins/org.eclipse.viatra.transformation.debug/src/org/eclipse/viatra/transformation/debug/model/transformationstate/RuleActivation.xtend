package org.eclipse.viatra.transformation.debug.model.transformationstate

import com.google.common.collect.Lists
import java.util.List
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace

class RuleActivation {
    final TransformationState transformationState
    final ActivationTrace trace
    final boolean nextActivation
    final String state
    final String ruleName
    final List<ActivationParameter> paremeters

    new(ActivationTrace trace, boolean nextActivation, String state, String ruleName,
        List<ActivationParameter> paremeters, TransformationState transformationState) {
        super()
        this.trace = trace
        this.nextActivation = nextActivation
        this.state = state
        this.ruleName = ruleName
        this.paremeters = paremeters
        this.transformationState = transformationState
    }

    def ActivationTrace getTrace() {
        return trace
    }

    def boolean isNextActivation() {
        return nextActivation
    }

    def String getState() {
        return state
    }

    def String getRuleName() {
        return ruleName
    }

    def List<ActivationParameter> getParameters() {
        return Lists.newArrayList(paremeters)
    }
    
     def TransformationState getTransformationState() {
        return transformationState
    }

    override String toString() {
        val adapterFactory = new ReflectiveItemProviderAdapterFactory()
        val adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory)
        
        '''Activation - State: «state» - Parameters: «FOR ActivationParameter param : parameters SEPARATOR ','» «param.name» : «adapterFactoryLabelProvider.getText(param.value)»«ENDFOR»'''
    }
}
