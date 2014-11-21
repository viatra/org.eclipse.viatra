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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.dse.api.strategy.Strategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategyFactory;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.impl.pojo.ConcurrentDesignSpace;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.guidance.Predicate;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;
import org.eclipse.viatra.dse.statecode.graph.GraphHasherFactory;
import org.eclipse.viatra.dse.statecode.graph.impl.GraphHash;
import org.eclipse.viatra.dse.util.EMFHelper;

/**
 * <p>
 * The {@link DesignSpaceExplorer} is the main API of the <b>Design Space Exploration</b> engine.
 * </p>
 * 
 * <p>
 * To parameterize the algorithm one must use the following methods after instantiating:
 * <ul>
 * <li>{@link #setStartingModel(EObject)} or it's overloads to set the starting model.</li>
 * <li>
 * {@link #addTransformationRule(IncQueryMatcher, IMatchProcessor)} to define the transformations.</li>
 * <li> {@link #addConstraint(Set)} to ensure some patterns on the trajectory's steps.</li>
 * <li {@link #addGoalPattern(Set)} to set the goal patterns.</li>
 * </ul>
 * </p>
 * 
 * 
 * <p>
 * <b>Designs Space Exploration</b> is the process of finding a sequence (or sequences) of predefined transformation
 * rules ("transitions") that, if applied in order on the starting model, results in a new model state that fulfills
 * some predefined rules, referred to as "goal patterns". You can add goals by invoking
 * {@link #addGoalPattern(PatternWithCardinality)}
 * </p>
 * 
 * <p>
 * An extension to this paradigm is the introduction of "constraints", which guarantees, that no sequence will be
 * returned, which if executed, results in an intermediate model state that violate the specified rules ("constraints"),
 * including the final state. (or in reverse, they all fulfill the negative of the given constraints) You can add
 * constraints by invoking {@link #addConstraint(PatternWithCardinality)}.
 * </p>
 * 
 * <p>
 * Guidance?
 * </p>
 * 
 * @author Andras Szabolcs Nagy & Miklos Foldenyi
 * 
 */
public class DesignSpaceExplorer {

    private EObject modelRoot;

    private GlobalContext globalContext = new GlobalContext();

    private final Logger logger = Logger.getLogger(this.getClass());

    private List<Predicate> predicates;

    private Set<EPackage> metaModelPackages = new HashSet<EPackage>();

