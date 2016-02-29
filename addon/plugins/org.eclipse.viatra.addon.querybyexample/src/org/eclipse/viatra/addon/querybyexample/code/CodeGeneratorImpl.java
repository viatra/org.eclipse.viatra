/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.code;

import java.util.Map;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.interfaces.ICodeGenerator;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPattern;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;

public class CodeGeneratorImpl implements ICodeGenerator {

    private VariableRegister variableRegister;

    public CodeGeneratorImpl(VariableRegister register) {
        this.variableRegister = register;
    }

    @Override
    public String generateConstraint(VQLConstraint eiqc) {
        return eiqc.getStart().eClass().getName() + "." + eiqc.getReference().getName() + "("
                + variableRegister.getVariableSetting(eiqc.getStart()).getVariableName() + ", "
                + variableRegister.getVariableSetting(eiqc.getEnd()).getVariableName() + ")";
    }

    @Override
    public String generateNegConstraintFind(VQLNegConstraint eiqc) {
        String startParam = variableRegister.getVariableSetting(eiqc.getStart()).getVariableName();
        String endParam = variableRegister.getVariableSetting(eiqc.getEnd()).getVariableName();
        return "neg find " + eiqc.getHelperPatternName() + "(" + startParam + ", " + endParam + ")";
    }

    @Override
    public String generateNegConstraintHelperPattern(VQLNegConstraint eiqc) {
        String startParam = variableRegister.getVariableSetting(eiqc.getStart()).getVariableName();
        String endParam = variableRegister.getVariableSetting(eiqc.getEnd()).getVariableName();
        String startParamLine = "\t" + startParam + " : " + eiqc.getStart().eClass().getName() + ",\n";
        String endParamLine = "\t" + endParam + " : " + eiqc.getEnd().eClass().getName() + "\n";

        StringBuilder sb = new StringBuilder();
        sb.append("@QueryExplorer(checked = " + Boolean.toString(eiqc.isQueryExplorerChecked()) + ")\n");
        sb.append("pattern " + eiqc.getHelperPatternName() + "(\n");
        sb.append(startParamLine);
        sb.append(endParamLine);
        sb.append(") {\n");
        sb.append("\t" + eiqc.getStart().eClass().getName() + "." + eiqc.getReference().getName() + "(" + startParam
                + ", " + endParam + ");\n");
        sb.append("}\n");

        return sb.toString();
    }

    @Override
    public String generateVariable(EObject eo) {
        VQLVariableSetting variableSetting = variableRegister.getVariableSetting(eo);
        String eClassName = variableSetting.getType().getName();
        String varName = variableSetting.getVariableName();
        return variableSetting.isInputVariable() == true ? varName + " : " + eClassName
                : eClassName + "(" + varName + ")";
    }

    @Override
    public String generateVariable(VQLVariableSetting variableSetting) {
        String eClassName = variableSetting.getType().getName();
        String varName = variableSetting.getVariableName();
        return variableSetting.isInputVariable() == true ? varName + " : " + eClassName
                : eClassName + "(" + varName + ")";
    }

