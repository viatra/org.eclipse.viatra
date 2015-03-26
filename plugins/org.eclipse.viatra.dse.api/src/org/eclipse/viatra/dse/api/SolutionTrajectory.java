/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

import com.google.common.util.concurrent.UncheckedExecutionException;

public class SolutionTrajectory {

    private final List<Object> transitionIds;
    private final List<DSETransformationRule<?, ?>> transformationRules;
    private final IStateSerializerFactory stateSerializerFactory;
    private Map<String, Double> fitness;

    private IncQueryEngine engine;
    private EObject rootEObject;
    private IStateSerializer stateSerializer;

    private int currentIndex;

    public SolutionTrajectory(final List<Object> transitionIds,
            final List<DSETransformationRule<?, ?>> transformationRules,
            final IStateSerializerFactory stateSerializerFactory) {
        checkNotNull(transformationRules, "Parameter transformationRules cannot be null!");
        checkNotNull(stateSerializerFactory, "Parameter stateSerializerFactory cannot be null!");
        checkNotNull(transitionIds, "Parameter activations cannot be null!");
        checkState(transformationRules.size() == transitionIds.size(),
                "The two List parameters must be the same in size.");

        this.transitionIds = transitionIds;
        this.transformationRules = transformationRules;
        this.stateSerializerFactory = stateSerializerFactory;
        currentIndex = 0;
    }

    public void setModel(Notifier modelRoot) throws IncQueryException {
        EMFScope scope = new EMFScope(modelRoot);
        this.engine = IncQueryEngine.on(scope);
        this.rootEObject = (EObject) modelRoot;
        stateSerializer = stateSerializerFactory.createStateSerializer(modelRoot);
    }

    /**
     * Transforms the given model into this {@link SolutionTrajectory}. To initialize the model call the
     * {@link SolutionTrajectory#setModel(EObject)} method.
     * 
     * @throws Exception
     *             If the activation to fire is not found. Possible problems: wrong model, bad state serializer.
     * @throws IncQueryException
     */
    public void doTransformation() throws IncQueryException {
        for (int i = currentIndex; i < transformationRules.size(); i++) {
            doNextTransformation(i);
        }
    }

    /**
     * Transforms the given model by one step to the solution (makes one step in the trajectory). To initialize the
     * model call the {@link SolutionTrajectory#setModel(EObject)} method.
     * 
     * @throws IncQueryException
     */
    public void doNextTransformation() throws IncQueryException {
        doNextTransformation(currentIndex++);
    }

    @SuppressWarnings("unchecked")
    private void doNextTransformation(int index) throws IncQueryException {
        checkArgument(rootEObject != null, "The model cannot be null!");

        // cast for the ".process(match)" method.
        DSETransformationRule<?, ?> tr = transformationRules.get(index);

        IncQueryMatcher<?> matcher = tr.getPrecondition().getMatcher(engine);

        boolean isActivationFound = false;
        for (IPatternMatch match : matcher.getAllMatches()) {
            Object matchHash = stateSerializer.serializePatternMatch(match);
            if (matchHash.equals(transitionIds.get(index))) {
                IMatchProcessor action = tr.getModelManipulation();
                action.process(match);
                isActivationFound = true;
                break;
            }
        }
        if (!isActivationFound) {
            throw new UncheckedExecutionException(
                    "Activation was not found for transformation! Possible cause: wrong model, bad state serializer.",
                    null);
        }
    }

    public List<Object> getActivations() {
        return transitionIds;
    }

    public List<DSETransformationRule<?,?>> getTransformationRules() {
        return transformationRules;
    }

    public IStateSerializerFactory getStateSerializerFactory() {
        return stateSerializerFactory;
    }

    public IncQueryEngine getEngine() {
        return engine;
    }

    public EObject getRootEObjcet() {
        return rootEObject;
    }

    public IStateSerializer getStateSerializer() {
        return stateSerializer;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTrajectoryLength() {
        return transitionIds.size();
    }

    public List<Object> getTransitionIds() {
        return transitionIds;
    }

    public Map<String, Double> getFitness() {
        return fitness;
    }

    public void setFitness(Map<String, Double> fitness) {
        this.fitness = fitness;
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (Object object : transitionIds) {
            sb .append(object.toString());
            sb.append(" | ");
        }
        sb.append("| Fitness: ");
        sb.append(fitness.toString());
        return sb.toString();
    }

}