    private static final String MODEL_NOT_YET_GIVEN = "The starting model is not given yet. Please call the setStartingModel method first.";

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
     * By default the state serializer used is the generic (not meta-model specific) {@link GraphHash}. You can provide
     * your custom state serializer by implementing the {@link IStateSerializerFactory} and {@link IStateSerializer}
     * interfaces, and passing the former to the {@link #setSerializerFactory(IStateSerializerFactory)} method.
     * 
     */
    public DesignSpaceExplorer() {
        setSerializerFactory(new GraphHasherFactory());
        setDesignspace(new ConcurrentDesignSpace());
        // setDesignspace(new EMFDesignSpace());
    }

    public void addMetaModelPackage(EPackage metaModelPackage) {
        metaModelPackages.add(metaModelPackage);
    }

    /**
     * Defines the starting model of the algorithm, and whether it is supposed to be used to execute the DSE process or
     * it should be cloned. Please note that in multithreaded mode any subsequent threads will be working on cloned
     * models!
     * 
     * @param rootEObject
     *            The root object of the EMF model.
     * @param deepCopyModel
     *            If it is set to true, the algorithm will run in cloned model.
     */
    public void setStartingModel(EObject rootEObject, boolean deepCopyModel) {

        EObject copiedEObject = rootEObject;

        if (deepCopyModel) {
            copiedEObject = EMFHelper.clone(rootEObject);
        }

        this.modelRoot = copiedEObject;
    }

    /**
     * Defines the starting model of the algorithm.
     * 
     * @param rootEObject
     *            The root object of the EMF model. It will be cloned on default.
     */
    public void setStartingModel(EObject rootEObject) {
        setStartingModel(rootEObject, true);
    }

    /**
     * Adds a {@link TransformationRule}.
     * 
     * @param rule
     *            The transformationRule.
     */
    public <P extends IPatternMatch, M extends IncQueryMatcher<P>> void addTransformationRule(TransformationRule<P> rule) {
        checkArgument(rule != null);
        for (TransformationRule<? extends IPatternMatch> rule2 : globalContext.getTransformations()) {
            if (rule.getQuerySpecification().equals(rule2.getQuerySpecification())) {
                throw new DSEException(
                        "Two transformation rule ("
                                + rule.getName()
                                + "; "
                                + rule2.getName()
                                + ") uses the same LHS IncQuery pattern ("
                                + rule.getQuerySpecification().getFullyQualifiedName()
                                + "), which may lead to hash collision."
                                + " Please wrap the pattern with an other pattern with the 'find' keyword (or duplicate the code), and use that for one of the rules LHS.");
            }
        }

        globalContext.getTransformations().add(rule);
    }

    /**
     * Add the specified condition as a goal to the exploration process.
     * 
     * @param goalPattern
     *            the condition definiton.
     */
    public void addGoalPattern(PatternWithCardinality goalPattern) {
        globalContext.getGoalPatterns().add(goalPattern);
    }

    /**
     * Add the specified condition as a constraint to the exploration process.
     * 
     * @param constraint
     *            the condition definition.
     */
    public void addConstraint(PatternWithCardinality constraint) {
        globalContext.getConstraints().add(constraint);
    }

    /**
     * Sets a {@link IStateSerializerFactory} for which will be used for creating {@link IStateSerializer}s.
     * 
     * @param serializerFactory
     *            The factory.
     */
    public final void setSerializerFactory(IStateSerializerFactory serializerFactory) {
        globalContext.setStateSerializerFactory(serializerFactory);
    }

    public void setGuidance(Guidance guidance) {
        this.guidance = guidance;
    }

    public void setPredicatesForOcVectorResolving(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    /**
     * Defines the maximum processing threads that the design space exploration can use. Note, that this is only
     * limiting the threads doing the actual calculation. By default this value will be set to the number of processors
     * in the computer, reported by {@link Runtime#availableProcessors()}. Right now cores that are capable of parallel
     * execution <b>are not considered</b> (e.g. HyperThreading) and achieving <b>optimal performance may require manual
     * configuration</b> of this value.
     * 
     * @param maxNumberOfThreads
     *            the number of maximum processing threads available to the design space exploration process.
     */
    public void setMaxNumberOfThreads(int maxNumberOfThreads) {
        globalContext.getThreadPool().setMaximumPoolSize(maxNumberOfThreads);
    }

    /**
     * Sets the {@link IDesignSpace} implementation that is to be used during the design space exploration process. By
     * default, the {@link ConcurrentDesignSpace} implementation is used.
     * 
     * @param designspace
     *            the {@link IDesignSpace} implementation.
     */
    public final void setDesignspace(IDesignSpace designspace) {
        globalContext.setDesignSpace(designspace);
    }

    public void setSolutionStore(ISolutionStore solutionStore) {
        globalContext.setSolutionStore(solutionStore);
    }

    public void setStrategyFactory(IStrategyFactory factory) {
        globalContext.setStrategyFactory(factory);
    }

    public void setSharedObject(Object sharedObject) {
        globalContext.setSharedObject(sharedObject);
    }

    /**
     * Starts the design space exploration. If {@code waitForTermination} is true, then it returns only when the
     * strategy decides to stop the execution, otherwise when the exploration process is started it returns immediately.
     * In this case, process completion can be verified by calling {@link DesignSpaceExplorer#isDone()}.
     * 
     * @param strategyBase
     *            The strategy of the exploration.
     * @param waitForTermination
     *            True if the method must wait for the engine to stop.
     * @throws DSEException
     *             On any execution error, a {@link DSEException} is thrown. It's is a descendant of
     *             {@link RuntimeException}, so it may be left unchecked.
     */
    public void startExploration(Strategy strategyBase, boolean waitForTermination) throws DSEException {
        initExploration(strategyBase);

        // wait until all threads exit
        while (waitForTermination) {
            if (globalContext.isDone()) {
                logger.info("DesignSpaceExplorer finished.");
                return;
            }
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
            }
        }

        logger.info("DesignSpaceExplorer working in detached mode.");
    }

    /**
     * Starts the design space exploration and then sleeps {@code waitInMilliseconds} millisecond. After that it stops
     * the execution which can be a few millis long and returns.
     * 
     * @param strategyBase
     *            The strategy of the exploration.
     * @param waitInMilliseconds
     *            The number of milliseconds the method must wait for stopping the exploration.
     * @throws DSEException
     *             On any execution error, a {@link DSEException} is thrown. It's is a descendant of
     *             {@link RuntimeException}, so it may be left unchecked.
     */
    public void startExploration(Strategy strategyBase, int waitInMilliseconds) throws DSEException {
        initExploration(strategyBase);

        try {
            Thread.sleep(waitInMilliseconds);
        } catch (InterruptedException e) {
        }

        logger.info("Stopping threads...");

        globalContext.stopAllThreads();

        // wait until all threads exit
        do {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }

            if (globalContext.isDone()) {
                logger.info("DesignSpaceExplorer finished.");
                return;
            }
        } while (true);
    }

