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
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.impl.pojo.ConcurrentDesignSpace;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.guidance.Predicate;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.statecode.graph.impl.GraphHash;
import org.eclipse.viatra.dse.statecoding.simple.SimpleStateCoderFactory;
import org.eclipse.viatra.dse.util.EMFHelper;
import org.eclipse.viatra.dse.util.EMFHelper.MetaModelElements;
import org.eclipse.viatra.dse.visualizer.IDesignSpaceVisualizer;

/**
 * <p>
 * The {@link DesignSpaceExplorer} is the main API of the <b>Design Space Exploration</b> engine.
 * </p>
 * 
 * <p>
 * To parameterize the algorithm one must use the following methods after instantiating:
 * <ul>
 * <li>{@link #setInitialModel(EObject)} or it's overloads to set the starting model.</li>
 * <li>
 * {@link #addTransformationRule(DSETransformationRule)} to define the transformations.</li>
 * <li> {@link #addGlobalConstraint(IGlobalConstraint)} to ensure some patterns on the trajectory's steps.</li>
 * <li {@link #addObjective(IObjective)} to define the objective functions.</li>
 * <li> {@link #startExploration(LocalSearchStrategyBase) to start an exploration with the given exploration strategy.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Designs Space Exploration</b> is the process of finding a sequence (or sequences) of predefined transformation
 * rules ("transitions") that, if applied in order on the starting model, results in a new model state that fulfills
 * the hard constraints defined by the objectives.
 * </p>
 * 
 * <p>
 * An extension to this paradigm is the introduction of global constraints, which guarantees, that no sequence will be
 * returned, which if executed, results in an intermediate model state that violate the specified global constraints,
 * including the final state. (or in reverse, they all fulfill the negative of the given constraints). You can add
 * constraints by invoking {@link #addGlobalConstraint(IGlobalConstraint)}.
 * </p>
 * 
 * <p>
 * There are several built-in exploration strategies which you can find by using the static methods of Strategies helper class. 
 * </p>
 * 
 * @author Andras Szabolcs Nagy & Miklos Foldenyi
 * 
 */
public class DesignSpaceExplorer {

    private Notifier model;

    private GlobalContext globalContext = new GlobalContext();

    private final Logger logger = Logger.getLogger(this.getClass());

    private List<Predicate> predicates;

    private Set<EPackage> metaModelPackages = new HashSet<EPackage>();

    private static final String MODEL_NOT_YET_GIVEN = "The starting model is not given yet. Please call the setInitialModel method first.";

    private static final long SLEEP_INTERVAL = 1000;

    private Guidance guidance;

    /**
     * <p>
     * Creates a {@link DesignSpaceExplorer} object that is able to execute a design space exploration process.
     * </p>
     * 
     * <p>
     * By default the design space implementation is the POJO based {@link ConcurrentDesignSpace}. You can provide your
     * own custom design space implementation by implementing the {@link IDesignSpace} interface and passing it to the
     * {@link #setDesignspace(IDesignSpace)} method.
     * </p>
     * 
     * <p>
     * By default the state coder used is the generic (not meta-model specific) {@link GraphHash}. You can provide your
     * custom state coder by implementing the {@link IStateCoderFactory} and {@link IStateCoder} interfaces, and passing
     * the former to the {@link #setStateCoderFactory(IStateCoderFactory)} method.
     * 
     */
    public DesignSpaceExplorer() {
        setDesignspace(new ConcurrentDesignSpace());
    }

    /**
     * Adds a metamodel in the form of {@link EPackage}, which is needed for certain guidance.
     * 
     * @param metaModelPackage
     */
    public void addMetaModelPackage(EPackage metaModelPackage) {
        metaModelPackages.add(metaModelPackage);
    }

    /**
     * Defines the starting model of the algorithm, and whether it is supposed to be used to execute the DSE process or
     * it should be cloned. Please note that in multithreaded mode any subsequent threads will be working on cloned
     * models.
     * 
     * @param model
     *            The root object of the EMF model.
     * @param deepCopyModel
     *            If it is set to true, the algorithm will run in cloned model.
     */
    public void setInitialModel(Notifier model, boolean deepCopyModel) {

        Notifier copiedEObject = model;

        if (deepCopyModel) {
            copiedEObject = EMFHelper.clone(model);
        }

        this.model = copiedEObject;
    }