    @Override
    public String generatePattern(VQLPattern pattern) {

        if (!pattern.validate())
            return "";

        // EIQ PATTERN INIT
        // first row: package name
        StringBuilder patternBuilder = new StringBuilder("package ");
        patternBuilder.append(pattern.getPackageName());

        // second row: importing
        patternBuilder.append("\n\nimport \"");
        patternBuilder.append(pattern.getNsUri());
        patternBuilder.append("\"\n\n");

        // EIQ PATTERN HEAD
        patternBuilder.append("pattern ");
        patternBuilder.append(pattern.getPatternName());
        patternBuilder.append("(\n");

        // appending input parameters
        for (EObject selectedEObject : pattern.getSelectedEObjects()) {
            patternBuilder.append("\t");
            patternBuilder.append(this.generateVariable(selectedEObject));
            patternBuilder.append(",\n");
        }
        // appending additional input parameters
        Map<EObject, VQLVariableSetting> freeVariables = variableRegister.getFreeVariables();
        for (VQLVariableSetting param : freeVariables.values()) {
            if (param.isInputVariable()) {
                patternBuilder.append("\t");
                patternBuilder.append(this.generateVariable(param));
                patternBuilder.append(",\n");
            }
        }

        // the last comma is not necessary, so deleting it
        patternBuilder.deleteCharAt(patternBuilder.length() - 1);
        patternBuilder.deleteCharAt(patternBuilder.length() - 1);
        patternBuilder.append("\n) {\n");

        // EIQ PATTERN BODY
        // appending variable constraints
        for (VQLVariableSetting param : freeVariables.values()) {
            if (!param.isInputVariable() && param.isVisible()) {
                patternBuilder.append("\t");
                patternBuilder.append(this.generateVariable(param));
                patternBuilder.append(";\n");
            }
        }

        // appending constraints
        for (VQLConstraint actualConstraint : pattern.getConstraints()) {
            if (actualConstraint.isVisible()) {
                patternBuilder.append("\t");
                patternBuilder.append(this.generateConstraint(actualConstraint));
                patternBuilder.append(";\n");
            }
        }

        // default pattern body: if constraint list is empty, then one plus "class constraint" from selected objects
        if (pattern.getConstraints().isEmpty() && pattern.getDiscoveredEObjects().isEmpty()) {
            // workaround for default constraint in body
            patternBuilder.append("\t");
            EObject eo = pattern.getSelectedEObjects().iterator().next();
            VQLVariableSetting pseudoEIQSetting = new VQLVariableSetting();
            pseudoEIQSetting.setInputVariable(false);
            pseudoEIQSetting.setVariableName(variableRegister.getVariableSetting(eo).getVariableName());
            pseudoEIQSetting.setType(eo.eClass());
            patternBuilder.append(this.generateVariable(pseudoEIQSetting));
            patternBuilder.append(";\n");
        }

        // generating attributes
        boolean attributeGenerated = false;
        for (VQLAttribute eiqAttribute : pattern.getAttributes()) {
            if (!eiqAttribute.isVisible())
                continue;
            attributeGenerated = true;
            patternBuilder.append("\n\t");
            patternBuilder.append(this.generateAttribute(eiqAttribute));
            patternBuilder.append(";");
        }
        for (VQLAttribute eiqAttribute : pattern.getDiscoveredObjectsAttributes()) {
            if (!eiqAttribute.isVisible())
                continue;
            attributeGenerated = true;
            patternBuilder.append("\n\t");
            patternBuilder.append(this.generateAttribute(eiqAttribute));
            patternBuilder.append(";");
        }
        if (attributeGenerated)
            patternBuilder.append("\n");

        // generating negative constraints
        if (pattern.getNegConstraints().isEmpty()) {
            patternBuilder.append("}\n");
        } else {
            patternBuilder.append("\n");

            // appending negative constraints ('neg find' calls)
            for (VQLNegConstraint actualNegConstraint : pattern.getNegConstraints()) {
                if (actualNegConstraint.isVisible()) {
                    patternBuilder.append("\t");
                    patternBuilder.append(this.generateNegConstraintFind(actualNegConstraint));
                    patternBuilder.append(";\n");
                }
            }

            patternBuilder.append("}\n\n");

            // appending negative constraints ('helper patterns' calls)
            for (VQLNegConstraint actualNegConstraint : pattern.getNegConstraints()) {
                if (actualNegConstraint.isVisible()) {
                    patternBuilder.append(this.generateNegConstraintHelperPattern(actualNegConstraint));
                    patternBuilder.append("\n");
                }
            }
        }

        return patternBuilder.toString();
    }

    @Override
    public String generatePathLabel(VQLPath path) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.variableRegister.getFixVariables().get(path.getStart()).getVariableName());
        builder.append(" -> ");
        builder.append(this.variableRegister.getFixVariables().get(path.getEnd()).getVariableName());
        return builder.toString();
    }

    @Override
    public String generateAttribute(VQLAttribute attribute) {
        StringBuilder ret = new StringBuilder(attribute.getOwner().eClass().getName());
        ret.append(".");
        ret.append(attribute.getAttribute().getName());
        ret.append("(");
        ret.append(variableRegister.getVariableSetting(attribute.getOwner()).getVariableName());
        ret.append(", ");
        ret.append(this.generateLiteralForAttribute(attribute.getValue()));
        ret.append(")");
        return ret.toString();
    }

    @Override
    public String generateLiteralForAttribute(Object literal) {
        StringBuilder ret = new StringBuilder();

        if (literal instanceof String) { // String literal
            ret.append("\"");
            ret.append(literal.toString());
            ret.append("\"");
        } else if (literal instanceof Enumerator) { // Enum literal
            ret.append(literal.getClass().getSimpleName());
            ret.append("::");
            ret.append(literal.toString());
        } else if (literal instanceof Character) { // char literal
            ret.append("eval(\"");
            ret.append(literal.toString());
            ret.append("\".charAt(0))");
        } else { // default literal
            ret.append(literal.toString());
        }
        return ret.toString();
    }
}