    private void initExploration(Strategy strategyBase) {
        checkArgument(modelRoot != null, MODEL_NOT_YET_GIVEN);
        checkArgument(strategyBase != null, "A strategy must be given. Use the Strategies helper class.");
        checkState(!globalContext.getTransformations().isEmpty(),
                "At least one transformation rule must be added to start the exploration.");

        if (guidance != null) {

            guidance.setRules(globalContext.getTransformations());
            guidance.setConstraints(globalContext.getConstraints());
            guidance.setGoalPatterns(globalContext.getGoalPatterns());

            // create rule dependency graph
            guidance.resolveDependencyGraph();

            if (guidance.getOccuranceVectorResolver() != null) {
                List<EModelElement> classesAndReferences = EMFHelper.getClassesAndReferences(metaModelPackages);
                Map<EModelElement, Integer> initialMarking = getInitialMarking(modelRoot, classesAndReferences);
                guidance.resolveOccurrenceVector(classesAndReferences, initialMarking, predicates);
            }
        }

        logger.info("DesignSpaceExplorer started exploration.");

        // Create main thread with given model, without cloning.
        ThreadContext threadContext = new ThreadContext(globalContext, strategyBase,
                EMFHelper.createEditingDomain(modelRoot), null, null);
        threadContext.setGuidance(guidance);

        globalContext.tryStartNewThread(threadContext, false);
    }

    public void resetForNewExlporation() {
        globalContext.reset();
    }

    private void processEObject(Map<EModelElement, Integer> initialMarking, EObject eObject) {

        // increment number of objects
        EClass eClass = eObject.eClass();
        Integer i = initialMarking.get(eClass);
        if (i == null) {
            throw new DSEException(
                    "The class "
                            + eClass.getName()
                            + " not found in the given meta models. Maybe you missed to call addMetaModelPackage with this parameter: "
                            + eClass.getEPackage().getNsURI());
        }
        initialMarking.put(eClass, i + 1);
        for (EClass superType : eClass.getEAllSuperTypes()) {
            initialMarking.put(superType, initialMarking.get(superType) + 1);
        }

        // increment number of references
        for (EReference eReference : eClass.getEReferences()) {
            if (!(eReference.isContainment() || eReference.isContainer())) {
                Object object = eObject.eGet(eReference);
                if (object != null) {
                    Integer i2 = initialMarking.get(eReference);
                    if (object instanceof EList<?>) {
                        i2 = i2 + ((EList<?>) object).size();
                    } else {
                        i2 = i2 + 1;
                    }
                    initialMarking.put(eReference, i2);
                }
            }
        }
    }

    private Map<EModelElement, Integer> getInitialMarking(EObject rootEObject,
            List<? extends EModelElement> classesAndReferences) {

        // init initialMarking (result map)
        HashMap<EModelElement, Integer> initialMarking = new HashMap<EModelElement, Integer>();
        for (EModelElement element : classesAndReferences) {
            initialMarking.put(element, 0);
        }

        // process instance model
        processEObject(initialMarking, rootEObject);
        TreeIterator<EObject> allContents = rootEObject.eAllContents();
        while (allContents.hasNext()) {
            EObject eObject = allContents.next();
            processEObject(initialMarking, eObject);

        }

        return initialMarking;
    }

    /**
     * Returns all of the found {@link Solution}s, trajectories. Call it after
     * {@link DesignSpaceExplorer#startExploration()}. Calling this while the process is running returns the solutions
     * that have been found <b>so far</b>. The returned {@link Solution} objects may change internal state after they
     * have been returned, if a shorter trajectory has been found to the referred state.
     * 
     * @return The found solutions.
     */
    public Collection<Solution> getAllSolutions() {
        return globalContext.getSolutionStore().getSolutions();
    }

    private int lastSolutions = 0;

    public Collection<Solution> getAllSolutions(int waitForTheNextXSolutions, int waitingIntervalInMilisec) {

        while (globalContext.getSolutionStore().getSolutions().size() < lastSolutions + waitForTheNextXSolutions) {
            try {
                Thread.sleep(waitingIntervalInMilisec);
            } catch (InterruptedException e) {

            }
        }

        lastSolutions += waitForTheNextXSolutions;

        return globalContext.getSolutionStore().getSolutions();
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

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

}
