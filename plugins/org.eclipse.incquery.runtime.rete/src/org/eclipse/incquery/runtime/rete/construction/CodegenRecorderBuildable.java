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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;
import org.eclipse.incquery.runtime.rete.tuple.LeftInheritanceTuple;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * Lightweight class that generates Java code of a builder method from the build actions. Code is sent to a coordinator
 * to be collected in string buffers there.
 * 
 * @author Gabor Bergmann
 */
public abstract class CodegenRecorderBuildable<PatternDescription> implements
		IOperationCompiler<PatternDescription, String> {
    public CodegenRecordingCoordinator<PatternDescription> coordinator;
    public PatternDescription effort;
    public String myName;
    public String baseName;
    public String indent;
    private Map<SubPlan, String> planMapping = new HashMap<SubPlan, String>();

    protected void mapPlan(SubPlan plan, String handle) {
        planMapping.put(plan, handle);
    }
    
    protected String getHandle(SubPlan plan) {
        return planMapping.get(plan);
    }
    
    /**
     * @param code
     * @param indent
     * @param myName
     */
    public CodegenRecorderBuildable(CodegenRecordingCoordinator<PatternDescription> coordinator,
            PatternDescription effort, String indent, String baseName, String instanceSuffix) {
        super();
        this.coordinator = coordinator;
        this.effort = effort;
        this.indent = indent;
        this.baseName = baseName;
        this.myName = baseName + instanceSuffix;
    }

    public void reinitialize() {
        throw new UnsupportedOperationException();
    }

    protected String prettyPrintStringArray(String[] elements, String separator) {
        if (elements.length == 0)
            return "";
        else {
            StringBuilder result = new StringBuilder(elements[0]);
            for (int i = 1; i < elements.length; ++i) {
                result.append(", ");
                result.append(elements[i]);
            }
            return result.toString();
        }
    }

    protected String prettyPrintStringArray(String[] elements) {
        return prettyPrintStringArray(elements, ", ");
    }

    protected String prettyPrintIntArray(int[] elements, String separator) {
        if (elements.length == 0)
            return "";
        else {
            StringBuilder result = new StringBuilder();
            result.append(elements[0]);
            for (int i = 1; i < elements.length; ++i) {
                result.append(", ");
                result.append(elements[i]);
            }
            return result.toString();
        }
    }

    protected String prettyPrintIntArray(int[] elements) {
        return prettyPrintIntArray(elements, ", ");
    }

    protected String prettyPrintObjectArray(Object[] elements, String separator, boolean strict) {
        if (elements.length == 0)
            return "";
        else {
            StringBuilder result = new StringBuilder(gen(elements[0], strict));
            for (int i = 1; i < elements.length; ++i) {
                result.append(separator);
                result.append(gen(elements[i], strict));
            }
            return result.toString();
        }
    }

    protected String prettyPrintObjectArray(Object[] constantValues, boolean strict) {
        return prettyPrintObjectArray(constantValues, ", ", strict);
    }

    protected void emitLine(String line) {
        coordinator.emitPatternBuilderLine(effort, indent, line);
    }

    protected String call(String methodName, String arguments) {
        return (myName + "." + methodName + "(" + arguments + ")");
    }

    protected String call(String methodName, String[] arguments) {
        return call(methodName, prettyPrintStringArray(arguments));
    }

    protected String emitFunctionCall(String resultType, String methodName, String arguments) {
        return declareNewValue(resultType, call(methodName, arguments));
    }

    protected String emitFunctionCall(String resultType, String methodName, String[] arguments) {
        return declareNewValue(resultType, call(methodName, arguments));
    }

    protected void emitProcedureCall(String methodName, String arguments) {
        emitLine(call(methodName, arguments) + ";");
    }

    protected void emitProcedureCall(String methodName, String[] arguments) {
        emitLine(call(methodName, arguments) + ";");
    }

    protected void declareNew(String type, String identifier, String value, boolean isFinal) {
        emitLine((isFinal ? "final " : "") + type + " " + identifier + " = " + value + ";");
    }

    protected String declareNewValue(String type, String value) {
        String name = coordinator.newVariableIdentifier();
        declareNew(type, name, value, true);
        return name;
    }

    protected String declareNewBuildable(String value) {
        String name = coordinator.newBuildableIdentifier();
        declareNew(coordinator.buildableType, name, value, true);
        return name;
    }

    protected String gen(boolean bool) {
        return bool ? "true" : "false";
    }

    protected String gen(Integer integer) {
        return integer == null ? "null" : integer.toString();
    }

    protected String gen(int[] ints) {
        // return declareNewValue("int[]", "{"+prettyPrintIntArray(ints)+"}");
        return "new int[] {" + prettyPrintIntArray(ints) + "}";
    }

    protected String gen(TupleMask mask) {
        return declareNewValue("TupleMask", "new TupleMask(" + gen(mask.indices) + ", " + gen(mask.sourceWidth) + ")");
    }

    protected String gen(Object o, boolean strict) {
        if (o instanceof Number)
            return o.toString();
        if (o instanceof String)
            return "\"" + o.toString() + "\"";
        if (!strict)
            return "\"" + o.toString() + "\"";
        throw new UnsupportedOperationException("Cannot currently generate code from an " + o.getClass()
                + " instance: " + o.toString());
    }

    protected String gen(Object[] o, boolean strict) {
        // return declareNewValue("Object[]", "{"+prettyPrintObjectArray(o, strict)+"}");
        return "new Object[] {" + prettyPrintObjectArray(o, strict) + "}";
    }

    protected String gen(Tuple tuple, boolean strict) {
        return "new FlatTuple(" + gen(tuple.getElements(), strict) + ")";
    }

    public String genCalibrationElement(Object calibrationElement) {
        return gen(calibrationElement, false);// calibrationElement.toString();
    }

    // public String genUnaryType(Object type) {
    // return type==null? "null" : "context.retrieveUnaryType(\"" + coordinator.targetContext.retrieveUnaryTypeFQN(type)
    // + "\")";
    // }
    //
    // public String genTernaryEdgeType(Object type) {
    // return type==null? "null" : "context.retrieveTernaryEdgeType(\"" +
    // coordinator.targetContext.retrieveBinaryEdgeTypeFQN(type) + "\")";
    //
    // }
    // public String genBinaryEdgeType(Object type) {
    // return type==null? "null" : "context.retrieveBinaryEdgeType(\"" +
    // coordinator.targetContext.retrieveTernaryEdgeTypeFQN(type) + "\")";
    // }

    public abstract String genUnaryType(Object type);

    public abstract String genTernaryEdgeType(Object type);

    public abstract String genBinaryEdgeType(Object type);

    public abstract String genPattern(Object desc);

    // public abstract String genPosMap(PatternDescription desc);

    public String declareNextContainerVariable() {
        return declareNewBuildable(call("getNextContainer", ""));
    }

    // public String declarePutOnTabVariable(PatternDescription effort) {
    // return declareNewBuildable(call("putOnTab", genPattern(effort)));
    // }
    // //////////////////////////////////
    // * BL
    // //////////////////////////////////

    public SubPlan buildBetaNode(SubPlan primaryPlan, SubPlan sidePlan, TupleMask primaryMask,
            TupleMask sideMask, TupleMask complementer, boolean negative) {
        String[] arguments = { getHandle(primaryPlan), getHandle(sidePlan), gen(primaryMask), gen(sideMask), gen(complementer),
                gen(negative) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildBetaNode", arguments);

        SubPlan subPlan;
        if (negative) {
			subPlan = new SubPlan(primaryPlan);
        } else {
            Tuple newCalibrationPattern = negative ? primaryPlan.getVariablesTuple() : complementer.combine(
                    primaryPlan.getVariablesTuple(), sidePlan.getVariablesTuple(), Options.enableInheritance, true);

            subPlan = new SubPlan(primaryPlan, sidePlan, newCalibrationPattern);
        }
        mapPlan(subPlan, resultVar);
        return subPlan;
    }

    public SubPlan buildCountCheckBetaNode(SubPlan primaryPlan, SubPlan sidePlan, TupleMask primaryMask,
            TupleMask originalSideMask, int resultPositionInSignature) {
        String[] arguments = { getHandle(primaryPlan), getHandle(sidePlan), gen(primaryMask), gen(originalSideMask),
                gen(resultPositionInSignature) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildCountCheckBetaNode", arguments);

        SubPlan subPlan = new SubPlan(primaryPlan, primaryPlan.getVariablesTuple());
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan buildCounterBetaNode(SubPlan primaryPlan, SubPlan sidePlan, TupleMask primaryMask,
            TupleMask originalSideMask, TupleMask complementer, Object aggregateResultCalibrationElement) {
        String[] arguments = { getHandle(primaryPlan), getHandle(sidePlan), gen(primaryMask), gen(originalSideMask),
                gen(complementer), genCalibrationElement(aggregateResultCalibrationElement) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildCounterBetaNode", arguments);

        Object[] newCalibrationElement = { aggregateResultCalibrationElement };
        Tuple newCalibrationPattern = new LeftInheritanceTuple(primaryPlan.getVariablesTuple(), newCalibrationElement);

        SubPlan subPlan = new SubPlan(primaryPlan, newCalibrationPattern);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public void buildConnection(SubPlan parentPlan, String collector) {
        String[] arguments = { getHandle(parentPlan), collector };
        emitProcedureCall("buildConnection", arguments);
    }

    public SubPlan buildEqualityChecker(SubPlan parentPlan, int[] indices) {
        String[] arguments = { getHandle(parentPlan), gen(indices) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildEqualityChecker", arguments);
        SubPlan subPlan = new SubPlan(parentPlan);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan buildInjectivityChecker(SubPlan parentPlan, int subject, int[] inequalIndices) {
        String[] arguments = { getHandle(parentPlan), gen(subject), gen(inequalIndices) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildInjectivityChecker", arguments);
        SubPlan subPlan = new SubPlan(parentPlan);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    @Override
    public SubPlan buildTransitiveClosure(SubPlan parentPlan) {
        String[] arguments = { getHandle(parentPlan) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildTransitiveClosure", arguments);
        SubPlan subPlan = new SubPlan(parentPlan);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan buildScopeConstrainer(SubPlan parentPlan, boolean transitive, Object unwrappedContainer,
            int constrainedIndex) {
        throw new UnsupportedOperationException("Code generation does not support external scoping as of now");
    }

    public SubPlan buildStartingPlan(Object[] constantValues, Object[] constantNames) {
        String[] arguments = { gen(constantValues, true), gen(constantNames, false), };
        String resultVar = emitFunctionCall(coordinator.planType, "buildStartPlan", arguments);
        SubPlan subPlan = new SubPlan(new FlatTuple(constantNames));
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan buildTrimmer(SubPlan parentPlan, TupleMask trimMask, boolean enforceUniqueness) {
        String[] arguments = { getHandle(parentPlan), gen(trimMask), gen(enforceUniqueness) };
        String resultVar = emitFunctionCall(coordinator.planType, "buildTrimmer", arguments);
        SubPlan subPlan = new SubPlan(parentPlan, trimMask.transform(parentPlan.getVariablesTuple()));
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan directContainmentPlan(Tuple nodes) {
        String[] arguments = { gen(nodes, false) };
        String resultVar = emitFunctionCall(coordinator.planType, "containmentDirectPlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan transitiveContainmentPlan(Tuple nodes) {
        String[] arguments = { gen(nodes, false) };
        String resultVar = emitFunctionCall(coordinator.planType, "containmentTransitivePlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan unaryTypePlan(Tuple nodes, Object supplierKey) {
        String[] arguments = { gen(nodes, false), declareNewValue("Object", genUnaryType(supplierKey)) };
        String resultVar = emitFunctionCall(coordinator.planType, "unaryTypePlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan directGeneralizationPlan(Tuple nodes) {
        String[] arguments = { gen(nodes, false) };
        String resultVar = emitFunctionCall(coordinator.planType, "generalizationDirectPlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan transitiveGeneralizationPlan(Tuple nodes) {
        String[] arguments = { gen(nodes, false) };
        String resultVar = emitFunctionCall(coordinator.planType, "generalizationTransitivePlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan directInstantiationPlan(Tuple nodes) {
        String[] arguments = { gen(nodes, false) };
        String resultVar = emitFunctionCall(coordinator.planType, "instantiationDirectPlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan transitiveInstantiationPlan(Tuple nodes) {
        String[] arguments = { gen(nodes, false) };
        String resultVar = emitFunctionCall(coordinator.planType, "instantiationTransitivePlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan patternCallPlan(Tuple nodes, Object supplierKey) {
        // if (!coordinator.collectors.containsKey(supplierKey)) coordinator.unbuilt.add(supplierKey);
        String[] arguments = { gen(nodes, false), genPattern(supplierKey) };
        String resultVar = emitFunctionCall(coordinator.planType, "patternCallPlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan binaryEdgeTypePlan(Tuple nodes, Object supplierKey) {
        String[] arguments = { gen(nodes, false), declareNewValue("Object", genBinaryEdgeType(supplierKey)) };
        String resultVar = emitFunctionCall(coordinator.planType, "binaryEdgeTypePlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    public SubPlan ternaryEdgeTypePlan(Tuple nodes, Object supplierKey) {
        String[] arguments = { gen(nodes, false), declareNewValue("Object", genTernaryEdgeType(supplierKey)) };
        String resultVar = emitFunctionCall(coordinator.planType, "ternaryEdgeTypePlan", arguments);
        SubPlan subPlan = new SubPlan(nodes);
        mapPlan(subPlan, resultVar);
		return subPlan;
    }

    @Override
    public String patternCollector(PatternDescription pattern) {
        String patternName = genPattern(pattern);
        String[] arguments = { patternName };
        return emitFunctionCall(coordinator.collectorType, "patternCollector", arguments);
        // return coordinator.allocateNewCollector(pattern);
    }
    
    @Override
    public void patternFinished(PatternDescription pattern, IPatternMatcherContext context, String collector) {
    	// NO-OP
    }


    // /**
    // * @pre coordinator.isComplete()
    // */
    // public void printInitializer(String collectorsMap, String posMappingMap) {
    // for (Entry<PatternDescription, String> entry : coordinator.collectors.entrySet()) {
    // String patternName = genPattern(entry.getKey());
    // emitLine("// "+patternName);
    // emitLine(posMappingMap + ".put(" + patternName + ", " +genPosMap(entry.getKey()) + ");");
    // String[] arguments = {patternName};
    // String resultVar = emitFunctionCall(coordinator.collectorType, "patternCollector", arguments);
    // emitLine(entry.getValue() + " = " + resultVar + ";");
    // emitLine(collectorsMap + ".put(" + patternName + ", " +entry.getValue() + ");");
    // }
    // }
}
