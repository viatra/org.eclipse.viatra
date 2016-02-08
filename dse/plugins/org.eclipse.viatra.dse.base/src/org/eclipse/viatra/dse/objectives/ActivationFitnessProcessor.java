package org.eclipse.viatra.dse.objectives;

import org.eclipse.incquery.runtime.api.IPatternMatch;

public interface ActivationFitnessProcessor {
    public double process(IPatternMatch match);
}