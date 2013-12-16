/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction;

import java.util.Map;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.IOperationCompiler;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.LeftInheritanceTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.runtime.rete.eval.CachedFunctionEvaluatorNode;
import org.eclipse.incquery.runtime.rete.eval.CachedPredicateEvaluatorNode;
import org.eclipse.incquery.runtime.rete.index.DualInputNode;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.index.IterableIndexer;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Library;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.remote.Address;
import org.eclipse.incquery.runtime.rete.single.EqualityFilterNode;
import org.eclipse.incquery.runtime.rete.single.InequalityFilterNode;
import org.eclipse.incquery.runtime.rete.single.TransitiveClosureNode;
import org.eclipse.incquery.runtime.rete.single.TrimmerNode;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * An operation compiler implementation for a Rete container.
 * 
 * @author Gabor Bergmann
 * 
 */
public class ReteContainerCompiler<PatternDescription>
		implements IOperationCompiler<PatternDescription, Address<? extends Receiver>>, Cloneable {

    protected Library library;
    protected ReteContainer targetContainer;
    protected Network reteNet;
    protected ReteBoundary<PatternDescription> boundary;
    protected ReteEngine<PatternDescription> engine;
    protected boolean headAttached = false;
    
    // only if provided by putOnTab
    protected PatternDescription pattern = null;
    protected IPatternMatcherContext context = null;

    protected void mapPlan(SubPlan plan, Address<? extends Supplier> handle) {
        boundary.mapPlanToAddress(plan, handle);
    }
    
    protected Address<? extends Supplier> getHandle(SubPlan plan) {
        return boundary.getAddress(plan);
    }
    
    /**
     * Constructs the builder attached to a specified container. Prerequisite: engine has its network and boundary
     * fields initialized.
     * 
     * @param targetContainer
     */
    public ReteContainerCompiler(ReteEngine<PatternDescription> engine, ReteContainer targetContainer) {
        super();
        this.engine = engine;
        this.reteNet = engine.getReteNet();
        this.boundary = engine.getBoundary();
        this.targetContainer = targetContainer;
        this.library = targetContainer.getLibrary();
        this.headAttached = false;
    }

    /**
     * Constructs the builder attached to the head container. Prerequisite: engine has its network and boundary fields
     * initialized
     */
    public ReteContainerCompiler(ReteEngine engine) {
        super();
        this.engine = engine;
        this.reteNet = engine.getReteNet();
        this.boundary = engine.getBoundary();
        this.targetContainer = reteNet.getHeadContainer();
        this.library = targetContainer.getLibrary();
        this.headAttached = true;
    }

    public void reinitialize() {
        this.reteNet = engine.getReteNet();
        this.boundary = engine.getBoundary();
        this.targetContainer = headAttached ? reteNet.getHeadContainer() : reteNet.getNextContainer();
        this.library = targetContainer.getLibrary();
    }
    
    public void patternFinished(PatternDescription pattern, IPatternMatcherContext context, Address<? extends Receiver> collector) {
    	final NodeToPatternTraceInfo traceInfo = new NodeToPatternTraceInfo(pattern, context);
		collector.getContainer().resolveLocal(collector).assignTraceInfo(traceInfo);
    };

    public SubPlan buildTrimmer(SubPlan parentPlan, TupleMask trimMask, boolean enforceUniqueness) {
        Address<TrimmerNode> trimmer = library.accessTrimmerNode(getHandle(parentPlan), trimMask);
        final Tuple trimmedVariables = trimMask.transform(parentPlan.getVariablesTuple());
        Address<? extends Supplier> resultNode;
        if (enforceUniqueness) {
        	resultNode = library.accessUniquenessEnforcerNode(trimmer, trimmedVariables.getSize());
        } else {
        	resultNode = trimmer;
        }
		return trace(new SubPlan(parentPlan, trimmedVariables), resultNode);
    }

    public void buildConnection(SubPlan parentPlan, Address<? extends Receiver> collector) {
        reteNet.connectRemoteNodes(getHandle(parentPlan), collector, true);
        boundary.registerParentPlanForReceiver(collector, parentPlan);
    }

    public SubPlan buildStartingPlan(Object[] constantValues, Object[] constantNames) {
        return trace(new SubPlan(new FlatTuple(constantNames)), library.accessConstantNode(boundary
                .wrapTuple(new FlatTuple(constantValues))));
    }

    public SubPlan buildEqualityChecker(SubPlan parentPlan, int[] indices) {
        Address<EqualityFilterNode> checker = library.accessEqualityFilterNode(getHandle(parentPlan), indices);
        return trace(new SubPlan(parentPlan), checker);
    }

    public SubPlan buildInjectivityChecker(SubPlan parentPlan, int subject, int[] inequalIndices) {
        Address<InequalityFilterNode> checker = library.accessInequalityFilterNode(getHandle(parentPlan), subject,
                new TupleMask(inequalIndices, parentPlan.getVariablesTuple().getSize()));
        return trace(new SubPlan(parentPlan), checker);
    }

    @Override
    public SubPlan buildTransitiveClosure(SubPlan parentPlan) {
        Address<TransitiveClosureNode> checker = library.accessTransitiveClosureNode(getHandle(parentPlan));
        return trace(new SubPlan(parentPlan), checker);
    }

    @Override
    public SubPlan patternCallPlan(Tuple nodes, Object supplierKey)
            throws QueryPlannerException {
        return trace(new SubPlan(nodes), boundary.accessProduction((PatternDescription)supplierKey));
    }

    public SubPlan transitiveInstantiationPlan(Tuple nodes) {
        return trace(new SubPlan(nodes), boundary.accessInstantiationTransitiveRoot());
    }

    public SubPlan directInstantiationPlan(Tuple nodes) {
        return trace(new SubPlan(nodes), boundary.accessInstantiationRoot());
    }

    public SubPlan transitiveGeneralizationPlan(Tuple nodes) {
        return trace(new SubPlan(nodes), boundary.accessGeneralizationTransitiveRoot());
    }

    public SubPlan directGeneralizationPlan(Tuple nodes) {
        return trace(new SubPlan(nodes), boundary.accessGeneralizationRoot());
    }

    public SubPlan transitiveContainmentPlan(Tuple nodes) {
        return trace(new SubPlan(nodes), boundary.accessContainmentTransitiveRoot());
    }

    public SubPlan directContainmentPlan(Tuple nodes) {
        return trace(new SubPlan(nodes), boundary.accessContainmentRoot());
    }

    public SubPlan binaryEdgeTypePlan(Tuple nodes, Object supplierKey) {
        return trace(new SubPlan(nodes), boundary.accessBinaryEdgeRoot(supplierKey));
    }

    public SubPlan ternaryEdgeTypePlan(Tuple nodes, Object supplierKey) {
        return trace(new SubPlan(nodes), boundary.accessTernaryEdgeRoot(supplierKey));
    }

    public SubPlan unaryTypePlan(Tuple nodes, Object supplierKey) {
        return trace(new SubPlan(nodes), boundary.accessUnaryRoot(supplierKey));
    }

    public SubPlan buildBetaNode(SubPlan primaryPlan,
            SubPlan sidePlan, TupleMask primaryMask, TupleMask sideMask,
            TupleMask complementer, boolean negative) {
        Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(getHandle(primaryPlan),
                primaryMask);
        Address<? extends Indexer> sideSlot = library.accessProjectionIndexer(getHandle(sidePlan), sideMask);

        if (negative) {
            Address<? extends DualInputNode> checker = library.accessExistenceNode(primarySlot, sideSlot, true);
            return trace(new SubPlan(primaryPlan), checker);
        } else {
            Address<? extends DualInputNode> checker = library.accessJoinNode(primarySlot, sideSlot, complementer);
            Tuple newCalibrationPattern = complementer.combine(primaryPlan.getVariablesTuple(),
                    sidePlan.getVariablesTuple(), Options.enableInheritance, true);
            return trace(new SubPlan(primaryPlan, sidePlan, newCalibrationPattern), checker);
        }
    }

    public SubPlan buildCounterBetaNode(SubPlan primaryPlan,
            SubPlan sidePlan, TupleMask primaryMask, TupleMask originalSideMask,
            TupleMask complementer, Object aggregateResultCalibrationElement) {
        Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(getHandle(primaryPlan),
                primaryMask);
        Address<? extends Indexer> sideSlot = library.accessCountOuterIndexer(getHandle(sidePlan), originalSideMask);

        Address<? extends DualInputNode> checker = library.accessJoinNode(primarySlot, sideSlot,
                TupleMask.selectSingle(originalSideMask.indices.length, originalSideMask.indices.length + 1));

        Object[] newCalibrationElement = { aggregateResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(primaryPlan.getVariablesTuple(), newCalibrationElement);

        SubPlan result = new SubPlan(primaryPlan, newCalibrationPattern);

        return trace(result, checker);
    }

    public SubPlan buildCountCheckBetaNode(SubPlan primaryPlan,
            SubPlan sidePlan, TupleMask primaryMask, TupleMask originalSideMask,
            int resultPositionInSignature) {
        Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(getHandle(primaryPlan),
                primaryMask);
        Address<? extends Indexer> sideSlot = library.accessCountOuterIdentityIndexer(getHandle(sidePlan),
                originalSideMask, resultPositionInSignature);

        Address<? extends DualInputNode> checker = library.accessJoinNode(primarySlot, sideSlot,
                TupleMask.empty(originalSideMask.indices.length + 1));

        Tuple newCalibrationPattern = primaryPlan.getVariablesTuple();

        SubPlan result = new SubPlan(primaryPlan, newCalibrationPattern);

        return trace(result, checker);
    }
    
    @Override
    public SubPlan buildPredicateChecker(IExpressionEvaluator evaluator, Map<String, Integer> tupleNameMap,
            SubPlan parentPlan) {
        CachedPredicateEvaluatorNode cpen = new CachedPredicateEvaluatorNode(targetContainer, engine, evaluator,
                tupleNameMap, parentPlan.getVariablesTuple().getSize());
        Address<CachedPredicateEvaluatorNode> checker = Address.of(cpen);

        reteNet.connectRemoteNodes(getHandle(parentPlan), checker, true);
        SubPlan result = new SubPlan(parentPlan);

        return trace(result, checker);

    }

    @Override
    public SubPlan buildFunctionEvaluator(IExpressionEvaluator evaluator, Map<String, Integer> tupleNameMap,
            SubPlan parentPlan, Object computedResultCalibrationElement) {
        CachedFunctionEvaluatorNode cfen = new CachedFunctionEvaluatorNode(targetContainer, engine, evaluator, tupleNameMap, parentPlan.getVariablesTuple().getSize());
        Address<CachedFunctionEvaluatorNode> computer = Address.of(cfen);
        
        reteNet.connectRemoteNodes(getHandle(parentPlan), computer, true);
        
        Object[] newCalibrationElement = { computedResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(parentPlan.getVariablesTuple(), newCalibrationElement);
        
        SubPlan result = new SubPlan(parentPlan,
                newCalibrationPattern);
        
        return trace(result, computer);
    }

    /**
     * @return trace(a buildable that potentially acts on a separate container
     */
    public IOperationCompiler<PatternDescription, Address<? extends Receiver>> getNextContainer() {
        return new ReteContainerCompiler<PatternDescription>(engine, reteNet.getNextContainer());
    }

    public Address<? extends Receiver> patternCollector(Object pattern) throws QueryPlannerException {
        return engine.getBoundary().createProductionInternal(pattern);
    }

    /**
     * No need to distinguish
     */
    public IOperationCompiler<PatternDescription, Address<? extends Receiver>> putOnTab(Object effort, IPatternMatcherContext effortContext) {
    	final ReteContainerCompiler<PatternDescription> patternSpecific;
    	try {
    		patternSpecific = (ReteContainerCompiler<PatternDescription>) this.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
    	patternSpecific.pattern = (PatternDescription) effort;
    	patternSpecific.context = effortContext;
        return patternSpecific;
    }
    
    private SubPlan trace(SubPlan parentPlan, final Address<? extends Supplier> address) {
    	NodeToPlanTraceInfo traceInfo = new NodeToPlanTraceInfo(parentPlan, pattern, context);
    	mapPlan(parentPlan, address);
    	address.getContainer().resolveLocal(address).assignTraceInfo(traceInfo);
    	return parentPlan;
    }

}