    /**
     * Defines the starting model of the algorithm.
     * 
     * @param model
     *            The root object of the EMF model. It will be cloned on default.
     */
    public void setInitialModel(Notifier model) {
        setInitialModel(model, true);
    }

    /**
     * Adds a {@link DSETransformationRule}.
     * 
     * @param rule
     *            The transformationRule.
     */
    public void addTransformationRule(DSETransformationRule<?, ?> rule) {
        checkArgument(rule != null);
        for (DSETransformationRule<?, ?> rule2 : globalContext.getTransformations()) {
            if (rule.getPrecondition().equals(rule2.getPrecondition())) {
                throw new DSEException(
                        "Two transformation rule ("
                                + rule.getName()
                                + "; "
                                + rule2.getName()
                                + ") uses the same LHS IncQuery pattern ("
                                + rule.getPrecondition().getFullyQualifiedName()
                                + "), which may lead to hash collision."
                                + " Please wrap the pattern with an other pattern with the 'find' keyword (or duplicate the code), and use that for one of the rules LHS.");
            }
        }

        globalContext.getTransformations().add(rule);
    }

    /**
     * Adds a global constraint to the exploration process. Please see the {@link IGlobalConstraint} interface and its
     * implementations for details.
     * 
     * @param constraint
     *            The global constraint.
     * @see IGlobalConstraint
     */
    public void addGlobalConstraint(IGlobalConstraint constraint) {
        globalContext.getGlobalConstraints().add(constraint);
    }

    /**
     * Adds an objective the the exploration process. Please see the {@link IObjective} interface and its
     * implementations for details.
     * 
     * @param objective
     *            The objective.
     * @see IObjective
     */
    public void addObjective(IObjective objective) {
        for (IObjective o : globalContext.getObjectives()) {
            if (o.getName().equals(objective.getName())) {
                throw new DSEException("Two objectives with the same name cannot be registered:" + o.getName());
            }
        }
        globalContext.getObjectives().add(objective);
    }

    /**
     * Sets a {@link IStateCoderFactory} for which will be used for creating {@link IStateCoder}s.
     * 
     * @param serializerFactory
     *            The factory.
     * @deprecated Use {@link DesignSpaceExplorer#setStateCoderFactory(IStateCoderFactory)}
     */
    @Deprecated
    public final void setSerializerFactory(IStateCoderFactory serializerFactory) {
        setStateCoderFactory(serializerFactory);
    }

    /**
     * Sets a {@link IStateCoderFactory} for which will be used for creating {@link IStateCoder}s.
     * 
     * @param stateCoderFactory
     *            The factory.
     */
    public final void setStateCoderFactory(IStateCoderFactory stateCoderFactory) {
        globalContext.setStateCoderFactory(stateCoderFactory);
    }

    @Deprecated
    public void setGuidance(Guidance guidance) {
        this.guidance = guidance;
    }

