package org.eclipse.viatra.examples.uml.evm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.log4j.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.viatra.examples.uml.evm.queries.OnlyInheritedOperationsMatch;
import org.eclipse.viatra.examples.uml.evm.queries.OnlyInheritedOperationsMatcher;
import org.eclipse.viatra.examples.uml.evm.queries.PossibleSuperClassMatch;
import org.eclipse.viatra.examples.uml.evm.queries.PossibleSuperClassMatcher;
import org.eclipse.viatra.examples.uml.evm.queries.SuperClassMatcher;
import org.eclipse.viatra.examples.uml.evm.queries.util.OnlyInheritedOperationsProcessor;
import org.eclipse.viatra.examples.uml.evm.queries.util.PossibleSuperClassProcessor;
import org.eclipse.viatra.examples.uml.evm.queries.util.SuperClassProcessor;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.resolver.ScopedConflictSet;
import org.eclipse.viatra.transformation.evm.specific.ConflictResolvers;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.RuleEngines;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.viatra.transformation.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class UMLexampleForEVM {

    @Test
    public void RuleEngineExample() {

        final ResourceSet resourceSet = new ResourceSetImpl();
        final URI fileURI = URI.createPlatformPluginURI("org.eclipse.viatra.examples.uml.evm/testmodels/Testmodel.uml",
                false);
        resourceSet.getResource(fileURI, true);

        try {
            // create IncQueryEngine for the resource set
            final ViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resourceSet));
            // create rule engine over IncQueryEngine
            final RuleEngine ruleEngine = RuleEngines.createIncQueryRuleEngine(engine);
            // set logger level to debug to see activation life-cycle events
            ruleEngine.getLogger().setLevel(Level.DEBUG);
            // create context for execution
            final Context context = Context.create();

            // prepare rule specifications
            final RuleSpecification<PossibleSuperClassMatch> createGeneralization = getCreateGeneralizationRule();
            final RuleSpecification<OnlyInheritedOperationsMatch> createOperation = getCreateOperationRule();

            // add rule specifications to engine
            ruleEngine.addRule(createGeneralization);
            testFilteredRules(engine, ruleEngine, createGeneralization);

            ruleEngine.addRule(createOperation);

            // check rule applicability
            final Set<Activation<PossibleSuperClassMatch>> createClassesActivations = ruleEngine.getActivations(createGeneralization);
            if (!createClassesActivations.isEmpty()) {
                // fire activation of a given rule
                createClassesActivations.iterator().next().fire(context);
            }
            
            ruleEngine.setConflictResolver(ConflictResolvers.createFixedPriorityResolver());

            final ScopedConflictSet activationSet = ruleEngine.createScopedConflictSet(createGeneralization, createGeneralization.createEmptyFilter());
            assertTrue(activationSet.getNextActivation() != null);

            // check for any applicable rules
            while (!ruleEngine.getConflictingActivations().isEmpty()) {
                // fire next activation as long as possible
                ruleEngine.getNextActivation().fire(context);
            }

            assertTrue(activationSet.getNextActivation() == null);
            activationSet.dispose();

            // rules that are no longer needed can be removed
            ruleEngine.removeRule(createGeneralization);

            // rule engine manages the activations of the added rules until
            // disposed
            ruleEngine.dispose();

        } catch (final IncQueryException e) {
            e.printStackTrace();
        }

    }

    private void testFilteredRules(final ViatraQueryEngine engine, final RuleEngine ruleEngine,
            final RuleSpecification<PossibleSuperClassMatch> createGeneralization) throws IncQueryException {
        assertFalse(ruleEngine.addRule(createGeneralization));

        final PossibleSuperClassMatcher matcher = PossibleSuperClassMatcher.on(engine);
        final PossibleSuperClassMatch emptyMatch = matcher.newMatch(null, null);
        final PossibleSuperClassMatch arbitraryMatch = matcher.getOneArbitraryMatch();
        final EventFilter<PossibleSuperClassMatch> emptyFilter1 = Rules.newSingleMatchFilter(emptyMatch);
        final EventFilter<PossibleSuperClassMatch> emptyFilter2 = Rules.newSingleMatchFilter(emptyMatch);
        final EventFilter<PossibleSuperClassMatch> filter = Rules.newSingleMatchFilter(arbitraryMatch);
        final EventFilter<PossibleSuperClassMatch> filter2 = Rules.newSingleMatchFilter(arbitraryMatch);

        final EventFilter<IPatternMatch> eventFilter = new EventFilter<IPatternMatch>() {

            Class cl = arbitraryMatch.getCl();

            @Override
            public boolean isProcessable(final IPatternMatch eventAtom) {
                return eventAtom.get("cl").equals(cl);
            }
        };

        ruleEngine.addRule(createGeneralization, eventFilter);
        assertFalse(ruleEngine.getActivations(createGeneralization, eventFilter).isEmpty());

        assertTrue(ruleEngine.addRule(createGeneralization, emptyFilter1)); // empty filter different from no filter
        assertFalse(ruleEngine.addRule(createGeneralization, emptyFilter2));
        assertTrue(ruleEngine.addRule(createGeneralization, filter));
        assertFalse(ruleEngine.addRule(createGeneralization, filter2));
    }

    @Test
    public void ExecutionSchemaExample() {

        final ResourceSet resourceSet = new ResourceSetImpl();
        final URI fileURI = URI.createPlatformPluginURI("org.eclipse.viatra.examples.uml.evm/testmodels/Testmodel.uml",
                false);
        resourceSet.getResource(fileURI, true);

        try {
            // create IncQueryEngine for the resource set
            final ViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resourceSet));
            // use IQBase update callback for scheduling execution
            final UpdateCompleteBasedSchedulerFactory schedulerFactory = Schedulers.getQueryEngineSchedulerFactory(engine);
            // create execution schema over IncQueryEngine
            final ExecutionSchema executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(engine, schedulerFactory);
            // set logger level to debug to see activation life-cycle events
            executionSchema.getLogger().setLevel(Level.DEBUG);


            // prepare rule specifications
            final RuleSpecification<PossibleSuperClassMatch> createGeneralization = getCreateGeneralizationRule();
            final RuleSpecification<OnlyInheritedOperationsMatch> createOperation = getCreateOperationRule();

            // add rule specifications to engine
            executionSchema.addRule(createGeneralization);
            testFilteredRules(engine, executionSchema, createGeneralization);

            executionSchema.addRule(createOperation);


            // execution schema waits for a scheduling to fire activations
            // we trigger this by removing one generalization at random
            SuperClassMatcher.querySpecification().getMatcher(engine).forOneArbitraryMatch(new SuperClassProcessor() {

                @Override
                public void process(final Class sub, final Class sup) {
                    sub.getGeneralizations().remove(0);
                }
            });

            // rules that are no longer needed can be removed
            executionSchema.removeRule(createGeneralization);

            // execution schema manages and fires the activations of the added
            // rules until disposed
            executionSchema.dispose();

        } catch (final IncQueryException e) {
            e.printStackTrace();
        }

    }

    private RuleSpecification<PossibleSuperClassMatch> getCreateGeneralizationRule() throws IncQueryException {
        // the job specifies what to do when an activation is fired in the given
        // state
        final Job<PossibleSuperClassMatch> job = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, new PossibleSuperClassProcessor() {
            @Override
            public void process(final Class cl, final Class sup) {
                System.out.println("Found cl " + cl + " without superclass");
                final Generalization generalization = UMLFactory.eINSTANCE.createGeneralization();
                generalization.setGeneral(sup);
                generalization.setSpecific(cl);
            }
        });
        // the life-cycle determines how events affect the state of activations
        final ActivationLifeCycle lifecycle = Lifecycles.getDefault(false, false);
        // the factory is used to initialize the matcher for the precondition
        final IQuerySpecification<PossibleSuperClassMatcher> factory = PossibleSuperClassMatcher.querySpecification();
        // the rule specification is a model-independent definition that can be
        // used to instantiate a rule
        final RuleSpecification<PossibleSuperClassMatch> spec = Rules.newMatcherRuleSpecification(factory, lifecycle, ImmutableSet.of(job));
        return spec;
    }

    private RuleSpecification<OnlyInheritedOperationsMatch> getCreateOperationRule() throws IncQueryException {
        final Job<OnlyInheritedOperationsMatch> job = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, new OnlyInheritedOperationsProcessor() {
            @Override
            public void process(final Class cl) {
                System.out.println("Found class " + cl + " without operation");
                final Operation operation = UMLFactory.eINSTANCE.createOperation();
                operation.setName("newOp");
                operation.setClass_(cl);
            }
        });

        final ActivationLifeCycle lifecycle = Lifecycles.getDefault(false, false);
        final IQuerySpecification<OnlyInheritedOperationsMatcher> factory = OnlyInheritedOperationsMatcher.querySpecification();
        final RuleSpecification<OnlyInheritedOperationsMatch> spec = Rules.newMatcherRuleSpecification(factory, lifecycle, ImmutableSet.of(job));
        return spec;
    }



}
