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

import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.runtime.rete.eval.AbstractEvaluator;
import org.eclipse.incquery.runtime.rete.eval.CachedFunctionEvaluatorNode;
import org.eclipse.incquery.runtime.rete.eval.PredicateEvaluatorNode;
import org.eclipse.incquery.runtime.rete.index.DualInputNode;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.index.IterableIndexer;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
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
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;
import org.eclipse.incquery.runtime.rete.tuple.LeftInheritanceTuple;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * The buildable interface of a rete container.
 * 
 * @author Bergmann Gábor
 * 
 */
public class ReteContainerBuildable<PatternDescription> implements
        Buildable<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>>,
        Cloneable {

    protected Library library;
    protected ReteContainer targetContainer;
    protected Network reteNet;
    protected ReteBoundary<PatternDescription> boundary;
    protected ReteEngine<PatternDescription> engine;
    protected boolean headAttached = false;
    
    // only if provided by putOnTab
    protected PatternDescription pattern = null;
    protected IPatternMatcherContext<PatternDescription> context = null;

    /**
     * Constructs the builder attached to a specified container. Prerequisite: engine has its network and boundary
     * fields initialized.
     * 
     * @param targetContainer
     */
    public ReteContainerBuildable(ReteEngine<PatternDescription> engine, ReteContainer targetContainer) {
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
    public ReteContainerBuildable(ReteEngine<PatternDescription> engine) {
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
    
    public void patternFinished(PatternDescription pattern, IPatternMatcherContext<PatternDescription> context, Address<? extends Receiver> collector) {
    	final NodeToPatternTraceInfo<PatternDescription> traceInfo = new NodeToPatternTraceInfo<PatternDescription>(pattern, context);
		collector.getContainer().resolveLocal(collector).assignTraceInfo(traceInfo);
    };

    public Stub<Address<? extends Supplier>> buildTrimmer(Stub<Address<? extends Supplier>> stub, TupleMask trimMask, boolean enforceUniqueness) {
        Address<TrimmerNode> trimmer = library.accessTrimmerNode(stub.getHandle(), trimMask);
        final Tuple trimmedVariables = trimMask.transform(stub.getVariablesTuple());
        Address<? extends Supplier> resultNode;
        if (enforceUniqueness) {
        	resultNode = library.accessUniquenessEnforcerNode(trimmer, trimmedVariables.getSize());
        } else {
        	resultNode = trimmer;
        }
		return trace(new Stub<Address<? extends Supplier>>(stub, trimmedVariables, resultNode));
    }

    public void buildConnection(Stub<Address<? extends Supplier>> stub, Address<? extends Receiver> collector) {
        reteNet.connectRemoteNodes(stub.getHandle(), collector, true);
        boundary.registerParentStubForReceiver(collector, stub);
    }

    public Stub<Address<? extends Supplier>> buildStartStub(Object[] constantValues, Object[] constantNames) {
        return trace(new Stub<Address<? extends Supplier>>(new FlatTuple(constantNames), library.accessConstantNode(boundary
                .wrapTuple(new FlatTuple(constantValues)))));
    }

    public Stub<Address<? extends Supplier>> buildEqualityChecker(Stub<Address<? extends Supplier>> stub, int[] indices) {
        Address<EqualityFilterNode> checker = library.accessEqualityFilterNode(stub.getHandle(), indices);
        return trace(new Stub<Address<? extends Supplier>>(stub, checker));
    }

    public Stub<Address<? extends Supplier>> buildInjectivityChecker(Stub<Address<? extends Supplier>> stub,
            int subject, int[] inequalIndices) {
        Address<InequalityFilterNode> checker = library.accessInequalityFilterNode(stub.getHandle(), subject,
                new TupleMask(inequalIndices, stub.getVariablesTuple().getSize()));
        return trace(new Stub<Address<? extends Supplier>>(stub, checker));
    }

    @Override
    public Stub<Address<? extends Supplier>> buildTransitiveClosure(Stub<Address<? extends Supplier>> stub) {
        Address<TransitiveClosureNode> checker = library.accessTransitiveClosureNode(stub.getHandle());
        return trace(new Stub<Address<? extends Supplier>>(stub, checker));
    }

    public Stub<Address<? extends Supplier>> patternCallStub(Tuple nodes, PatternDescription supplierKey)
            throws RetePatternBuildException {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessProduction(supplierKey)));
    }

    public Stub<Address<? extends Supplier>> instantiationTransitiveStub(Tuple nodes) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessInstantiationTransitiveRoot()));
    }

    public Stub<Address<? extends Supplier>> instantiationDirectStub(Tuple nodes) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessInstantiationRoot()));
    }

    public Stub<Address<? extends Supplier>> generalizationTransitiveStub(Tuple nodes) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessGeneralizationTransitiveRoot()));
    }

    public Stub<Address<? extends Supplier>> generalizationDirectStub(Tuple nodes) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessGeneralizationRoot()));
    }

    public Stub<Address<? extends Supplier>> containmentTransitiveStub(Tuple nodes) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessContainmentTransitiveRoot()));
    }

    public Stub<Address<? extends Supplier>> containmentDirectStub(Tuple nodes) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessContainmentRoot()));
    }

    public Stub<Address<? extends Supplier>> binaryEdgeTypeStub(Tuple nodes, Object supplierKey) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessBinaryEdgeRoot(supplierKey)));
    }

    public Stub<Address<? extends Supplier>> ternaryEdgeTypeStub(Tuple nodes, Object supplierKey) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessTernaryEdgeRoot(supplierKey)));
    }

    public Stub<Address<? extends Supplier>> unaryTypeStub(Tuple nodes, Object supplierKey) {
        return trace(new Stub<Address<? extends Supplier>>(nodes, boundary.accessUnaryRoot(supplierKey)));
    }

    public Stub<Address<? extends Supplier>> buildBetaNode(Stub<Address<? extends Supplier>> primaryStub,
            Stub<Address<? extends Supplier>> sideStub, TupleMask primaryMask, TupleMask sideMask,
            TupleMask complementer, boolean negative) {
        Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(primaryStub.getHandle(),
                primaryMask);
        Address<? extends Indexer> sideSlot = library.accessProjectionIndexer(sideStub.getHandle(), sideMask);

        if (negative) {
            Address<? extends DualInputNode> checker = library.accessExistenceNode(primarySlot, sideSlot, true);
            return trace(new Stub<Address<? extends Supplier>>(primaryStub, checker));
        } else {
            Address<? extends DualInputNode> checker = library.accessJoinNode(primarySlot, sideSlot, complementer);
            Tuple newCalibrationPattern = complementer.combine(primaryStub.getVariablesTuple(),
                    sideStub.getVariablesTuple(), Options.enableInheritance, true);
            return trace(new Stub<Address<? extends Supplier>>(primaryStub, sideStub, newCalibrationPattern, checker));
        }
    }

    public Stub<Address<? extends Supplier>> buildCounterBetaNode(Stub<Address<? extends Supplier>> primaryStub,
            Stub<Address<? extends Supplier>> sideStub, TupleMask primaryMask, TupleMask originalSideMask,
            TupleMask complementer, Object aggregateResultCalibrationElement) {
        Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(primaryStub.getHandle(),
                primaryMask);
        Address<? extends Indexer> sideSlot = library.accessCountOuterIndexer(sideStub.getHandle(), originalSideMask);

        Address<? extends DualInputNode> checker = library.accessJoinNode(primarySlot, sideSlot,
                TupleMask.selectSingle(originalSideMask.indices.length, originalSideMask.indices.length + 1));

        Object[] newCalibrationElement = { aggregateResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(primaryStub.getVariablesTuple(), newCalibrationElement);

        Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(primaryStub,
                newCalibrationPattern, checker);

        return trace(result);
    }

    public Stub<Address<? extends Supplier>> buildCountCheckBetaNode(Stub<Address<? extends Supplier>> primaryStub,
            Stub<Address<? extends Supplier>> sideStub, TupleMask primaryMask, TupleMask originalSideMask,
            int resultPositionInSignature) {
        Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(primaryStub.getHandle(),
                primaryMask);
        Address<? extends Indexer> sideSlot = library.accessCountOuterIdentityIndexer(sideStub.getHandle(),
                originalSideMask, resultPositionInSignature);

        Address<? extends DualInputNode> checker = library.accessJoinNode(primarySlot, sideSlot,
                TupleMask.empty(originalSideMask.indices.length + 1));

        Tuple newCalibrationPattern = primaryStub.getVariablesTuple();

        Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(primaryStub,
                newCalibrationPattern, checker);

        return trace(result);
    }

    public Stub<Address<? extends Supplier>> buildPredicateChecker(AbstractEvaluator evaluator, Integer rhsIndex,
            int[] affectedIndices, Stub<Address<? extends Supplier>> stub) {
        PredicateEvaluatorNode ten = new PredicateEvaluatorNode(engine, targetContainer, rhsIndex, affectedIndices,
                stub.getVariablesTuple().getSize(), evaluator);
        Address<PredicateEvaluatorNode> checker = Address.of(ten);
    	
//    	// TODO - eventually replace with newer version
//    	CachedPredicateEvaluatorNode cpen = new CachedPredicateEvaluatorNode(targetContainer, engine, evaluator, stub.getVariablesTuple().getSize());
//        Address<CachedPredicateEvaluatorNode> checker = Address.of(cpen);

        reteNet.connectRemoteNodes(stub.getHandle(), checker, true);

        Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(stub, checker);

        return trace(result);
    }
    @Override
	public Stub<Address<? extends Supplier>> buildFunctionEvaluator(AbstractEvaluator evaluator, 
            Stub<Address<? extends Supplier>> stub, Object computedResultCalibrationElement) {
    	CachedFunctionEvaluatorNode cfen = new CachedFunctionEvaluatorNode(targetContainer, engine, evaluator, stub.getVariablesTuple().getSize());
        Address<CachedFunctionEvaluatorNode> computer = Address.of(cfen);

        reteNet.connectRemoteNodes(stub.getHandle(), computer, true);

        Object[] newCalibrationElement = { computedResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(stub.getVariablesTuple(), newCalibrationElement);

        Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(stub,
                newCalibrationPattern, computer);

        return trace(result);
    }

    /**
     * @return trace(a buildable that potentially acts on a separate container
     */
    public Buildable<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>> getNextContainer() {
        return new ReteContainerBuildable<PatternDescription>(engine, reteNet.getNextContainer());
    }

    public Stub<Address<? extends Supplier>> buildScopeConstrainer(Stub<Address<? extends Supplier>> stub,
            boolean transitive, Object unwrappedContainer, int constrainedIndex) {
        Address<? extends Supplier> root = (transitive) ? boundary.accessContainmentTransitiveRoot() : boundary
                .accessContainmentRoot();
        // bind the container element
        Address<? extends Supplier> filteredRoot = targetContainer.getLibrary().accessValueBinderFilterNode(root,
                0/* container */, boundary.wrapElement(unwrappedContainer));
        // build secondary indexer
        int[] secondaryIndices = { 1 /* contained element */};
        Address<? extends Indexer> secondary = targetContainer.getLibrary().accessProjectionIndexer(filteredRoot,
                new TupleMask(secondaryIndices, 2));
        // build primary indexer
        int[] primaryIndices = { constrainedIndex };
        TupleMask primaryMask = new TupleMask(primaryIndices, stub.getVariablesTuple().getSize());
        Address<? extends IterableIndexer> primary = targetContainer.getLibrary().accessProjectionIndexer(
                stub.getHandle(), primaryMask);
        // build checker
        stub = new Stub<Address<? extends Supplier>>(stub, targetContainer.getLibrary().accessExistenceNode(primary,
                secondary, false));
        return trace(stub);
    }

    public Address<? extends Receiver> patternCollector(PatternDescription pattern) throws RetePatternBuildException {
        return engine.getBoundary().createProductionInternal(pattern);
    }

    /**
     * No need to distinguish
     */
    public Buildable<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>> putOnTab(PatternDescription effort, IPatternMatcherContext<PatternDescription> effortContext) {
    	final ReteContainerBuildable<PatternDescription> patternSpecific;
    	try {
    		patternSpecific = (ReteContainerBuildable<PatternDescription>) this.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
    	patternSpecific.pattern = effort;
    	patternSpecific.context = effortContext;
        return patternSpecific;
    }
    
    private Stub<Address<? extends Supplier>> trace(Stub<Address<? extends Supplier>> stub) {
    	NodeToStubTraceInfo<PatternDescription> traceInfo = new NodeToStubTraceInfo<PatternDescription>(stub, pattern, context);
    	final Address<? extends Supplier> address = stub.getHandle();
    	address.getContainer().resolveLocal(address).assignTraceInfo(traceInfo);
    	return stub;
    }

}