    @Deprecated
    public void setPredicatesForOcVectorResolving(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    /**
     * Defines the maximum processing threads that the design space exploration can use. Note, that this is only
     * limiting the threads doing the actual calculation. By default this value will be set to the number of logical
     * processors (including HyperThreading) in the computer, reported by {@link Runtime#availableProcessors()}.
     * 
     * @param maxNumberOfThreads
     *            The number of maximum processing threads available to the design space exploration process.
     */
    public void setMaxNumberOfThreads(int maxNumberOfThreads) {
        globalContext.getThreadPool().setMaximumPoolSize(maxNumberOfThreads);
    }

    /**
     * Sets the {@link IDesignSpace} implementation that is to be used during the design space exploration process. By
     * default, the {@link ConcurrentDesignSpace} implementation is used.
     * 
     * @param designspace
     *            The {@link IDesignSpace} implementation.
     */
    public final void setDesignspace(IDesignSpace designspace) {
        globalContext.setDesignSpace(designspace);
    }

    /**
     * Sets the solution store. Please see the {@link ISolutionStore} interface and its implementations for details.
     * 
     * @param solutionStore
     *            The {@link ISolutionStore} implementation.
     */
    public void setSolutionStore(ISolutionStore solutionStore) {
        globalContext.setSolutionStore(solutionStore);
    }

    /**
     * Sets the solution store for strategies using the new strategy API. Please see the {@link SolutionStore} for details.
     * 
     * @param solutionStore
     *            The parameterized {@link SolutionStore} implementation.
     */
    public void setSolutionStore(SolutionStore solutionStore) {
        globalContext.setSolutionStore2(solutionStore);
    }

    /**
     * Starts the design space exploration. It returns only when the strategy decides to stop the execution.
     * 
     * @param strategy
     *            The strategy of the exploration.
     */
    public void startExploration(IStrategy strategy) {
        startExploration(strategy, true, -1);
    }

    /**
     * Starts the design space exploration asynchronously. Completion of the process can be verified by calling
     * {@link DesignSpaceExplorer#isDone()}.
     * 
     * @param strategy
     *            The strategy of the exploration.
     */
    public void startExplorationAsync(IStrategy strategy) {
        startExploration(strategy, false, -1);
    }

    /**
     * Starts the design space exploration with a timeout. It returns only when the strategy decides to stop the
     * execution or the given timeout is elapsed.
     * 
     * @param strategy
     *            The strategy of the exploration.
     * @param timeout
     *            The number of milliseconds before the exploration is forced to stop.
     * @return Returns true if the exploration stopped by the timeout.
     */
    public boolean startExplorationWithTimeout(IStrategy strategy, long timeout) {
        return startExploration(strategy, true, timeout);
    }

    /**
     * Starts the design space exploration asynchronously with a timeout. Completion of the process can be verified by
     * calling {@link DesignSpaceExplorer#isDone()}.
     * 
     * @param strategy
     *            The strategy of the exploration.
     * @param timeout
     *            The number of milliseconds before the exploration is forced to stop.
     * @return Returns true if the exploration stopped by the timeout.
     */
    public boolean startExplorationAsyncWithTimeout(IStrategy strategy, long timeout) {
        return startExploration(strategy, false, timeout);
    }

    /**
     * Starts the design space exploration. If {@code waitForTermination} is true, then it returns only when the
     * strategy decides to stop the execution or there was a timeout, otherwise when the exploration process is started
     * it returns immediately. In this case, process completion can be verified by calling
     * {@link DesignSpaceExplorer#isDone()}.
     * 
     * @param strategy
     *            The strategy of the exploration.
     * @param waitForTermination
     *            True if the method must wait for the engine to stop, i.e. whether to start synchronously.
     * @param timeout
     *            The number of milliseconds before the exploration is forced to stop.
     * @return Returns true if the exploration stopped by the timeout.
     */
    public boolean startExploration(IStrategy strategy, boolean waitForTermination, final long timeout) {
        initExploration(strategy);

        Timer timer = new Timer();
        final AtomicBoolean wasTimeout = new AtomicBoolean(false);

        if (timeout > 0) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    logger.debug("Timeout, stopping threads...");
                    globalContext.stopAllThreads();
                    wasTimeout.set(true);
                }
            };
            timer.schedule(timerTask, timeout);
        }

        if (waitForTermination) {
            do {
                try {
                    Thread.sleep(SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                }

                if (globalContext.isDone()) {
                    timer.cancel();
                    logger.debug("Design space exploration has finished.");
                    return wasTimeout.get();
                }
            } while (true);
        } else {
            logger.debug("Design space exploration started asynchronously.");
        }

        return wasTimeout.get();

    }

    private void initExploration(IStrategy strategy) {
        checkArgument(model != null, MODEL_NOT_YET_GIVEN);
        checkArgument(strategy != null, "A strategy must be given. Use the Strategies helper class.");
        checkState(!globalContext.getTransformations().isEmpty(),
                "At least one transformation rule must be added to start the exploration.");

        if (globalContext.getStateCoderFactory() == null) {
            if (getMetaModelPackages() == null || getMetaModelPackages().isEmpty()) {
                throw new DSEException("Cannot initialize state coder."
                        + " Please specifiy the EPackages your model uses with addMetaModelPackage(EPackage)");
            }
            globalContext.setStateCoderFactory(new SimpleStateCoderFactory(getMetaModelPackages()));
        }

        if (guidance != null) {

            guidance.setRules(globalContext.getTransformations());
            // guidance.setConstraints(globalContext.getConstraints());
            // guidance.setGoalPatterns(globalContext.getGoalPatterns());

            // create rule dependency graph
            guidance.resolveDependencyGraph();

            if (guidance.getOccuranceVectorResolver() != null) {
                MetaModelElements metaModelElements = EMFHelper.getAllMetaModelElements(metaModelPackages);
                List<EModelElement> classesAndReferences = new ArrayList<EModelElement>(metaModelElements.attributes);
                classesAndReferences.addAll(metaModelElements.references);
                Map<EModelElement, Integer> initialMarking = Guidance
                        .getInitialMarking(model, classesAndReferences);
                guidance.resolveOccurrenceVector(classesAndReferences, initialMarking, predicates);
            }
        }

        logger.debug("DesignSpaceExplorer started exploration.");

        // Create main thread with given model, without cloning.
        ThreadContext threadContext = new ThreadContext(globalContext, strategy,
                EMFHelper.createEditingDomain(model), null, null);
        threadContext.setGuidance(guidance);

        globalContext.tryStartNewThread(threadContext, false);
    }

    /**
     * Returns all of the found {@link Solution}s, trajectories. Call it after
     * {@link DesignSpaceExplorer#startExploration()}. Calling this while the process is running returns the solutions
     * that have been found <b>so far</b>. The returned {@link Solution} objects may change internal state after they
     * have been returned, if a shorter trajectory has been found to the referred state.
     * 
     * @return The found solutions.
     */
    public Collection<Solution> getSolutions() {
        Collection<Solution> result = globalContext.getSolutionStore().getSolutions();
        if (result.isEmpty()) {
            return globalContext.getSolutionStore2().getSolutions();
        } else {
            return result;
        }
    }

    /**
     * Returns an arbitrary solution trajectory or null if the exploration failed to find any.
     * 
     * @return An arbitrary solution trajectory.
     */
    public SolutionTrajectory getArbitrarySolution() {
        Collection<Solution> solutions = getSolutions();
        if (solutions.isEmpty()) {
            return null;
        }
        return solutions.iterator().next().getArbitraryTrajectory();
    }

    /**
     * Returns the number of distinct states the exploration process has visited so far.
     * 
     * @return the number of distinct states.
     */
    public long getNumberOfStates() {
        return globalContext.getDesignSpace().getNumberOfStates();
    }

    /**
     * Returns the number of distinct transitions the exploration process has discovered (but not necessarily traversed)
     * so far.
     * 
     * @return the number of distinct transitions.
     */
    public long getNumberOfTransitions() {
        return globalContext.getDesignSpace().getNumberOfTransitions();
    }

    /**
     * Returns the {@link EPackage}s, which were registered with the
     * {@link DesignSpaceExplorer#addMetaModelPackage(EPackage)} method.
     * 
     * @return The set of meta model packages.
     */
    public Set<EPackage> getMetaModelPackages() {
        return metaModelPackages;
    }

    /**
     * Returns true if the {@link IExplorerThread strategy} decided to stop, and all the threads finished their work.
     * 
     * @return true if the process has finished, false otherwise.
     */
    public boolean isDone() {
        return globalContext.isDone();
    }

    /**
     * Returns the {@link GlobalContext} which holds the configurations such as rule, objectives, etc.
     * 
     * @return The global context.
     */
    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    /**
     * Registers a design space visualizer. Please see the corresponding interface {@link IDesignSpaceVisualizer}.
     * 
     * @see IDesignSpaceVisualizer
     * 
     * @param visualizer
     */
    public void addDesignSpaceVisulaizer(IDesignSpaceVisualizer visualizer) {
        globalContext.registerDesignSpaceVisualizer(visualizer);
    }

    public String toStringSolutions() {
        StringBuilder sb = new StringBuilder();
        Collection<Solution> solutions = getSolutions();
        sb.append("Number of solutions: ");
        sb.append(solutions.size());
        sb.append("\n");
        for (Solution solution : solutions) {
            sb.append("Solution: ");
            sb.append(solution.getStateCode());
            sb.append("\n");
            for (SolutionTrajectory trajectory : solution.getTrajectories()) {
                sb.append("  Trajectory: ");
                sb.append(trajectory.toPrettyString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @deprecated use toStringSolutions instead
     */
    @Deprecated
    public String prettyPrintSolutions() {
        return toStringSolutions();
    }

}
