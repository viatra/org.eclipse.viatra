package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.util.List;

import com.google.common.collect.Lists;

public class TransformationRule {
    private final String ruleName;
    private final boolean filtered;
    private final List<RuleActivation> activations;
    
    public TransformationRule(String ruleName, boolean filtered, List<RuleActivation> activations) {
        super();
        this.ruleName = ruleName;
        this.filtered = filtered;
        this.activations = activations;
    }


    public String getRuleName() {
        return ruleName;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public List<RuleActivation> getActivations() {
        return Lists.newArrayList(activations);
    }
    
    
}
